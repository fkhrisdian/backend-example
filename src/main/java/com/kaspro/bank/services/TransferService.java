package com.kaspro.bank.services;

import com.kaspro.bank.converter.OGPConverter;
import com.kaspro.bank.enums.StatusCode;
import com.kaspro.bank.exception.NostraException;
import com.kaspro.bank.persistance.domain.*;
import com.kaspro.bank.persistance.repository.*;
import com.kaspro.bank.util.InitDB;
import com.kaspro.bank.vo.*;
import com.kaspro.bank.vo.Individual.IndividualRegistrationVO;
import com.kaspro.bank.vo.Inquiry.InquiryKasproBankResVO;
import com.kaspro.bank.vo.TransferKasproBank.TransferKasproBankReqVO;
import com.kaspro.bank.vo.TransferKasproBank.TransferKasproBankResVO;
import com.kaspro.bank.vo.ogp.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class TransferService {

  @Autowired
  OGPService ogpService;

  @Autowired
  TransferService tService;

  @Autowired
  VirtualAccountRepository vaRepo;

  @Autowired
  PartnerMemberService pmService;

  @Autowired
  TransferLimitRepository tlRepo;

  @Autowired
  UsageAccumulatorRepository uaRepo;

  @Autowired
  TransactionHistoryRepository thRepo;

  @Autowired
  TransactionHistoryStagingRepository thSTGRepo;

  @Autowired
  PartnerService pService;

  @Autowired
  PartnerRepository pRepo;

  @Autowired
  HttpProcessingService httpProcessingService;

  @Autowired
  IndividualService iService;

  @Autowired
  OGPConverter ogpConverter;

  @Autowired
  IncreaseLimitService ilService;

  @Autowired
  RequestCardService rcService;

  Logger logger = LoggerFactory.getLogger(TransferService.class);

  public String balance(BalanceVO vo) {
    OgpBalanceRespVO balanceResponse = ogpService.balance(vo);
    return balanceResponse.getGetBalanceResponse().getParameters().getAccountBalance();
  }

  public String inHouseInquiry(InHouseInquiryVO vo) {
    OgpInHouseInquiryRespVO inHouseInquiryResponse = ogpService.inHouseInquiry(vo);
    return inHouseInquiryResponse.getGetInHouseInquiryResponse().getParameters().getCustomerName();
  }

  public String interBankInquiry(InterBankInquiryVO vo) {
    OgpInterBankInquiryRespVO interBankInquiryResponse = ogpService.interBankInquiry(vo);
    return interBankInquiryResponse.getGetInterbankInquiryResponse().getParameters().getDestinationAccountName();
  }

  public OgpInHousePaymentRespVO inHousePayment(InHousePaymentVO vo) {
    OgpInHousePaymentRespVO paymentRespVO = ogpService.inHousePayment(vo);
    return paymentRespVO;
  }

  public String interBankPayment(InterBankPaymentVO vo) {
    OgpInterBankPaymentRespVO paymentRespVO = ogpService.interBankPayment(vo);
    return paymentRespVO.getGetInterbankPaymentResponse().getParameters().getResponseCode();
  }

  public String paymentStatus(PaymentStatusVO vo) {
    OgpPaymentStatusRespVO paymentStatusRespVO = ogpService.paymentStatus(vo);
    return paymentStatusRespVO.getGetPaymentStatusResponse().getParameters().getPreviousResponse().getValueAmount();
  }

  public List<TransactionHistory> findEntireTransaction(){
    List<TransactionHistory> ths=thRepo.findEntireTransaction();
    return ths;
  }

  public List<TransactionHistory> findFilteredTransaction(String accType, String partnerId, String senderId, String msisdn, String tid, String startDate, String endDate){
    final String OLD_FORMAT = "dd-MM-yyyy";
    final String NEW_FORMAT = "yyyy-MM-dd";

// August 12, 2010
    String newStartDate=null;
    String newEndDate=null;

    SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
    Date d1 = null;
    Date d2 = null;
    try {
      if(startDate!=null){
        d1 = sdf.parse(startDate);
      }

      if(endDate!=null){
        d2 = sdf.parse(endDate);
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
    sdf.applyPattern(NEW_FORMAT);
    if(startDate!=null){
      newStartDate = sdf.format(d1)+" 00:00:00";
    }

    if(endDate!=null){
      newEndDate=sdf.format(d2)+" 23:59:59";
    }
    logger.info("Filtering Transaction History : "+accType+" "+partnerId+" "+senderId+" "+msisdn+" "+tid+" "+newStartDate+" "+newEndDate);

    List<TransactionHistory> ths=thRepo.findFilteredTransaction(accType, partnerId, senderId, msisdn, tid, newStartDate, newEndDate);
    return ths;
  }

  @Transactional
  public InquiryKasproBankResVO kasproBankInquiry(String source, String destination, String sku, String amount, String paymentMethod, String chargingModel, boolean isAdmin, String email, String address1, String address2){

    VirtualAccount va = new VirtualAccount();
    RegisterPartnerMemberVO pmVO = new RegisterPartnerMemberVO();
    InHouseInquiryVO ihi = new InHouseInquiryVO();
    OgpInHouseInquiryRespVO ihiResp = new OgpInHouseInquiryRespVO();
    TransactionHistoryStaging th=new TransactionHistoryStaging();
    InquiryKasproBankResVO vo = new InquiryKasproBankResVO();
    VirtualAccount vaSource=new VirtualAccount();
    String bankCode="";
    String bankCodeRTGS="";
    String bankName="";
    RegisterPartnerMemberVO pmVOSource = new RegisterPartnerMemberVO();
    RegisterPartnerVO pVOSource=new RegisterPartnerVO();
    IndividualRegistrationVO iVOSource=new IndividualRegistrationVO();
    IndividualRegistrationVO iVo= new IndividualRegistrationVO();
    paymentMethod=paymentMethod.toUpperCase();
    String tmpSKU=sku;
    InitDB x=InitDB.getInstance();
    String tier="";
    th.setInterBankFee("0");
    th.setSender(source);
    th.setDest(destination);
    if(source.startsWith("628")||source.startsWith("08")){
      if(source.startsWith("08")){
        source="62"+source.substring(1);
      }
      vaSource=vaRepo.findIndividual(source);
      if(vaSource!=null){
        iVOSource=iService.getIndividualDetail(vaSource.getOwnerID());
        if(!iVOSource.getIndividual().getStatus().equals("ACTIVE")){
          throw new NostraException("Source account is not active",StatusCode.ERROR);
        }
        th.setDebitName(iVOSource.getIndividual().getName());
        th.setAccType("I");
        vo.setSourceType("I");
        th.setSenderId(iVOSource.getIndividual().getId().toString());
        vo.setSourceName(iVOSource.getIndividual().getName());
        th.setMsisdn(iVOSource.getIndividual().getMsisdn());
        tier=iVOSource.getTransferLimits().get(0).getTierType();
        logger.info("Tier type : "+tier);
      }else{
        throw new NostraException("Source account not found", StatusCode.DATA_NOT_FOUND);
      }
    }else {
      vaSource=vaRepo.findCorporate(source);
      if(vaSource!=null){
        pmVOSource = pmService.findDetail(vaSource.getOwnerID());
        if(!pmVOSource.getPartnerMember().getStatus().equals("ACTIVE")){
          throw new NostraException("Source account is not active",StatusCode.ERROR);
        }
        pVOSource=pService.findDetail(pmVOSource.getPartnerMember().getPartner().getId());
        th.setDebitName(pmVOSource.getPartnerMember().getName());
        th.setPartnerId(pVOSource.getPartner().getId().toString());
        th.setPartnerName(pVOSource.getPartner().getName());
        th.setSenderId(pmVOSource.getPartnerMember().getId().toString());
        th.setAccType("CPM");
        vo.setSourceType("CPM");
        vo.setSourceName(pmVOSource.getPartnerMember().getName());
        for(TransferInfoMember tim:pmVOSource.getListTransferInfoMember()){
          if(tim.getName().equals("TierLimit")){
            tier=tim.getValue();
            logger.info("Tier type : "+tier);
            break;
          }
        }
      }else{
        throw new NostraException("Source account not found", StatusCode.DATA_NOT_FOUND);
      }
    }

    removeTimeout(Integer.toString(vaSource.getOwnerID()));

    TransactionHistoryStaging thSTG=thSTGRepo.findBySenderId(Integer.toString(vaSource.getOwnerID()));
    if(thSTG!=null){
      throw new NostraException("You have pending transcation with TID "+thSTG.getTid()+". Please finish the pending transaction.",StatusCode.ERROR);
    }

    if(sku.equals("KasproBank")){
      if(!paymentMethod.equalsIgnoreCase("ONLINE")){
        throw new NostraException("Transfer to KasproBank, Kaspro, BNI or BNI Syariah can only with ONLINE method", StatusCode.ERROR);
      }
      if(destination.startsWith("628")||destination.startsWith("08")){
        if(destination.startsWith("08")){
          destination="62"+destination.substring(1);
        }
        va=vaRepo.findIndividual(destination);
        if(va!=null){
          iVo=iService.getIndividualDetail(va.getOwnerID());
          if(!iVo.getIndividual().getStatus().equals("ACTIVE")){
            throw new NostraException("Destination account is not active",StatusCode.ERROR);
          }
//          th.setDebitName(iVo.getIndividual().getName());
          vo.setDestinationName(iVo.getIndividual().getName());
          th.setCreditName(iVo.getIndividual().getName());
        }
      }else{
        va=vaRepo.findCorporate(destination);
        if(va!=null){
          pmVO = pmService.findDetail(va.getOwnerID());
          logger.info(pmVO.toString());
          if(!pmVO.getPartnerMember().getStatus().equals("ACTIVE")){
            throw new NostraException("Destination account is not active",StatusCode.ERROR);
          }
//          th.setDebitName(pmVOSource.getPartnerMember().getName());
          vo.setDestinationName(pmVO.getPartnerMember().getName());
          th.setCreditName(pmVO.getPartnerMember().getName());
        }
      }
      if(va==null) {
        throw new NostraException("Destination account is not found", StatusCode.DATA_NOT_FOUND);
      }

      String type = "KasproBank";
      if(isNotLimit(type, amount)){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String currentTime = sdf.format(new Date());
        logger.info("Current Time is : "+currentTime);
        if(tService.isCutOff(currentTime,type)){
          throw new NostraException("Already Cut Off Time",StatusCode.ERROR);
        }
        vo.setDestinationAccount(va.getVa());


        th.setCreditAcc(va.getVa());
      }
    }
//    else if(sku.equals("BNI")){
//      if(!paymentMethod.equalsIgnoreCase("ONLINE")){
//        throw new NostraException("Transfer to KasproBank, Kaspro, BNI or BNI Syariah can only with ONLINE method");
//      }
//      ihi.setAccountNo(destination);
//      ihiResp=ogpService.inHouseInquiry(ihi);
//      if(!ihiResp.getGetInHouseInquiryResponse().getParameters().getResponseCode().equals("0001")){
//        throw new NostraException("Destination account is not found", StatusCode.DATA_NOT_FOUND);
//      }else{
//        vo.setDestinationAccount(ihiResp.getGetInHouseInquiryResponse().getParameters().getAccountNumber());
//        vo.setDestinationName(ihiResp.getGetInHouseInquiryResponse().getParameters().getCustomerName());
//        th.setCreditName(vo.getDestinationName());
//        th.setCreditAcc(ihiResp.getGetInHouseInquiryResponse().getParameters().getAccountNumber());
//      }
//    }
    else if(sku.equals("Kaspro")){
      if(!paymentMethod.equalsIgnoreCase("ONLINE")){
        throw new NostraException("Transfer to KasproBank, Kaspro, BNI or BNI Syariah can only with ONLINE method", StatusCode.ERROR);
      }
      try {
        JSONObject resValidate= new JSONObject(httpProcessingService.kasproValidate(destination));
        logger.info(resValidate.toString());
        if(resValidate.getInt("code")!=0){
          throw new NostraException(resValidate.getString("message"),StatusCode.ERROR);
        }else {
          JSONObject resPayu = new JSONObject(httpProcessingService.kasproPayu(resValidate.getString("account-number")));
          logger.info(resPayu.toString());
          if(resPayu.getInt("code")!=0){
            throw new NostraException(resPayu.getString("message"), StatusCode.ERROR);
          }else{
            String type="Kaspro";
            if(isNotLimit(type, amount)){
              SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
              String currentTime = sdf.format(new Date());
              logger.info("Current Time is : "+currentTime);
              if(tService.isCutOff(currentTime,type)){
                throw new NostraException("Already Cut Off Time",StatusCode.ERROR);
              }
              String accountName=resPayu.getJSONObject("account").getString("account-name");
              vo.setDestinationName(accountName);
              vo.setDestinationAccount(resValidate.getString("account-number"));
              th.setCreditName(vo.getDestinationName());
              logger.info("Account number: "+resValidate.getString("account-number"));
              th.setCreditAcc(resValidate.getString("account-number"));
            }
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }else{
      if(sku.equals("BNI")){
        if(!paymentMethod.equalsIgnoreCase("ONLINE")){
          throw new NostraException("Transfer to KasproBank, Kaspro, BNI or BNI Syariah can only with ONLINE method", StatusCode.ERROR);
        }
        ihi.setAccountNo(destination);
        ihiResp=ogpService.inHouseInquiry(ihi);
        if(!ihiResp.getGetInHouseInquiryResponse().getParameters().getResponseCode().equals("0001")){
          throw new NostraException("Destination account is not found", StatusCode.DATA_NOT_FOUND);
        }else{
          String type="BNI";
          if(isNotLimit(type, amount)){
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String currentTime = sdf.format(new Date());
            logger.info("Current Time is : "+currentTime);
            if(tService.isCutOff(currentTime,type)){
              throw new NostraException("Already Cut Off Time",StatusCode.ERROR);
            }
            vo.setDestinationAccount(ihiResp.getGetInHouseInquiryResponse().getParameters().getAccountNumber());
            vo.setDestinationName(ihiResp.getGetInHouseInquiryResponse().getParameters().getCustomerName());
            th.setCreditName(vo.getDestinationName());
            th.setCreditAcc(ihiResp.getGetInHouseInquiryResponse().getParameters().getAccountNumber());
          }
        }
      }else{
//        th.setInterBankFee(x.get("InterBank.Fee"));
        InterBankInquiryVO ibiVO=new InterBankInquiryVO();
        tmpSKU="OtherBank";
        OgpInterBankInquiryRespVO ibiResVO=new OgpInterBankInquiryRespVO();
        bankCode=x.get("Bank.Code."+sku);
        bankCodeRTGS=x.get("RTGS.Code."+sku);

        int maxChar = Integer.parseInt(x.get("MaxLength.Address"));

        ibiVO.setAccountNo(vaSource.getVa());
        ibiVO.setDestinationAccountNo(destination);
        ibiVO.setDestinationBankCode(bankCode);
        ibiResVO=ogpService.interBankInquiry(ibiVO);
        if(!ibiResVO.getGetInterbankInquiryResponse().getParameters().getResponseCode().equals("0001")){
          throw new NostraException("Error during Inter Bank Inquiry "+ibiResVO.getGetInterbankInquiryResponse().getParameters().getResponseCode()+". "+ibiResVO.getGetInterbankInquiryResponse().getParameters().getResponseMessage());
        }
        bankName=ibiResVO.getGetInterbankInquiryResponse().getParameters().getDestinationBankName();
        th.setCreditAcc(destination);
        th.setCreditName(ibiResVO.getGetInterbankInquiryResponse().getParameters().getDestinationAccountName());
        vo.setDestinationAccount(th.getCreditAcc());
        vo.setDestinationName(th.getCreditName());
        sku=bankName;
        if(paymentMethod.equalsIgnoreCase("ONLINE")){
          String type="OtherBank";
          if(isNotLimit(type, amount)){
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String currentTime = sdf.format(new Date());
            logger.info("Current Time is : "+currentTime);
            if(tService.isCutOff(currentTime,type)){
              throw new NostraException("Already Cut Off Time",StatusCode.ERROR);
            }
            th.setDestinationBankCode(bankCode);
            th.setInterBankFee(x.get("InterBank.Fee."+paymentMethod.toUpperCase()));
          }
        }else if(paymentMethod.equalsIgnoreCase("RTGS")){
          String type="RTGS";
          if(address1!=null && !address1.equalsIgnoreCase("")){
            if(address1.length()>maxChar){
              throw new NostraException("Address1 max length is "+maxChar, StatusCode.ERROR);
            }else {
              th.setAddress1(address1);
            }
          }else {
            throw new NostraException("Address 1 is mandatory for transfer RTGS/Kliring.", StatusCode.ERROR);
          }
          if(address2!=null){
            if(address2.length()>maxChar){
              throw new NostraException("Address2 max length is "+maxChar, StatusCode.ERROR);
            }else {
              th.setAddress2(address2);
            }
          }
          if(email!=null){
            th.setEmail(email);
          }
          if(isNotLimit(type,amount)){
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String currentTime = sdf.format(new Date());
            logger.info("Current Time is : "+currentTime);
            if(tService.isCutOff(currentTime,type)){
              throw new NostraException("Already Cut Off Time",StatusCode.ERROR);
            }
            th.setDestinationBankCode(bankCodeRTGS);
            th.setInterBankFee(x.get("InterBank.Fee."+paymentMethod.toUpperCase()));
          }
        }else if(paymentMethod.equalsIgnoreCase("KLIRING")){
          String type="Kliring";
          if(address1!=null && !address1.equalsIgnoreCase("")){
            if(address1.length()>maxChar){
              throw new NostraException("Address1 max length is "+maxChar, StatusCode.ERROR);
            }else {
              th.setAddress1(address1);
            }
          }else {
            throw new NostraException("Address 1 is mandatory for transfer RTGS/Kliring.", StatusCode.ERROR);
          }
          if(address2!=null){
            if(address2.length()>maxChar){
              throw new NostraException("Address2 max length is "+maxChar, StatusCode.ERROR);
            }else {
              th.setAddress2(address2);
            }
          }
          if(email!=null){
            th.setEmail(email);
          }
          if(isNotLimit(type,amount)){
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String currentTime = sdf.format(new Date());
            logger.info("Current Time is : "+currentTime);
            if(tService.isCutOff(currentTime,type)){
              throw new NostraException("Already Cut Off Time",StatusCode.ERROR);
            }
            th.setDestinationBankCode(bankCodeRTGS);
            th.setInterBankFee(x.get("InterBank.Fee."+paymentMethod.toUpperCase()));
          }
        }else{
          throw new NostraException("Invalid payment method",StatusCode.ERROR);
        }
      }
    }

    logger.info("Access = Access"+tmpSKU);
    logger.info("Setting transfer fee starts");
    String fee="";
    boolean invoice=false;

    if(vaSource.getFlag().equals("I")){
      th.setFeeMethod("transaction");
      if(sku.equals("KasproBank")){
        fee=x.get("Individual.Fee.KasproBank");
      }else if(sku.equals("Kaspro")){
        fee=x.get("Individual.Fee.Kaspro");
      }else if(sku.equals("BNI")){
        fee=x.get("Individual.Fee.BNI");
      }else{
        fee=x.get("Individual.Fee.OtherBank");
      }
    }
    else {
      for(TransferInfoMember tif:pmVOSource.getListTransferInfoMember()){
        if(tif.getName().equals("PaymentFeeMethod")){
          th.setFeeMethod(tif.getValue());
          if(tif.getValue().equals("invoice")){
            fee="0";
            invoice=true;
          }
        }else if(tif.getName().equalsIgnoreCase("Access"+tmpSKU)){
          if(tif.getValue().equalsIgnoreCase("off")){
            throw new NostraException(tif.getName()+" is "+tif.getValue(),StatusCode.ERROR);
          }
        }
      }
      if(!invoice){
        for (TransferFee tf : pVOSource.getTransferFees()) {
          if (tf.getDestination().equals(tmpSKU)) {
            fee = tf.getFee().toString().replaceAll("\\.0*$", "");
            break;
          }
        }
      }
    }
    if(isAdmin){
      if(Long.parseLong(amount)>Long.parseLong(fee)){
        throw new NostraException("If isAdmin=true then transfer amount must less than equal transfer fee",StatusCode.ERROR);
      }
      fee="0";
    }
    logger.info("Transfer Fee = "+fee);
    logger.info("Setting transfer fee end");



    TransferLimit tl = tlRepo.findByTierAndDest(tier,tmpSKU);
    log.info("TL : "+tl.toString());
    UsageAccumulator ua = uaRepo.findByOwnerIDAndDest(vaSource.getOwnerID(),tmpSKU);
    log.info("UA : "+ua.toString());
    Long usage=Long.parseLong(ua.getUsage())+Long.parseLong(amount);
    Long totalAmount=Long.parseLong(amount)+Long.parseLong(fee);

    if(usage>Long.parseLong(tl.getTransactionLimit())){
      log.info("VA Source type is : "+vaSource.getFlag());
      String additionalLimit="";
      if(vaSource.getFlag().equalsIgnoreCase("CPM")){
        ilService.checkIncreaseLimitResVO(pmVOSource.getPartnerMember().getId().toString(), tmpSKU);
      }
      if(additionalLimit.equalsIgnoreCase("")){
        throw new NostraException("Transfer Limit is exceeded", StatusCode.ERROR);
      }else {
        Long additional=Long.parseLong(additionalLimit);
        additional=additional+Long.parseLong(tl.getTransactionLimit());
        logger.info("Total limit after additional limit is "+additional);
        if(usage>additional){
          throw new NostraException("Transfer Limit is exceeded", StatusCode.ERROR);
        }
      }

    }
    java.util.Date currentTime = new java.util.Date();
    String referenceNumber = ogpService.getCustomerReferenceNumber(currentTime,vaSource.getVa());
    th.setAmount(amount);
    th.setAdminFee(fee);
    th.setTotalAmount(totalAmount.toString());
    th.setCurrency("IDR");
    th.setDebitAcc(vaSource.getVa());
    th.setStatus("Pending");
    th.setRemark("Pending for confirmation");
    th.setTid(referenceNumber);
    th.setSku(sku);
    th.setPaymentMethod(paymentMethod);
    th.setChargingModelId(chargingModel);
    th.setInvoice(invoice);
    TransactionHistoryStaging savedTH=thSTGRepo.save(th);

    Long tmpTotal = Long.valueOf(th.getTotalAmount());
    Long tmpBankFee=Long.valueOf(th.getInterBankFee());
    Long ibf = tmpTotal+tmpBankFee;

    vo.setAmount(amount);
    vo.setSourceAccount(vaSource.getVa());
//    vo.setSourceName(pmVOSource.getPartnerMember().getName());
    vo.setTid(savedTH.getTid());
    vo.setAdminFee(savedTH.getAdminFee());
    vo.setInterBankFee(savedTH.getInterBankFee());
    vo.setTotalAmount(ibf.toString());

    return vo;

  }

  @Transactional
  public void removeTimeout(String id){
    InitDB initDB=InitDB.getInstance();
    String timeout = initDB.get("Timeout.Transaction");
    TransactionHistoryStaging thg = thSTGRepo.findTimeout(id, timeout);

    if(thg!=null){
      cancelTID("Timeout", thg.getTid());
      log.info(thg.getTid()+" is cancelled due to timeout");
    }
  }

  public boolean isCutOff(String currentTime, String type){
    try {
      InitDB initDB=InitDB.getInstance();
      String cutoffStart = initDB.get("CutOff."+type+".Start");
      String cutoffEnd = initDB.get("CutOff."+type+".End");
      if(cutoffStart.equalsIgnoreCase("NA")||cutoffEnd.equalsIgnoreCase("NA")){
        return false;
      }else {
        Date time1 = new SimpleDateFormat("HH:mm:ss").parse(cutoffStart);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(time1);
        calendar1.add(Calendar.DATE, 1);
        System.out.println(calendar1.getTime());

        Date time2 = new SimpleDateFormat("HH:mm:ss").parse(cutoffEnd);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(time2);
        calendar2.add(Calendar.DATE, 1);
        System.out.println(calendar2.getTime());

        Date d = new SimpleDateFormat("HH:mm:ss").parse(currentTime);
        Calendar calendar3 = Calendar.getInstance();
        calendar3.setTime(d);
        calendar3.add(Calendar.DATE, 1);
        System.out.println(calendar3.getTime());

        Date x = calendar3.getTime();
        if (x.before(calendar1.getTime()) && x.after(calendar2.getTime())) {
          return false;
        }else{
          return true;
        }
      }
    } catch (ParseException e) {
      e.printStackTrace();
      return true;
    }
  }

  public boolean isNotLimit(String sku, String amount){
    InitDB initDB=InitDB.getInstance();
    String limitMin=initDB.get(sku+".Min.Limit");
    String limitMax=initDB.get(sku+".Max.Limit");

    if (!limitMin.equalsIgnoreCase("")&&!limitMin.equalsIgnoreCase("NA")){
      Long min=Long.parseLong(limitMin);
      Long amt=Long.parseLong(amount);
      if(amt>=min){
        return true;
      }else {
        throw new NostraException("Minimum amount for SKU "+sku+" is "+limitMin);
      }
    }
    if (!limitMax.equalsIgnoreCase("")&&!limitMax.equalsIgnoreCase("NA")) {
      Long max = Long.parseLong(limitMax);
      Long amt = Long.parseLong(amount);
      if (amt <= max) {
        return true;
      } else {
        throw new NostraException("Maximum amount for SKU "+sku+" is "+limitMax);
      }
    }
    return true;
  }

  public OgpInHousePaymentRespVO transferKasproBank(TransferKasproBankReqVO vo){
    TransactionHistoryStaging thSTG=thSTGRepo.findByTID(vo.getTid(),"KasproBank");
    TransactionHistory th = new TransactionHistory();
    if(thSTG==null){
      throw new NostraException(vo.getTid()+" Transaction ID is not found",StatusCode.DATA_NOT_FOUND);
    }else {
      thSTGRepo.delete(thSTG);
      thSTGRepo.flush();
      th=ogpConverter.convertTransactionHistory(thSTG);
    }
    BalanceVO balanceVO=new BalanceVO();
    balanceVO.setAccountNo(th.getDebitAcc());
    if(!isSuficientBalance(th, balanceVO,th.getTotalAmount(),th.getPaymentMethod(), th.getSku())){
      th.setStatus("Error");
      th.setRemark("Insuficient Balance");
      thRepo.save(th);
      throw new NostraException(vo.getTid()+" Insuficient Balance",StatusCode.ERROR);
    }
    InitDB x  = InitDB.getInstance();
    String method=x.get("Code.PaymentMethod."+th.getPaymentMethod());
    String escrow=x.get("Account.Escrow");
//    String feeAccount="0115476151";

    OgpInHousePaymentRespVO resIHPDest = new OgpInHousePaymentRespVO();

    try{
      logger.info(vo.getTid()+" Starting transfer to escrow account: "+escrow+" with amount: "+th.getTotalAmount());
      InHousePaymentVO ihpVOEscrow=new InHousePaymentVO();
      ihpVOEscrow.setAmount(th.getAmount());
      ihpVOEscrow.setDebitAccountNo(th.getDebitAcc());
      ihpVOEscrow.setCreditAccountNo(escrow);
      ihpVOEscrow.setChargingModelId(th.getChargingModelId());
      ihpVOEscrow.setPaymentMethod(method);
      ihpVOEscrow.setRemark("Transfer from source account to escrow account");
      OgpInHousePaymentRespVO resIHPEscrow=inHousePayment(ihpVOEscrow);
      logger.info(vo.getTid()+" Finished transfer to escrow account with response: "+resIHPEscrow.getDoPaymentResponse().getParameters().getResponseMessage());
      if(!resIHPEscrow.getDoPaymentResponse().getParameters().getResponseCode().equals("0001")){
        th.setStatus("Error");
        th.setRemark("Exception during transfer to escrow account. "+resIHPEscrow.getDoPaymentResponse().getParameters().getResponseMessage()+". "+resIHPEscrow.getDoPaymentResponse().getParameters().getErrorMessage());
//        thRepo.saveAndFlush(th);
        throw new NostraException(vo.getTid()+" Exception during transfer to escrow account. "+resIHPEscrow.getDoPaymentResponse().getParameters().getResponseMessage()+". "+resIHPEscrow.getDoPaymentResponse().getParameters().getErrorMessage(), StatusCode.ERROR);
      }else {
        th.setBankRef(resIHPEscrow.getDoPaymentResponse().getParameters().getCustomerReference());
        th.setCustRef(th.getTid());
      }

      logger.info(vo.getTid()+" Starting transfer to destination account: "+th.getCreditAcc()+" with amount: "+th.getAmount());
      InHousePaymentVO ihpVODest=new InHousePaymentVO();
      ihpVODest.setAmount(th.getAmount());
      ihpVODest.setDebitAccountNo(escrow);
      ihpVODest.setCreditAccountNo(th.getCreditAcc());
      ihpVODest.setChargingModelId(th.getChargingModelId());
      ihpVODest.setPaymentMethod(method);
      ihpVODest.setRemark("Transfer from escrow account to destination account");
      resIHPDest=inHousePayment(ihpVODest);
      logger.info(vo.getTid()+" Finished transfer to destination account with response: "+resIHPDest.getDoPaymentResponse().getParameters().getResponseMessage());
      if(!resIHPDest.getDoPaymentResponse().getParameters().getResponseCode().equals("0001")){
        th.setStatus("Error");
        th.setRemark("Exception during transfer to credit account. "+resIHPDest.getDoPaymentResponse().getParameters().getResponseMessage()+". "+resIHPDest.getDoPaymentResponse().getParameters().getErrorMessage());
//        thRepo.saveAndFlush(th);
        logger.info(vo.getTid()+" Rollingback transfer start");
        InHousePaymentVO ihpVORollback=new InHousePaymentVO();
        ihpVORollback.setAmount(th.getTotalAmount());
        ihpVORollback.setDebitAccountNo(escrow);
        ihpVORollback.setCreditAccountNo(th.getDebitAcc());
        ihpVORollback.setChargingModelId(th.getChargingModelId());
        ihpVORollback.setPaymentMethod(method);
        ihpVORollback.setRemark("Rollback Transfer");
        OgpInHousePaymentRespVO resIHPRollback=inHousePayment(ihpVORollback);
        logger.info(vo.getTid()+" Finished Rollingback transfer with response: "+resIHPRollback);
        throw new NostraException(vo.getTid()+" Exception during transfer to escrow account. "+resIHPDest.getDoPaymentResponse().getParameters().getResponseMessage()+". "+resIHPDest.getDoPaymentResponse().getParameters().getErrorMessage(),StatusCode.ERROR);
      }else {
        th.setBankRef(resIHPDest.getDoPaymentResponse().getParameters().getCustomerReference());
        th.setCustRef(th.getTid());
      }

      th.setStatus("Success");
      th.setRemark("Success");
      thRepo.save(th);

      VirtualAccount vaSource=new VirtualAccount();
      vaSource=vaRepo.findByVA(th.getDebitAcc());
      UsageAccumulator ua = uaRepo.findByOwnerIDAndDest(vaSource.getOwnerID(),"KasproBank");
      Long usage =Long.parseLong(ua.getUsage())+Long.parseLong(th.getAmount());
      ua.setUsage(usage.toString());
      uaRepo.save(ua);
    }catch (Exception e){
      th.setStatus("Exception Occured");
      th.setRemark(e.getMessage());
      thRepo.saveAndFlush(th);
      throw new NostraException(e.getMessage(),StatusCode.ERROR);
    }
    TransferKasproBankResVO resVO=new TransferKasproBankResVO();
    resVO.setAdminFee(th.getAdminFee());
    resVO.setAmount(th.getAmount());
    resVO.setDestinationAccount(th.getCreditAcc());
    resVO.setInterBankFee(th.getInterBankFee());
    resVO.setRemark(vo.getRemark());
    resVO.setSourceAccount(th.getDebitAcc());
    resVO.setStatus(th.getStatus());
    resVO.setTid(th.getTid());
    resVO.setTotalAmount(th.getTotalAmount());
    return resIHPDest;
  }

  public OgpInHousePaymentRespVO transferInHouse(TransferKasproBankReqVO vo){
    TransactionHistoryStaging thSTG=thSTGRepo.findOtherBank(vo.getTid());
    TransactionHistory th = new TransactionHistory();
    if(thSTG==null){
      throw new NostraException(vo.getTid()+" Transaction ID is not found",StatusCode.DATA_NOT_FOUND);
    }else {
      thSTGRepo.delete(thSTG);
      thSTGRepo.flush();
      th=ogpConverter.convertTransactionHistory(thSTG);
    }
    BalanceVO balanceVO=new BalanceVO();
    balanceVO.setAccountNo(th.getDebitAcc());
    if(!isSuficientBalance(th, balanceVO,th.getTotalAmount(),th.getPaymentMethod(), th.getSku())){
      th.setStatus("Error");
      th.setRemark("Insuficient Balance");
      thRepo.save(th);
      throw new NostraException(vo.getTid()+" Insuficient Balance",StatusCode.ERROR);
    }
    InitDB x  = InitDB.getInstance();
    String method=x.get("Code.PaymentMethod."+th.getPaymentMethod());

    String escrow=x.get("Account.Escrow");
    OgpInHousePaymentRespVO resIHPDest = new OgpInHousePaymentRespVO();
    try {
      if (th.getSku().equals("BNI")) {
        logger.info(vo.getTid() + " Starting transfer to destination account: " + th.getCreditAcc() + " with amount: " + th.getAmount());
        InHousePaymentVO ihpVODest = new InHousePaymentVO();
        ihpVODest.setAmount(th.getAmount());
        ihpVODest.setDebitAccountNo(th.getDebitAcc());
        ihpVODest.setCreditAccountNo(th.getCreditAcc());
        ihpVODest.setChargingModelId(th.getChargingModelId());
        ihpVODest.setPaymentMethod(method);
        ihpVODest.setDestinationBankCode(th.getDestinationBankCode());
        ihpVODest.setBeneficiaryName(th.getCreditName());
        ihpVODest.setRemark("Transfer from Source account to destination account");
        resIHPDest = inHousePayment(ihpVODest);
        logger.info(vo.getTid() + " Finished transfer to destination account with response: " + resIHPDest.getDoPaymentResponse().getParameters().getResponseMessage());
        if (!resIHPDest.getDoPaymentResponse().getParameters().getResponseCode().equals("0001")) {
          th.setStatus("Error");
          th.setRemark("Exception during transfer to destination account. " + resIHPDest.getDoPaymentResponse().getParameters().getResponseMessage() + ". " + resIHPDest.getDoPaymentResponse().getParameters().getErrorMessage());
//          thRepo.save(th);
          throw new NostraException(vo.getTid() + " Exception during transfer to destination account. " + resIHPDest.getDoPaymentResponse().getParameters().getResponseMessage() + ". " + resIHPDest.getDoPaymentResponse().getParameters().getErrorMessage(), StatusCode.ERROR);
        } else {
          th.setBankRef(resIHPDest.getDoPaymentResponse().getParameters().getCustomerReference());
        }
      } else {
        logger.info(vo.getTid() + " Starting transfer to destiantion account: " + th.getCreditAcc() + " with amount: " + th.getAmount());
        InHousePaymentVO ihpVODest = new InHousePaymentVO();
        ihpVODest.setAmount(th.getAmount());
        ihpVODest.setDebitAccountNo(th.getDebitAcc());
        ihpVODest.setCreditAccountNo(th.getCreditAcc());
        ihpVODest.setChargingModelId(th.getChargingModelId());
        ihpVODest.setPaymentMethod(method);
        ihpVODest.setDestinationBankCode(th.getDestinationBankCode());
        ihpVODest.setRemark("Transfer to destination Account");
        ihpVODest.setBeneficiaryName(th.getCreditName());
        if (th.getEmail() != null) {
          ihpVODest.setBeneficiaryEmailAddress(th.getEmail());
        }
        if (th.getAddress1() != null) {
          ihpVODest.setBeneficiaryAddress1(th.getAddress1());
        }
        if (th.getAddress2() != null) {
          ihpVODest.setBeneficiaryAddress2(th.getAddress2());
        }
        resIHPDest = inHousePayment(ihpVODest);
        logger.info(vo.getTid() + " Finished transfer to destination account with response: " + resIHPDest.getDoPaymentResponse().getParameters().getResponseMessage());
        if (!resIHPDest.getDoPaymentResponse().getParameters().getResponseCode().equals("0001")) {
          th.setStatus("Error");
          th.setRemark("Exception during transfer to Destination account. " + resIHPDest.getDoPaymentResponse().getParameters().getResponseMessage() + ". " + resIHPDest.getDoPaymentResponse().getParameters().getErrorMessage());
//          thRepo.save(th);
          throw new NostraException(vo.getTid() + " Exception during transfer to Destination account." + resIHPDest.getDoPaymentResponse().getParameters().getResponseMessage() + ". " + resIHPDest.getDoPaymentResponse().getParameters().getErrorMessage(), StatusCode.ERROR);
        } else {
          th.setBankRef(resIHPDest.getDoPaymentResponse().getParameters().getCustomerReference());
          th.setCustRef(th.getTid());
        }
      }

      if (!th.getAdminFee().equals("0")) {
        logger.info(vo.getTid() + " Starting transfer to fee account: " + escrow + " with amount: " + th.getAdminFee());
        InHousePaymentVO ihpVOEscrow = new InHousePaymentVO();
        ihpVOEscrow.setAmount(th.getAdminFee());
        ihpVOEscrow.setDebitAccountNo(th.getDebitAcc());
        ihpVOEscrow.setCreditAccountNo(escrow);
        ihpVOEscrow.setChargingModelId(th.getChargingModelId());
        ihpVOEscrow.setPaymentMethod("0");
        ihpVOEscrow.setRemark("Transfer Admin Fee to Fee Account");
        OgpInHousePaymentRespVO resIHPEscrow = inHousePayment(ihpVOEscrow);
        logger.info(vo.getTid() + " Finished transfer to Fee account with response: " + resIHPEscrow.getDoPaymentResponse().getParameters().getResponseMessage());
        if (!resIHPEscrow.getDoPaymentResponse().getParameters().getResponseCode().equals("0001")) {
          th.setStatus("Error");
          th.setRemark("Exception during transfer to Fee account. " + resIHPEscrow.getDoPaymentResponse().getParameters().getResponseMessage() + ". " + resIHPEscrow.getDoPaymentResponse().getParameters().getErrorMessage());
//          thRepo.save(th);
          throw new NostraException(vo.getTid() + " Exception during transfer to Fee account. " + resIHPEscrow.getDoPaymentResponse().getParameters().getErrorMessage(), StatusCode.ERROR);
        }
      }

      th.setStatus("Success");
      th.setRemark("Success");
      thRepo.save(th);

      VirtualAccount vaSource = vaRepo.findByVA(th.getDebitAcc());
      if (th.getSku().equalsIgnoreCase("BNI")) {
        UsageAccumulator ua = uaRepo.findByOwnerIDAndDest(vaSource.getOwnerID(), "BNI");
        Long usage = Long.parseLong(ua.getUsage()) + Long.parseLong(th.getAmount());
        ua.setUsage(usage.toString());
        uaRepo.save(ua);
      } else {
        UsageAccumulator ua = uaRepo.findByOwnerIDAndDest(vaSource.getOwnerID(), "OtherBank");
        Long usage = Long.parseLong(ua.getUsage()) + Long.parseLong(th.getAmount());
        ua.setUsage(usage.toString());
        uaRepo.save(ua);
      }
    }catch (Exception e){
      th.setStatus("Exception Occured");
      th.setRemark(e.getMessage());
      thRepo.saveAndFlush(th);
      throw new NostraException(e.getMessage(),StatusCode.ERROR);
    }

    TransferKasproBankResVO resVO=new TransferKasproBankResVO();
    resVO.setAdminFee(th.getAdminFee());
    resVO.setAmount(th.getAmount());
    resVO.setDestinationAccount(th.getCreditAcc());
    resVO.setInterBankFee(th.getInterBankFee());
    resVO.setRemark(vo.getRemark());
    resVO.setSourceAccount(th.getDebitAcc());
    resVO.setStatus(th.getStatus());
    resVO.setTid(th.getTid());
    resVO.setTotalAmount(th.getTotalAmount());
    return resIHPDest;
  }

  public OgpInterBankPaymentRespVO transferInterBank(TransferKasproBankReqVO vo){
    TransactionHistoryStaging thSTG=thSTGRepo.findOtherBank(vo.getTid());
    TransactionHistory th = new TransactionHistory();
    if(thSTG==null){
      throw new NostraException(vo.getTid()+" Transaction ID is not found",StatusCode.DATA_NOT_FOUND);
    }else {
      thSTGRepo.delete(thSTG);
      thSTGRepo.flush();
      th=ogpConverter.convertTransactionHistory(thSTG);
    }
    BalanceVO balanceVO=new BalanceVO();
    balanceVO.setAccountNo(th.getDebitAcc());
    if(!isSuficientBalance(th, balanceVO,th.getTotalAmount(),th.getPaymentMethod(), th.getSku())){
      th.setStatus("Error");
      th.setRemark("Insuficient Balance");
      thRepo.save(th);
      throw new NostraException(vo.getTid()+" Insuficient Balance",StatusCode.ERROR);
    }
    InitDB x  = InitDB.getInstance();
    String method=x.get("Code.PaymentMethod."+th.getPaymentMethod());
    logger.info("Payment Method = "+method);

    OgpInterBankPaymentRespVO ibpResVO = new OgpInterBankPaymentRespVO();
    try {
      String escrow = x.get("Account.Escrow");
      logger.info(vo.getTid() + " Starting transfer to destiantion account: " + th.getCreditAcc() + " with amount: " + th.getAmount());
      InterBankPaymentVO ibpVO = new InterBankPaymentVO();
      ibpVO.setAccountNo(th.getDebitAcc());
      ibpVO.setAmount(th.getAmount());
      ibpVO.setDestinationAccountName(th.getCreditName());
      ibpVO.setDestinationAccountNo(th.getCreditAcc());
      ibpVO.setDestinationBankCode(th.getDestinationBankCode());
      ibpVO.setDestinationBankName(th.getSku());
      ibpVO.setRetrievalReffNo(th.getTid());
      ibpResVO = ogpService.interBankPayment(ibpVO);
      logger.info(vo.getTid() + " Finished transfer to destination account with response: " + ibpResVO.getGetInterbankPaymentResponse().getParameters().getResponseMessage());
      if (!ibpResVO.getGetInterbankPaymentResponse().getParameters().getResponseCode().equals("0001")) {
        th.setStatus("Error");
        th.setRemark("Exception during transfer to Destination account. " + ibpResVO.getGetInterbankPaymentResponse().getParameters().getResponseMessage() + ". " + ibpResVO.getGetInterbankPaymentResponse().getParameters().getErrorMessage());
//        thRepo.save(th);
        throw new NostraException(vo.getTid() + " Exception during transfer to Destination account. " + ibpResVO.getGetInterbankPaymentResponse().getParameters().getResponseMessage() + ". " + ibpResVO.getGetInterbankPaymentResponse().getParameters().getErrorMessage(), StatusCode.ERROR);
      } else {
        th.setBankRef(ibpResVO.getGetInterbankPaymentResponse().getParameters().getCustomerReffNum());
        th.setCustRef(th.getTid());
      }

      if (!th.getAdminFee().equals("0")) {
        logger.info(vo.getTid() + " Starting transfer to fee account: " + escrow + " with amount: " + th.getAdminFee());
        InHousePaymentVO ihpVOEscrow = new InHousePaymentVO();
        ihpVOEscrow.setAmount(th.getAdminFee());
        ihpVOEscrow.setDebitAccountNo(th.getDebitAcc());
        ihpVOEscrow.setCreditAccountNo(escrow);
        ihpVOEscrow.setChargingModelId(th.getChargingModelId());
        ihpVOEscrow.setPaymentMethod(method);
        ihpVOEscrow.setRemark("Transfer Admin Fee to Fee Account");
        OgpInHousePaymentRespVO resIHPEscrow = inHousePayment(ihpVOEscrow);
        logger.info(vo.getTid() + " Finished transfer to Fee account with response: " + resIHPEscrow.getDoPaymentResponse().getParameters().getResponseMessage());
        if (!resIHPEscrow.getDoPaymentResponse().getParameters().getResponseCode().equals("0001")) {
          th.setStatus("Error");
          th.setRemark("Exception during transfer to Fee account. " + resIHPEscrow.getDoPaymentResponse().getParameters().getResponseMessage() + ". " + resIHPEscrow.getDoPaymentResponse().getParameters().getErrorMessage());
//          thRepo.save(th);
          throw new NostraException(vo.getTid() + " Exception during transfer to Fee account. " + resIHPEscrow.getDoPaymentResponse().getParameters().getErrorMessage(), StatusCode.ERROR);
        }
      }

      th.setStatus("Success");
      th.setRemark("Success");
      thRepo.save(th);

      VirtualAccount vaSource = vaRepo.findByVA(th.getDebitAcc());
      UsageAccumulator ua = uaRepo.findByOwnerIDAndDest(vaSource.getOwnerID(), "OtherBank");
      Long usage = Long.parseLong(ua.getUsage()) + Long.parseLong(th.getAmount());
      ua.setUsage(usage.toString());
      uaRepo.save(ua);
    }catch (Exception e){
      th.setStatus("Exception Occured");
      th.setRemark(e.getMessage());
      thRepo.saveAndFlush(th);
      throw new NostraException(e.getMessage(),StatusCode.ERROR);
    }

    TransferKasproBankResVO resVO=new TransferKasproBankResVO();
    resVO.setAdminFee(th.getAdminFee());
    resVO.setAmount(th.getAmount());
    resVO.setDestinationAccount(th.getCreditAcc());
    resVO.setInterBankFee(th.getInterBankFee());
    resVO.setRemark(vo.getRemark());
    resVO.setSourceAccount(th.getDebitAcc());
    resVO.setStatus(th.getStatus());
    resVO.setTid(th.getTid());
    resVO.setTotalAmount(th.getTotalAmount());
    return ibpResVO;

  }

  public OgpInHousePaymentRespVO transferKaspro(TransferKasproBankReqVO vo){
    TransactionHistoryStaging thSTG=thSTGRepo.findByTID(vo.getTid(),"Kaspro");
    TransactionHistory th = new TransactionHistory();
    if(thSTG==null){
      throw new NostraException(vo.getTid()+" Transaction ID is not found",StatusCode.DATA_NOT_FOUND);
    }else {
      thSTGRepo.delete(thSTG);
      thSTGRepo.flush();
      th=ogpConverter.convertTransactionHistory(thSTG);
    }
    BalanceVO balanceVO=new BalanceVO();
    balanceVO.setAccountNo(th.getDebitAcc());
    if(!isSuficientBalance(th, balanceVO,th.getTotalAmount(), th.getPaymentMethod(), th.getSku())){
      th.setStatus("Error");
      th.setRemark("Insuficient Balance");
      thRepo.save(th);
      throw new NostraException(vo.getTid()+" Insuficient Balance",StatusCode.ERROR);
    }
    InitDB x  = InitDB.getInstance();
    String method=x.get("Code.PaymentMethod."+th.getPaymentMethod());

    String escrow=x.get("Account.Escrow");
    String custodian=x.get("Account.Custodian");

    OgpInHousePaymentRespVO resIHPCustodian = new OgpInHousePaymentRespVO();
    try {
      logger.info(vo.getTid() + " Starting transfer to custodian account: " + custodian + " with amount: " + th.getAmount());
      InHousePaymentVO ihpVOCustodian = new InHousePaymentVO();
      ihpVOCustodian.setAmount(th.getAmount());
      ihpVOCustodian.setDebitAccountNo(th.getDebitAcc());
      ihpVOCustodian.setCreditAccountNo(custodian);
      ihpVOCustodian.setChargingModelId(th.getChargingModelId());
      ihpVOCustodian.setPaymentMethod(method);
      ihpVOCustodian.setRemark("Transfer from Source account to custodian account");
      resIHPCustodian = inHousePayment(ihpVOCustodian);
      logger.info(vo.getTid() + " Finished transfer to custodian account with response: " + resIHPCustodian.getDoPaymentResponse().getParameters().getResponseMessage());
      if (!resIHPCustodian.getDoPaymentResponse().getParameters().getResponseCode().equals("0001")) {
        th.setStatus("Error");
        th.setRemark("Exception during transfer to destination account. " + resIHPCustodian.getDoPaymentResponse().getParameters().getResponseMessage() + ". " + resIHPCustodian.getDoPaymentResponse().getParameters().getErrorMessage());
//        thRepo.save(th);

        throw new NostraException(vo.getTid() + " Exception during transfer to destination account. " + resIHPCustodian.getDoPaymentResponse().getParameters().getResponseMessage() + ". " + resIHPCustodian.getDoPaymentResponse().getParameters().getErrorMessage(), StatusCode.ERROR);
      } else {
        th.setBankRef(resIHPCustodian.getDoPaymentResponse().getParameters().getCustomerReference());
        th.setCustRef(th.getTid());
      }

      logger.info(vo.getTid() + " Start doing Cash In");
      String requestId = ogpService.getValueDate(new Date()).concat(th.getDebitAcc());
      String body = "{\"payments\": [\n" +
              "        {\n" +
              "            \"pocket-id\": \"2\",\n" +
              "            \"amount\": \"" + th.getAmount() + "\",\n" +
              "            \"reference\": \"CashIn-" + requestId + "\"\n" +
              "        }\n" +
              "    ],\n" +
              "    \"auth\": {\n" +
              "        \"pass\": \"1234\"\n" +
              "    },\n" +
              "    \"destination\": \"" + th.getCreditAcc() + "\",\n" +
              "    \"request-id\": \"" + requestId + "\"\n" +
              "}";
      String resCashIn = "";
      try {
        resCashIn = httpProcessingService.kasproCashIn(body);
        JSONObject resCashInJSON = new JSONObject(resCashIn);
        logger.info(vo.getTid() + " Cash In Response : " + resCashInJSON.toString());
        if (resCashInJSON.getInt("code") != 0) {
          th.setStatus("Error while doin Cash In");
          th.setRemark(resCashInJSON.getString("message"));
          throw new NostraException(vo.getTid() + " " + resCashInJSON.getString("message"), StatusCode.ERROR);
        }
      } catch (Exception e) {
        e.printStackTrace();
        th.setStatus("Exception while doin Cash In");
        th.setRemark(e.getMessage());
        logger.info(vo.getTid() + " Starting rollback transfer to debit account: " + th.getDebitAcc() + " with amount: " + th.getAmount());
        InHousePaymentVO ihpVORollback = new InHousePaymentVO();
        ihpVORollback.setAmount(th.getAmount());
        ihpVORollback.setDebitAccountNo(custodian);
        ihpVORollback.setCreditAccountNo(th.getDebitAcc());
        ihpVORollback.setChargingModelId(th.getChargingModelId());
        ihpVORollback.setPaymentMethod(method);
        ihpVORollback.setRemark("Rollback Transfer from custodian account to debit account");
        inHousePayment(ihpVORollback);
        logger.info(vo.getTid() + " Finished rollback transfer to debit account: " + th.getDebitAcc() + " with amount: " + th.getAmount());
        throw new NostraException(vo.getTid() + " " + e.getMessage(), StatusCode.ERROR);
      }
      logger.info(vo.getTid() + " Finished doing Cash In");

      if (!th.getAdminFee().equals("0")) {
        logger.info(vo.getTid() + " Starting transfer to fee account: " + escrow + " with amount: " + th.getAdminFee());
        InHousePaymentVO ihpVOEscrow = new InHousePaymentVO();
        ihpVOEscrow.setAmount(th.getAdminFee());
        ihpVOEscrow.setDebitAccountNo(th.getDebitAcc());
        ihpVOEscrow.setCreditAccountNo(escrow);
        ihpVOEscrow.setChargingModelId(th.getChargingModelId());
        ihpVOEscrow.setPaymentMethod(method);
        ihpVOEscrow.setRemark("Transfer Admin Fee to Fee Account");
        OgpInHousePaymentRespVO resIHPEscrow = inHousePayment(ihpVOEscrow);
        logger.info(vo.getTid() + " Finished transfer to Fee account with response: " + resIHPEscrow.getDoPaymentResponse().getParameters().getResponseMessage());
        if (!resIHPEscrow.getDoPaymentResponse().getParameters().getResponseCode().equals("0001")) {
          th.setStatus("Error");
          th.setRemark("Exception during transfer to Fee account. " + resIHPEscrow.getDoPaymentResponse().getParameters().getResponseMessage() + ". " + resIHPEscrow.getDoPaymentResponse().getParameters().getErrorMessage());
//          thRepo.save(th);
          throw new NostraException(vo.getTid() + " Exception during transfer to Fee account. " + resIHPEscrow.getDoPaymentResponse().getParameters().getErrorMessage(), StatusCode.ERROR);
        }
      }

      th.setStatus("Success");
      th.setRemark("Success");
      thRepo.save(th);

      VirtualAccount vaSource = new VirtualAccount();
      vaSource = vaRepo.findByVA(th.getDebitAcc());
      UsageAccumulator ua = uaRepo.findByOwnerIDAndDest(vaSource.getOwnerID(), "Kaspro");
      Long usage = Long.parseLong(ua.getUsage()) + Long.parseLong(th.getAmount());
      ua.setUsage(usage.toString());
      uaRepo.save(ua);
    }catch (Exception e){
      th.setStatus("Exception Occured");
      th.setRemark(e.getMessage());
      thRepo.saveAndFlush(th);
      throw new NostraException(e.getMessage(),StatusCode.ERROR);
    }

    TransferKasproBankResVO resVO=new TransferKasproBankResVO();
    resVO.setAdminFee(th.getAdminFee());
    resVO.setAmount(th.getAmount());
    resVO.setDestinationAccount(th.getCreditAcc());
    resVO.setInterBankFee(th.getInterBankFee());
    resVO.setRemark(vo.getRemark());
    resVO.setSourceAccount(th.getDebitAcc());
    resVO.setStatus(th.getStatus());
    resVO.setTid(th.getTid());
    resVO.setTotalAmount(th.getTotalAmount());
    return resIHPCustodian;
  }

  public boolean isSuficientBalance(TransactionHistory th, BalanceVO vo, String amount, String method, String sku){
    OgpBalanceRespVO ogpBalanceRespVO = new OgpBalanceRespVO();
    InitDB initDB=InitDB.getInstance();
    List<String> dan = Arrays.asList("Kaspro", "BNI", "KasproBank");
    Long bankFee= Long.valueOf(0);
    if(!dan.contains(sku)){
      bankFee=Long.valueOf(initDB.get("InterBank.Fee."+method));
    }

    logger.info("Bank Fee = "+bankFee);

    try {
      ogpBalanceRespVO = ogpService.balance(vo);
      if(!ogpBalanceRespVO.getGetBalanceResponse().getParameters().getResponseCode().equals("0001")){
        th.setRemark("Error during query balance. "+ogpBalanceRespVO.getGetBalanceResponse().getParameters().getResponseCode()+" : "+ogpBalanceRespVO.getGetBalanceResponse().getParameters().getResponseMessage());
        th.setStatus("Error Query Balance");
        thRepo.saveAndFlush(th);
        throw new NostraException("Error while query balance with error code "+ogpBalanceRespVO.getGetBalanceResponse().getParameters().getResponseCode()+" with message : "+ogpBalanceRespVO.getGetBalanceResponse().getParameters().getResponseMessage());
      }
      else if(Long.parseLong(ogpBalanceRespVO.getGetBalanceResponse().getParameters().getAccountBalance()) < (Long.parseLong(amount)+bankFee)){
        return false;
      }
      return true;
    }catch (Exception e){
      th.setRemark("Error during query balance. "+e.getMessage());
      th.setStatus("Error Query Balance");
      thRepo.saveAndFlush(th);
      throw new NostraException("Error Query Balance. "+e.getMessage(),StatusCode.ERROR);
    }
  }

  public OgpBalanceRespVO getBalance(K2KBGetBalanceReqVO vo){
    ValidateMSISDNVO msisdnSource=rcService.validateMsisdn(vo.getMsisdn());
    VirtualAccount va = new VirtualAccount();
    if(msisdnSource.getIsMsisdn().equalsIgnoreCase("0")){
      va=vaRepo.findIndividual(msisdnSource.getValue());
    }else {
      va=vaRepo.findCorporate(msisdnSource.getValue());
    }

    if(va.getVa()==null){
      throw new NostraException("Account not found", StatusCode.ERROR);
    }
    BalanceVO reqVo = new BalanceVO();
    reqVo.setAccountNo(va.getVa());
    OgpBalanceRespVO result = ogpService.balance(reqVo);
    if(!result.getGetBalanceResponse().getParameters().getResponseCode().equalsIgnoreCase("0001")){
      throw new NostraException("Error during get balance : "+result.getGetBalanceResponse().getParameters().getResponseCode()+" : "+result.getGetBalanceResponse().getParameters().getResponseMessage(),StatusCode.ERROR);
    }else {
      return result;
    }
  }

  public OgpPaymentStatusRespVO getPaymentStatus(K2KBGetPaymentStatusReqVO vo){
    PaymentStatusVO reqVO=new PaymentStatusVO();
    reqVO.setCustomerReferenceNumber(vo.getCustReff());
    OgpPaymentStatusRespVO result=ogpService.paymentStatus(reqVO);
    if(!result.getGetPaymentStatusResponse().getParameters().getResponseCode().equalsIgnoreCase("0001")){
      throw new NostraException(result.getGetPaymentStatusResponse().getParameters().getResponseCode()+" : "+result.getGetPaymentStatusResponse().getParameters().getResponseMessage(),StatusCode.ERROR);
    }else {
      return result;
    }
  }

  public OgpInHousePaymentRespVO k2kbPaymentInhouse(K2KBPaymentInhouseReqVO vo){
    OgpInHousePaymentRespVO result = new OgpInHousePaymentRespVO();
    String sku="";
    InitDB initDB=InitDB.getInstance();
    ValidateMSISDNVO msisdnSource=rcService.validateMsisdn(vo.getMsisdn());

    if (vo.getBankCode()==null||vo.getBankCode().equals("009")){
      if(vo.getDestAcc().startsWith("8945")||vo.getDestAcc().startsWith("8513")){
        sku="KasproBank";
        if(vo.getDestAcc().startsWith("8945")){
          vo.setDestAcc(vo.getDestAcc().substring(4));
          vo.setDestAcc("0"+rmLeadingZeros(vo.getDestAcc()));
          ValidateMSISDNVO msisdnDest=rcService.validateMsisdn(vo.getDestAcc());
          vo.setDestAcc(msisdnDest.getValue());
          log.info("Destination : "+vo.getDestAcc());
        }
      }else if(vo.getDestAcc().startsWith("8693")){
        sku="Kaspro";
        vo.setDestAcc(vo.getDestAcc().substring(4));
        vo.setDestAcc("0"+rmLeadingZeros(vo.getDestAcc()));
        log.info("Destination : "+vo.getDestAcc());
      }else{
        sku="BNI";
      }
    }else {
      sku=initDB.get("SKU.Code."+vo.getBankCode());
    }

    if(sku.equals("")||sku==null){
      throw new NostraException("SKU Not Found",StatusCode.DATA_NOT_FOUND);
    }

    if(vo.getPaymentMethod().equalsIgnoreCase("0")){
      vo.setPaymentMethod("ONLINE");
    }else if(vo.getPaymentMethod().equalsIgnoreCase("1")){
      vo.setPaymentMethod("RTGS");
    }else if(vo.getPaymentMethod().equalsIgnoreCase("2")){
      vo.setPaymentMethod("KLIRING");
    }

    String email=null;
    String address1=null;
    String address2=null;

    if(vo.getEmail()!=null){
      email=vo.getEmail();
    }
    if(vo.getAddress1()!=null){
      address1=vo.getAddress1();
    }
    if(vo.getAddress2()!=null){
      address2=vo.getAddress2();
    }

    InquiryKasproBankResVO inquiryRes = this.kasproBankInquiry(msisdnSource.getValue(), vo.getDestAcc(), sku, vo.getAmount(), vo.getPaymentMethod(), "OUR", false, email, address1, address2);

    TransferKasproBankReqVO transferReq=new TransferKasproBankReqVO();
    transferReq.setRemark(vo.getRemark());
    transferReq.setTid(inquiryRes.getTid());

    if(sku.equals("KasproBank")){
      result=this.transferKasproBank(transferReq);
    }else if(sku.equals("Kaspro")){
      result=this.transferKaspro(transferReq);
    }else {
      result=this.transferInHouse(transferReq);
    }

    if(!result.getDoPaymentResponse().getParameters().getResponseCode().equalsIgnoreCase("0001")){
      throw new NostraException(result.getDoPaymentResponse().getParameters().getResponseMessage(), StatusCode.ERROR);
    }

    return result;
  }

  public OgpInterBankPaymentRespVO k2kbPaymentInterBank(K2KBPaymentInterBankReqVO vo){
    log.info("Starting Interbank Payment");
    log.info(vo.toString());
    InitDB initDB=InitDB.getInstance();
    String sku = initDB.get("SKU.Code."+vo.getBankCode());
    if(sku.equals("")||sku==null){
      throw new NostraException("SKU Not Found",StatusCode.DATA_NOT_FOUND);
    }

    OgpInterBankPaymentRespVO result = new OgpInterBankPaymentRespVO();

    if(vo.getBankCode().equalsIgnoreCase("022") && vo.getDestAcc().startsWith("3929")){
      log.info("This is Kaspro Account");
      vo.setDestAcc(vo.getDestAcc().substring(4));
      vo.setDestAcc("0"+rmLeadingZeros(vo.getDestAcc()));
      log.info("Destination : "+vo.getDestAcc());

      ValidateMSISDNVO msisdnSource=rcService.validateMsisdn(vo.getMsisdn());

      InquiryKasproBankResVO inquiryRes = kasproBankInquiry(msisdnSource.getValue(), vo.getDestAcc(), "Kaspro", vo.getAmount(), "online", "OUR", false, null, null, null);
      log.info("Inquiry Kaspro Result : "+inquiryRes.toString());
      if(inquiryRes.getTid()==null){
        throw new NostraException("Inquiry Kaspro Failed", StatusCode.ERROR);
      }

      TransferKasproBankReqVO transferReq=new TransferKasproBankReqVO();
      transferReq.setRemark(vo.getRemark());
      transferReq.setTid(inquiryRes.getTid());

      OgpInHousePaymentRespVO resp =transferKaspro(transferReq);
      log.info("Transfer Kaspro Result : "+resp.toString());

      OgpInterBankPaymentRespVO.PaymentResponse.Parameter parameter = new OgpInterBankPaymentRespVO.PaymentResponse.Parameter();
      parameter.setAccountName(vo.getFormName());
      parameter.setCustomerReffNum(resp.getDoPaymentResponse().getParameters().getCustomerReference());
      parameter.setDestinationAccountName(vo.getName());
      parameter.setDestinationAccountNum(resp.getDoPaymentResponse().getParameters().getCreditAccountNo());
      parameter.setErrorMessage(resp.getDoPaymentResponse().getParameters().getErrorMessage());
      parameter.setResponseCode(resp.getDoPaymentResponse().getParameters().getResponseCode());
      parameter.setResponseMessage(resp.getDoPaymentResponse().getParameters().getResponseMessage());
      parameter.setResponseTimestamp(resp.getDoPaymentResponse().getParameters().getResponseTimestamp());

      OgpInterBankPaymentRespVO.PaymentResponse paymentResponse = new OgpInterBankPaymentRespVO.PaymentResponse();
      paymentResponse.setParameters(parameter);
      paymentResponse.setClientId(resp.getDoPaymentResponse().getClientId());

      result.setGetInterbankPaymentResponse(paymentResponse);
      if(!result.getGetInterbankPaymentResponse().getParameters().getResponseCode().equalsIgnoreCase("0001")){
        throw new NostraException(result.getGetInterbankPaymentResponse().getParameters().getResponseMessage(),StatusCode.ERROR);
      }

    }else {
      log.info("Account is other bank");
      ValidateMSISDNVO msisdnSource=rcService.validateMsisdn(vo.getMsisdn());
      ValidateMSISDNVO msisdnDest=rcService.validateMsisdn(vo.getDestAcc());

      InquiryKasproBankResVO inquiryRes = kasproBankInquiry(msisdnSource.getValue(), msisdnDest.getValue(), sku, vo.getAmount(), "ONLINE", "OUR", false, null, null, null);
      log.info("Inquiry Other Bank Result : "+inquiryRes.toString());

      TransferKasproBankReqVO transferReq=new TransferKasproBankReqVO();
      transferReq.setRemark(vo.getRemark());
      transferReq.setTid(inquiryRes.getTid());
      result=this.transferInterBank(transferReq);
      log.info("Transfer Other Bank Result : "+inquiryRes.toString());

      if(!result.getGetInterbankPaymentResponse().getParameters().getResponseCode().equalsIgnoreCase("0001")){
        throw new NostraException(result.getGetInterbankPaymentResponse().getParameters().getResponseMessage(),StatusCode.ERROR);
      }
    }

    return result;
  }

  public WrapperTransferResVO wrapperTransfer(WrapperTransferReqVO vo){
    WrapperTransferResVO result = new WrapperTransferResVO();
    TransferKasproBankReqVO reqVO = new TransferKasproBankReqVO();
    reqVO.setTid(vo.getTid());
    reqVO.setRemark(vo.getRemark());
    if(vo.getPaymentMethod().equalsIgnoreCase("ONLINE")){
      if(vo.getSku().equalsIgnoreCase("Kaspro")){
        OgpInHousePaymentRespVO resVO=this.transferKaspro(reqVO);
        result.setReffId(resVO.getDoPaymentResponse().getParameters().getCustomerReference());
        if(!resVO.getDoPaymentResponse().getParameters().getResponseCode().equalsIgnoreCase("0001")){
          throw new NostraException(resVO.getDoPaymentResponse().getParameters().getResponseMessage(),StatusCode.ERROR);
        }
      }else if (vo.getSku().equalsIgnoreCase("KasproBank")){
        OgpInHousePaymentRespVO resVO=this.transferKasproBank(reqVO);
        result.setReffId(resVO.getDoPaymentResponse().getParameters().getCustomerReference());
        if(!resVO.getDoPaymentResponse().getParameters().getResponseCode().equalsIgnoreCase("0001")){
          throw new NostraException(resVO.getDoPaymentResponse().getParameters().getResponseMessage(),StatusCode.ERROR);
        }
      }else if(vo.getSku().equalsIgnoreCase("BNI")){
        OgpInHousePaymentRespVO resVO=this.transferInHouse(reqVO);
        result.setReffId(resVO.getDoPaymentResponse().getParameters().getCustomerReference());
        if(!resVO.getDoPaymentResponse().getParameters().getResponseCode().equalsIgnoreCase("0001")){
          throw new NostraException(resVO.getDoPaymentResponse().getParameters().getResponseMessage(),StatusCode.ERROR);
        }
      }else {
        OgpInterBankPaymentRespVO resVO = this.transferInterBank(reqVO);
        result.setReffId(resVO.getGetInterbankPaymentResponse().getParameters().getCustomerReffNum());
        if(!resVO.getGetInterbankPaymentResponse().getParameters().getResponseCode().equalsIgnoreCase("0001")){
          throw new NostraException(resVO.getGetInterbankPaymentResponse().getParameters().getResponseMessage(),StatusCode.ERROR);
        }
      }
    }else {
      OgpInHousePaymentRespVO resVO=this.transferInHouse(reqVO);
      result.setReffId(resVO.getDoPaymentResponse().getParameters().getCustomerReference());
      if(!resVO.getDoPaymentResponse().getParameters().getResponseCode().equalsIgnoreCase("0001")){
        throw new NostraException(resVO.getDoPaymentResponse().getParameters().getResponseMessage(),StatusCode.ERROR);
      }
    }
    return result;
  }

  public OgpInHouseInquiryRespVO k2kbInquiryInhouse(K2KBInquiryInhouseReqVO vo){
    InHouseInquiryVO reqVo = new InHouseInquiryVO();
    reqVo.setAccountNo(vo.getDestAcc());

    OgpInHouseInquiryRespVO result=ogpService.inHouseInquiry(reqVo);
    if(!result.getGetInHouseInquiryResponse().getParameters().getResponseCode().equalsIgnoreCase("0001")){
      throw new NostraException(result.getGetInHouseInquiryResponse().getParameters().getResponseMessage(),StatusCode.ERROR);
    }

    return result;
  }

  public OgpInterBankInquiryRespVO k2kbInquireInterBank(K2KBInquiryInterBankReqVO vo){

    OgpInterBankInquiryRespVO result=new OgpInterBankInquiryRespVO();
    ValidateMSISDNVO msisdnSource=rcService.validateMsisdn(vo.getMsisdn());

    VirtualAccount vaSource=new VirtualAccount();
    if(msisdnSource.getIsMsisdn().equalsIgnoreCase("0")){
      vaSource = vaRepo.findIndividual(msisdnSource.getValue());
    }else {
      vaSource = vaRepo.findCorporate(msisdnSource.getValue());
    }

    if(vaSource.getVa()==null){
      throw new NostraException("Source Account Not Found", StatusCode.DATA_NOT_FOUND);
    }

    InterBankInquiryVO reqVO = new InterBankInquiryVO();
    reqVO.setDestinationBankCode(vo.getBankCode());
    reqVO.setDestinationAccountNo(vo.getDestAcc());
    reqVO.setAccountNo(vaSource.getVa());

    result=ogpService.interBankInquiry(reqVO);
    if(!result.getGetInterbankInquiryResponse().getParameters().getResponseCode().equalsIgnoreCase("0001")){
      throw new NostraException(result.getGetInterbankInquiryResponse().getParameters().getResponseMessage(),StatusCode.ERROR);
    }

    return result;
  }

  public String cancelTID(String username, String tid){
    String result ="";

    TransactionHistoryStaging thStg = thSTGRepo.findByTID2(tid);
    if(thStg==null){
      throw new NostraException("TID Not Found", StatusCode.DATA_NOT_FOUND);
    }else {
      thSTGRepo.delete(thStg);
      thSTGRepo.flush();
      TransactionHistory th=ogpConverter.convertTransactionHistory(thStg);
      th.setStatus("Cancelled");
      th.setRemark("Cancelled by "+username);
      thRepo.saveAndFlush(th);
      result="Transaction ID "+th.getTid()+" is cancelled by "+username;
    }
    return result;
  }

  @Scheduled(cron = "0 1 0 * * *")
  @Transactional
  public void resetUsage(){
    uaRepo.resetUsage();
  }

  public String rmLeadingZeros(String str){
    return str.replaceFirst("^0+(?!$)", "");
  }

  public String valueBeforeDot(String value){

    if(value.contains(".")){
      return value.substring(0, value.indexOf("."));
    }else {
      return value;
    }
  }
}
