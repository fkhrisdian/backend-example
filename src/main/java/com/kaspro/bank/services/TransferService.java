package com.kaspro.bank.services;

import com.google.gson.Gson;
import com.kaspro.bank.converter.OGPConverter;
import com.kaspro.bank.enums.StatusCode;
import com.kaspro.bank.exception.NostraException;
import com.kaspro.bank.persistance.domain.*;
import com.kaspro.bank.persistance.repository.*;
import com.kaspro.bank.util.InitDB;
import com.kaspro.bank.vo.*;
import com.kaspro.bank.vo.Inquiry.InquiryKasproBankResVO;
import com.kaspro.bank.vo.TransferKasproBank.TransferKasproBankReqVO;
import com.kaspro.bank.vo.TransferKasproBank.TransferKasproBankResVO;
import com.kaspro.bank.vo.ogp.OgpBalanceRespVO;
import com.kaspro.bank.vo.ogp.OgpInHouseInquiryRespVO;
import com.kaspro.bank.vo.ogp.OgpInterBankInquiryRespVO;
import com.kaspro.bank.vo.ogp.OgpInHousePaymentRespVO;
import com.kaspro.bank.vo.ogp.OgpInterBankPaymentRespVO;
import com.kaspro.bank.vo.ogp.OgpPaymentStatusRespVO;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

  public String inHousePayment(InHousePaymentVO vo) {
    OgpInHousePaymentRespVO paymentRespVO = ogpService.inHousePayment(vo);
    return paymentRespVO.getDoPaymentResponse().getParameters().getResponseCode();
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

  public List<TransactionHistory> findFilteredTransaction(String accType, String partnerId, String senderId, String msisdn, String tid){
    List<TransactionHistory> ths=thRepo.findFilteredTransaction(accType, partnerId, senderId, msisdn, tid);
    return ths;
  }

  @Transactional
  public InquiryKasproBankResVO kasproBankInquiry(String source, String destination, String sku, String amount, String paymentMethod, String chargingModel){

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
    IndividualVO iVOSource=new IndividualVO();
    IndividualVO iVo= new IndividualVO();
    paymentMethod=paymentMethod.toUpperCase();
    String tmpSKU=sku;

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
        th.setSenderId(iVOSource.getIndividual().getId().toString());
        th.setMsisdn(iVOSource.getIndividual().getMsisdn());
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
      }
    }

    if(vaSource==null){
      throw new NostraException("Source account is not found", StatusCode.DATA_NOT_FOUND);
    }

    TransactionHistoryStaging thSTG=thSTGRepo.findBySenderId(Integer.toString(vaSource.getOwnerID()));
    if(thSTG!=null){
      throw new NostraException("You have pending transcation with TID "+thSTG.getTid()+". Please finish the pending transaction.",StatusCode.ERROR);
    }

    if(sku.equals("KasproBank")){
      if(!paymentMethod.equalsIgnoreCase("ONLINE")){
        throw new NostraException("Transfer to KasproBank, Kaspro, BNI or BNI Syariah can only with ONLINE method");
      }
      if(destination.startsWith("628")||destination.startsWith("08")){
        if(destination.startsWith("08")){
          destination="62"+destination.substring(1);
        }
        va=vaRepo.findIndividual(destination);
        if(va!=null){
          iVo=iService.getIndividualDetail(va.getOwnerID());
          if(!iVo.getIndividual().getStatus().equals("ACTIVE")){
            throw new NostraException("Source account is not active",StatusCode.ERROR);
          }
          th.setDebitName(iVo.getIndividual().getName());
        }
      }else{
        va=vaRepo.findCorporate(destination);
        if(va!=null){
          pmVO = pmService.findDetail(va.getOwnerID());
          if(!pmVO.getPartnerMember().getStatus().equals("ACTIVE")){
            throw new NostraException("Source account is not active",StatusCode.ERROR);
          }
          th.setDebitName(pmVOSource.getPartnerMember().getName());
        }
      }
      if(va==null) {
        throw new NostraException("Destination account is not found", StatusCode.DATA_NOT_FOUND);
      }

      vo.setDestinationAccount(va.getVa());
      vo.setDestinationName(pmVO.getPartnerMember().getName());
      th.setCreditName(pmVO.getPartnerMember().getName());
      th.setCreditAcc(va.getVa());
    }else if(sku.equals("BNI")){
      if(!paymentMethod.equalsIgnoreCase("ONLINE")){
        throw new NostraException("Transfer to KasproBank, Kaspro, BNI or BNI Syariah can only with ONLINE method");
      }
      ihi.setAccountNo(destination);
      ihiResp=ogpService.inHouseInquiry(ihi);
      if(!ihiResp.getGetInHouseInquiryResponse().getParameters().getResponseCode().equals("0001")){
        throw new NostraException("Destination account is not found", StatusCode.DATA_NOT_FOUND);
      }else{
        vo.setDestinationAccount(ihiResp.getGetInHouseInquiryResponse().getParameters().getAccountNumber());
        vo.setDestinationName(ihiResp.getGetInHouseInquiryResponse().getParameters().getCustomerName());
        th.setCreditName(vo.getDestinationName());
        th.setCreditAcc(ihiResp.getGetInHouseInquiryResponse().getParameters().getAccountNumber());
      }
    }else if(sku.equals("Kaspro")){
      if(!paymentMethod.equalsIgnoreCase("ONLINE")){
        throw new NostraException("Transfer to KasproBank, Kaspro, BNI or BNI Syariah can only with ONLINE method");
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
            String accountName=resPayu.getJSONObject("account").getString("account-name");
            vo.setDestinationName(accountName);
            vo.setDestinationAccount(resValidate.getString("account-number"));
            th.setCreditName(vo.getDestinationName());
            logger.info("Account number: "+resValidate.getString("account-number"));
            th.setCreditAcc(resValidate.getString("account-number"));
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }else{
      Long tmpAmount=Long.parseLong(amount);
      InterBankInquiryVO ibiVO=new InterBankInquiryVO();
      tmpSKU="OtherBank";
      OgpInterBankInquiryRespVO ibiResVO=new OgpInterBankInquiryRespVO();
      bankCode="014";
      bankName="BCA";
      ibiVO.setAccountNo(vaSource.getVa());
      ibiVO.setDestinationAccountNo(destination);
      ibiVO.setDestinationBankCode(bankCode);
      ibiResVO=ogpService.interBankInquiry(ibiVO);
      th.setCreditAcc(destination);
      th.setCreditName("Dummy");
      vo.setDestinationAccount(th.getCreditAcc());
      vo.setDestinationName(th.getCreditName());
      sku="BCA";
      if(paymentMethod.equalsIgnoreCase("ONLINE")){
        th.setDestinationBankCode(bankCode);

      }else if(paymentMethod.equalsIgnoreCase("RTGS")){
        bankCodeRTGS="CENAIDJAXXX";
        if(tmpAmount<100000000){
          throw new NostraException("Minimum RTGS Transfer is 100 Million", StatusCode.ERROR);
        }
        th.setDestinationBankCode(bankCodeRTGS);
      }else if(paymentMethod.equalsIgnoreCase("KLIRING")){
        if(tmpAmount>1000000000){
          throw new NostraException("Maximum KLIRING Transfer is 1 Billion", StatusCode.ERROR);
        }
        bankCodeRTGS="CENAIDJAXXX";
        th.setDestinationBankCode(bankCodeRTGS);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String currentTime = sdf.format(new Date());
        logger.info("Current Time is : "+currentTime);
        if(!tService.isCutOff(currentTime)){
          throw new NostraException("Already Cut Off Time",StatusCode.ERROR);
        }
      }else{
        throw new NostraException("Invalid payment method",StatusCode.ERROR);
      }
    }

    String fee="";
    if(vaSource.getFlag().equals("I")){
      fee="2000";
    }
    for(TransferFee tf:pVOSource.getTransferFees()){
      if(tf.getDestination().equals(tmpSKU)){
        fee=tf.getFee().toString().replaceAll("\\.0*$", "");

        break;
      }
    }
    logger.info("Transfer Fee = "+fee);

    String tier="";
    for(TransferInfoMember tim:pmVOSource.getListTransferInfoMember()){
      if(tim.getName().equals("TierLimit")){
        tier=tim.getValue();
        break;
      }
    }

    TransferLimit tl = tlRepo.findByTierAndDest(tier,tmpSKU);
    UsageAccumulator ua = uaRepo.findByOwnerIDAndDest(vaSource.getOwnerID(),tmpSKU);
    Long usage=Long.parseLong(ua.getUsage())+Long.parseLong(amount);
    Long totalAmount=Long.parseLong(amount)+Long.parseLong(fee);

//    BalanceVO balanceVO = new BalanceVO();
//    balanceVO.setAccountNo(vaSource.getVa());
//    OgpBalanceRespVO ogpBalanceRespVO= ogpService.balance(balanceVO);
//
//    if(!ogpBalanceRespVO.getGetBalanceResponse().getParameters().getResponseCode().equals("0001")){
//      throw new NostraException("Failed when check balance", StatusCode.ERROR);
//    }
//    if(Long.parseLong(ogpBalanceRespVO.getGetBalanceResponse().getParameters().getAccountBalance()) < totalAmount){
//      throw new NostraException("Insuficient Balance", StatusCode.ERROR);
//    }

    if(usage>Long.parseLong(tl.getTransactionLimit())){
      String additionalLimit=ilService.checkIncreaseLimitResVO(pmVOSource.getPartnerMember().getId().toString(), tmpSKU);
      if(additionalLimit==null){
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
    String referenceNumber = ogpService.getCustomerReferenceNumber(currentTime);
    th.setAmount(amount);
    th.setAdminFee(fee);
    th.setInterBankFee("0");
    th.setTotalAmount(totalAmount.toString());
    th.setCurrency("IDR");
    th.setDebitAcc(vaSource.getVa());
    th.setStatus("Pending");
    th.setRemark("Pending for confirmation");
    th.setTid(referenceNumber);
    th.setSku(sku);
    th.setPaymentMethod(paymentMethod);
    th.setChargingModelId(chargingModel);
    TransactionHistoryStaging savedTH=thSTGRepo.save(th);

    vo.setAmount(amount);
    vo.setSourceAccount(vaSource.getVa());
    vo.setSourceName(pmVOSource.getPartnerMember().getName());
    vo.setTid(savedTH.getTid());
    vo.setAdminFee(savedTH.getAdminFee());
    vo.setInterBankFee(savedTH.getInterBankFee());
    vo.setTotalAmount(savedTH.getTotalAmount());

    return vo;

  }

  public boolean isCutOff(String currentTime){
    try {
      String cutoffStart = "15:00:00";
      Date time1 = new SimpleDateFormat("HH:mm:ss").parse(cutoffStart);
      Calendar calendar1 = Calendar.getInstance();
      calendar1.setTime(time1);
      calendar1.add(Calendar.DATE, 1);
      System.out.println(calendar1.getTime());


      String cutoffEnd = "06:30:00";
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
        return true;
      }else{
        return false;
      }
    } catch (ParseException e) {
      e.printStackTrace();
      return true;
    }
  }


  public TransferKasproBankResVO transferKasproBank(TransferKasproBankReqVO vo){
    TransactionHistoryStaging thSTG=thSTGRepo.findByTID(vo.getTid(),"KasproBank");
    TransactionHistory th = new TransactionHistory();
    if(thSTG==null){
      throw new NostraException(vo.getTid()+" Transaction ID is not found",StatusCode.DATA_NOT_FOUND);
    }else {
      thSTGRepo.delete(thSTG);
      thSTGRepo.flush();
      th=ogpConverter.convertTransactionHistory(thSTG);
    }
    InitDB x  = InitDB.getInstance();
    String method=x.get("Code.PaymentMethod."+th.getPaymentMethod());

    String escrow="0115476151";
    String feeAccount="0115476151";
    try{
      logger.info(vo.getTid()+" Starting transfer to escrow account: "+feeAccount+" with amount: "+th.getAdminFee());
      InHousePaymentVO ihpVO=new InHousePaymentVO();
      ihpVO.setAmount(th.getAdminFee());
      ihpVO.setDebitAccountNo(th.getDebitAcc());
      ihpVO.setCreditAccountNo(feeAccount);
      ihpVO.setChargingModelId(th.getChargingModelId());
      ihpVO.setPaymentMethod(method);
      ihpVO.setRemark("Transfer Admin Fee to Fee Account");
      String resIHP=inHousePayment(ihpVO);
      logger.info(vo.getTid()+" Finished transfer to Fee account with response: "+resIHP);
    }catch (Exception e){
      th.setStatus("Error");
      th.setRemark("Exception during transfer to Fee account");
      thRepo.saveAndFlush(th);
      throw new NostraException(vo.getTid()+" Exception during transfer to Fee account",StatusCode.ERROR);
    }
    try{
      logger.info(vo.getTid()+" Starting transfer to escrow account: "+escrow+" with amount: "+th.getAmount());
      InHousePaymentVO ihpVO=new InHousePaymentVO();
      ihpVO.setAmount(th.getAmount());
      ihpVO.setDebitAccountNo(th.getDebitAcc());
      ihpVO.setCreditAccountNo(escrow);
      ihpVO.setChargingModelId(th.getChargingModelId());
      ihpVO.setPaymentMethod(method);
      ihpVO.setRemark("Transfer from source account to escrow account");
      String resIHP=inHousePayment(ihpVO);
      logger.info(vo.getTid()+" Finished transfer to escrow account with response: "+resIHP);
    }catch (Exception e){
      th.setStatus("Error");
      th.setRemark("Exception during transfer to escrow account");
      thRepo.saveAndFlush(th);
      throw new NostraException(vo.getTid()+" Exception during transfer to escrow account",StatusCode.ERROR);
    }
    try{
      logger.info(vo.getTid()+" Starting transfer to destination account: "+th.getCreditAcc()+" with amount: "+th.getAmount());
      InHousePaymentVO ihpVO=new InHousePaymentVO();
      ihpVO.setAmount(th.getAmount());
      ihpVO.setDebitAccountNo(escrow);
      ihpVO.setCreditAccountNo(th.getDebitAcc());
      ihpVO.setChargingModelId(th.getChargingModelId());
      ihpVO.setPaymentMethod(method);
      ihpVO.setRemark("Transfer from escrow account to destination account");
      String resIHP=inHousePayment(ihpVO);
      logger.info(vo.getTid()+" Finished transfer to destination account with response: "+resIHP);
    }catch (Exception e){
      th.setStatus("Error");
      th.setRemark("Exception during transfer to destination account");
      thRepo.saveAndFlush(th);
      throw new NostraException(vo.getTid()+" Exception during transfer to destination account",StatusCode.ERROR);
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
    return resVO;
  }


  public TransferKasproBankResVO transferBNI(TransferKasproBankReqVO vo){
    TransactionHistoryStaging thSTG=thSTGRepo.findByTID(vo.getTid(),"BNI");
    TransactionHistory th = new TransactionHistory();
    if(thSTG==null){
      throw new NostraException(vo.getTid()+" Transaction ID is not found",StatusCode.DATA_NOT_FOUND);
    }else {
      thSTGRepo.delete(thSTG);
      thSTGRepo.flush();
      th=ogpConverter.convertTransactionHistory(thSTG);
    }
    InitDB x  = InitDB.getInstance();
    String method=x.get("Code.PaymentMethod."+th.getPaymentMethod());

    String feeAccount="0115476151";
    try{
      logger.info(vo.getTid()+" Starting transfer to fee account: "+feeAccount+" with amount: "+th.getAdminFee());
      InHousePaymentVO ihpVO=new InHousePaymentVO();
      ihpVO.setAmount(th.getAdminFee());
      ihpVO.setDebitAccountNo(th.getDebitAcc());
      ihpVO.setCreditAccountNo(feeAccount);
      ihpVO.setChargingModelId(th.getChargingModelId());
      ihpVO.setPaymentMethod(method);
      ihpVO.setRemark("Transfer Admin Fee to Fee Account");
      String resIHP=inHousePayment(ihpVO);
      logger.info(vo.getTid()+" Finished transfer to Fee account with response: "+resIHP);
    }catch (Exception e){
      th.setStatus("Error");
      th.setRemark("Exception during transfer to Fee account");
      thRepo.save(th);
      throw new NostraException(vo.getTid()+" Exception during transfer to Fee account",StatusCode.ERROR);
    }

    try{
      logger.info(vo.getTid()+" Starting transfer to destination account: "+th.getCreditAcc()+" with amount: "+th.getAmount());
      InHousePaymentVO ihpVO=new InHousePaymentVO();
      ihpVO.setAmount(th.getAmount());
      ihpVO.setDebitAccountNo(th.getDebitAcc());
      ihpVO.setCreditAccountNo(th.getCreditAcc());
      ihpVO.setChargingModelId(th.getChargingModelId());
      ihpVO.setPaymentMethod(method);
      ihpVO.setRemark("Transfer from Source account to destination account");
      String resIHP=inHousePayment(ihpVO);
      logger.info(vo.getTid()+" Finished transfer to destination account with response: "+resIHP);
    }catch (Exception e){
      th.setStatus("Error");
      th.setRemark("Exception during transfer to destination account");
      thRepo.save(th);
      throw new NostraException(vo.getTid()+" Exception during transfer to destination account",StatusCode.ERROR);
    }
    th.setStatus("Success");
    th.setRemark("Success");
    thRepo.save(th);

    VirtualAccount vaSource=new VirtualAccount();
    vaSource=vaRepo.findByVA(th.getDebitAcc());
    UsageAccumulator ua = uaRepo.findByOwnerIDAndDest(vaSource.getOwnerID(),"BNI");
    Long usage =Long.parseLong(ua.getUsage())+Long.parseLong(th.getAmount());
    ua.setUsage(usage.toString());
    uaRepo.save(ua);

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
    return resVO;
  }

  public TransferKasproBankResVO transferOtherBank(TransferKasproBankReqVO vo){
    TransactionHistoryStaging thSTG=thSTGRepo.findOtherBank(vo.getTid());
    TransactionHistory th = new TransactionHistory();
    if(thSTG==null){
      throw new NostraException(vo.getTid()+" Transaction ID is not found",StatusCode.DATA_NOT_FOUND);
    }else {
      thSTGRepo.delete(thSTG);
      thSTGRepo.flush();
      th=ogpConverter.convertTransactionHistory(thSTG);
    }
    InitDB x  = InitDB.getInstance();
    String method=x.get("Code.PaymentMethod."+th.getPaymentMethod());
    logger.info("Payment Method = "+method);

    String feeAccount="0115476151";

    try{
      logger.info(vo.getTid()+" Starting transfer to fee account: "+feeAccount+" with amount: "+th.getAdminFee());
      InHousePaymentVO ihpVO=new InHousePaymentVO();
      ihpVO.setAmount(th.getAdminFee());
      ihpVO.setDebitAccountNo(th.getDebitAcc());
      ihpVO.setCreditAccountNo(feeAccount);
      ihpVO.setChargingModelId(th.getChargingModelId());
      ihpVO.setPaymentMethod("0");
      ihpVO.setRemark("Transfer Admin Fee to Fee Account");
      String resIHP=inHousePayment(ihpVO);
      logger.info(vo.getTid()+" Finished transfer to Fee account with response: "+resIHP);
    }catch (Exception e){
      th.setStatus("Error");
      th.setRemark("Exception during transfer to Fee account");
      thRepo.save(th);
      throw new NostraException(vo.getTid()+" Exception during transfer to Fee account",StatusCode.ERROR);
    }

    if(method.equals("1") || method.equals("2")){
      try{
        logger.info(vo.getTid()+" Starting transfer to destiantion account: "+th.getCreditAcc()+" with amount: "+th.getAmount());
        InHousePaymentVO ihpVO=new InHousePaymentVO();
        ihpVO.setAmount(th.getAmount());
        ihpVO.setDebitAccountNo(th.getDebitAcc());
        ihpVO.setCreditAccountNo(th.getCreditAcc());
        ihpVO.setChargingModelId(th.getChargingModelId());
        ihpVO.setPaymentMethod(method);
        ihpVO.setDestinationBankCode(th.getDestinationBankCode());
        ihpVO.setRemark("Transfer to destination Account");
        String resIHP=inHousePayment(ihpVO);
        logger.info(vo.getTid()+" Finished transfer to destination account with response: "+resIHP);
      }catch (Exception e){
        th.setStatus("Error");
        th.setRemark("Exception during transfer to Destination account");
        thRepo.save(th);
        throw new NostraException(vo.getTid()+" Exception during transfer to Destination account",StatusCode.ERROR);
      }
    }else if(method.equals("0")){
      try{
        logger.info(vo.getTid()+" Starting transfer to destiantion account: "+th.getCreditAcc()+" with amount: "+th.getAmount());
        InterBankPaymentVO ibpVO=new InterBankPaymentVO();
        ibpVO.setAccountNo(th.getDebitAcc());
        ibpVO.setAmount(th.getAmount());
        ibpVO.setDestinationAccountName(th.getCreditName());
        ibpVO.setDestinationAccountNo(th.getCreditAcc());
        ibpVO.setDestinationBankCode(th.getDestinationBankCode());
        ibpVO.setDestinationBankName(th.getSku());
        ibpVO.setRetrievalReffNo(th.getTid());
        OgpInterBankPaymentRespVO ibpResVO=ogpService.interBankPayment(ibpVO);
        logger.info(vo.getTid()+" Finished transfer to destination account with response: "+ibpResVO.getGetInterbankPaymentResponse().getParameters().getResponseMessage());
      }catch (Exception e){
        th.setStatus("Error");
        th.setRemark("Exception during transfer to Destination account");
        thRepo.save(th);
        throw new NostraException(vo.getTid()+" Exception during transfer to Destination account",StatusCode.ERROR);
      }
    }

    th.setStatus("Success");
    th.setRemark("Success");
    thRepo.save(th);

    VirtualAccount vaSource=vaRepo.findByVA(th.getDebitAcc());
    UsageAccumulator ua = uaRepo.findByOwnerIDAndDest(vaSource.getOwnerID(),"OtherBank");
    Long usage =Long.parseLong(ua.getUsage())+Long.parseLong(th.getAmount());
    ua.setUsage(usage.toString());
    uaRepo.save(ua);

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
    return resVO;

  }

  @Transactional
  public TransferKasproBankResVO transferKaspro(TransferKasproBankReqVO vo){
    TransactionHistoryStaging thSTG=thSTGRepo.findByTID(vo.getTid(),"Kaspro");
    TransactionHistory th = new TransactionHistory();
    if(thSTG==null){
      throw new NostraException(vo.getTid()+" Transaction ID is not found",StatusCode.DATA_NOT_FOUND);
    }else {
      thSTGRepo.delete(thSTG);
      thSTGRepo.flush();
      th=ogpConverter.convertTransactionHistory(thSTG);
    }
    InitDB x  = InitDB.getInstance();
    String method=x.get("Code.PaymentMethod."+th.getPaymentMethod());

    String feeAccount="0115476151";
    String custodian="0115476151";
    try{
      logger.info(vo.getTid()+" Starting transfer to fee account: "+feeAccount+" with amount: "+th.getAdminFee());
      InHousePaymentVO ihpVO=new InHousePaymentVO();
      ihpVO.setAmount(th.getAdminFee());
      ihpVO.setDebitAccountNo(th.getDebitAcc());
      ihpVO.setCreditAccountNo(feeAccount);
      ihpVO.setChargingModelId(th.getChargingModelId());
      ihpVO.setPaymentMethod(method);
      ihpVO.setRemark("Transfer Admin Fee to Fee Account");
      String resIHP=inHousePayment(ihpVO);
      logger.info(vo.getTid()+" Finished transfer to Fee account with response: "+resIHP);
    }catch (Exception e){
      th.setStatus("Error");
      th.setRemark("Exception during transfer to Fee account");
      thRepo.save(th);
      throw new NostraException(vo.getTid()+" Exception during transfer to Fee account",StatusCode.ERROR);
    }

    try{
      logger.info(vo.getTid()+" Starting transfer to custodian account: "+custodian+" with amount: "+th.getAmount());
      InHousePaymentVO ihpVO=new InHousePaymentVO();
      ihpVO.setAmount(th.getAmount());
      ihpVO.setDebitAccountNo(th.getDebitAcc());
      ihpVO.setCreditAccountNo(th.getCreditAcc());
      ihpVO.setChargingModelId(th.getChargingModelId());
      ihpVO.setPaymentMethod(method);
      ihpVO.setRemark("Transfer from Source account to custodian account");
      String resIHP=inHousePayment(ihpVO);
      logger.info(vo.getTid()+" Finished transfer to custodian account with response: "+resIHP);
    }catch (Exception e){
      th.setStatus("Error");
      th.setRemark("Exception during transfer to destination account");
      thRepo.save(th);
      throw new NostraException(vo.getTid()+" Exception during transfer to destination account",StatusCode.ERROR);
    }

    logger.info(vo.getTid()+" Start doing Cash In");
    String requestId=ogpService.getValueDate(new Date()).concat(th.getDebitAcc());
    String body="{\"payments\": [\n" +
            "        {\n" +
            "            \"pocket-id\": \"2\",\n" +
            "            \"amount\": \""+th.getAmount()+"\",\n" +
            "            \"reference\": \"CashIn-"+requestId+"\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"auth\": {\n" +
            "        \"pass\": \"1234\"\n" +
            "    },\n" +
            "    \"destination\": \""+th.getCreditAcc()+"\",\n" +
            "    \"request-id\": \""+requestId+"\"\n" +
            "}";
    String resCashIn="";
    try {
      resCashIn=httpProcessingService.kasproCashIn(body);
      JSONObject resCashInJSON=new JSONObject(resCashIn);
      if(resCashInJSON.getInt("code")!=0){
        th.setStatus("Error while doin Cash In");
        th.setRemark(resCashInJSON.getString("message"));
        thRepo.saveAndFlush(th);
        throw new NostraException(vo.getTid()+" "+resCashInJSON.getString("message"),StatusCode.ERROR);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    logger.info(vo.getTid()+" Finished doing Cash In");

    th.setStatus("Success");
    th.setRemark("Success");
    thRepo.save(th);

    VirtualAccount vaSource=new VirtualAccount();
    vaSource=vaRepo.findByVA(th.getDebitAcc());
    UsageAccumulator ua = uaRepo.findByOwnerIDAndDest(vaSource.getOwnerID(),"Kaspro");
    Long usage =Long.parseLong(ua.getUsage())+Long.parseLong(th.getAmount());
    ua.setUsage(usage.toString());
    uaRepo.save(ua);

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
    return resVO;
  }

}
