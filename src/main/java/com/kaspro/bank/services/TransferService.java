package com.kaspro.bank.services;

import com.kaspro.bank.enums.StatusCode;
import com.kaspro.bank.exception.NostraException;
import com.kaspro.bank.persistance.domain.*;
import com.kaspro.bank.persistance.repository.*;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class TransferService {

  @Autowired
  OGPService ogpService;

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
  PartnerService pService;

  @Autowired
  PartnerRepository pRepo;

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

  @Transactional
  public InquiryKasproBankResVO kasproBankInquiry(String source, String destination, String sku, String amount){
    VirtualAccount va=vaRepo.findByMsisdn(destination,"CPM");
    if(va==null) {
      throw new NostraException("Destination account is not found", StatusCode.DATA_NOT_FOUND);
    }
    RegisterPartnerMemberVO pmVO = pmService.findDetail(va.getOwnerID());

    VirtualAccount vaSource=vaRepo.findByMsisdn(source,"CPM");
    RegisterPartnerMemberVO pmVOSource = pmService.findDetail(vaSource.getOwnerID());
    RegisterPartnerVO pVOSource=pService.findDetail(pmVOSource.getPartnerMember().getPartner().getId());

    String fee="";
    for(TransferFee tf:pVOSource.getTransferFees()){
      if(tf.getDestination().equals(sku)){
        fee=tf.getFee().toString().replaceAll("\\.0*$", "");
        break;
      }
    }

    String tier="";
    for(TransferInfoMember tim:pmVOSource.getListTransferInfoMember()){
      if(tim.getName().equals("TierLimit")){
        tier=tim.getValue();
        break;
      }
    }

    TransferLimit tl = tlRepo.findByTierAndDest(tier,sku);
    UsageAccumulator ua = uaRepo.findByOwnerIDAndDest(vaSource.getOwnerID(),sku);
    Long usage=Long.parseLong(ua.getUsage())+Long.parseLong(amount);
    Long totalAmount=Long.parseLong(amount)+Long.parseLong(fee);

    if(usage>Long.parseLong(tl.getTransactionLimit())){
      throw new NostraException("Transfer Limit is exceeded", StatusCode.ERROR);
    }else if(!pmVO.getPartnerMember().getStatus().equals("ACTIVE")){
      throw new NostraException("Destination Account is not active", StatusCode.ERROR);
    }else{
      TransactionHistory th=new TransactionHistory();
      java.util.Date currentTime = new java.util.Date();
      String referenceNumber = ogpService.getCustomerReferenceNumber(currentTime);
      th.setAmount(amount);
      th.setAdminFee(fee);
      th.setInterBankFee("0");
      th.setTotalAmount(totalAmount.toString());
      th.setCreditAcc(va.getVa());
      th.setCurrency("IDR");
      th.setDebitAcc(vaSource.getVa());
      th.setStatus("Pending");
      th.setRemark("Pending for confirmation");
      th.setTid(referenceNumber);
      TransactionHistory savedTH=thRepo.save(th);

      InquiryKasproBankResVO vo = new InquiryKasproBankResVO();
      vo.setAmount(amount);
      vo.setDestinationAccount(va.getVa());
      vo.setDestinationName(pmVO.getPartnerMember().getName());
      vo.setSourceAccount(vaSource.getVa());
      vo.setSourceName(pmVOSource.getPartnerMember().getName());
      vo.setTid(savedTH.getTid());
      vo.setAdminFee(savedTH.getAdminFee());
      vo.setInterBankFee(savedTH.getInterBankFee());
      vo.setTotalAmount(savedTH.getTotalAmount());


      return vo;
    }
  }

  @Transactional
  public TransferKasproBankResVO transferKasproBank(TransferKasproBankReqVO vo){
    TransactionHistory th=thRepo.findByTID(vo.getTid());
    if(th==null){
      throw new NostraException("Transaction ID is not found",StatusCode.DATA_NOT_FOUND);
    }
    String escrow="0115476151";
    String feeAccount="0115476151";
    try{
      logger.info("Starting transfer to escrow account: "+feeAccount+" with amount: "+th.getAdminFee());
      InHousePaymentVO ihpVO=new InHousePaymentVO();
      ihpVO.setAmount(th.getAdminFee());
      ihpVO.setDebitAccountNo(th.getDebitAcc());
      ihpVO.setCreditAccountNo(feeAccount);
      ihpVO.setRemark("Transfer Admin Fee to Fee Account");
      String resIHP=inHousePayment(ihpVO);
      logger.info("Finished transfer to Fee account with response: "+resIHP);
    }catch (Exception e){
      th.setStatus("Error");
      th.setRemark("Exception during transfer to Fee account");
      thRepo.save(th);
      throw new NostraException("Exception during transfer to Fee account",StatusCode.ERROR);
    }
    try{
      logger.info("Starting transfer to escrow account: "+escrow+" with amount: "+th.getAmount());
      InHousePaymentVO ihpVO=new InHousePaymentVO();
      ihpVO.setAmount(th.getAmount());
      ihpVO.setDebitAccountNo(th.getDebitAcc());
      ihpVO.setCreditAccountNo(escrow);
      ihpVO.setRemark("Transfer from source account to escrow account");
      String resIHP=inHousePayment(ihpVO);
      logger.info("Finished transfer to escrow account with response: "+resIHP);
    }catch (Exception e){
      th.setStatus("Error");
      th.setRemark("Exception during transfer to escrow account");
      thRepo.save(th);
      throw new NostraException("Exception during transfer to escrow account",StatusCode.ERROR);
    }
    try{
      logger.info("Starting transfer to destination account: "+th.getCreditAcc()+" with amount: "+th.getAmount());
      InHousePaymentVO ihpVO=new InHousePaymentVO();
      ihpVO.setAmount(th.getAmount());
      ihpVO.setDebitAccountNo(escrow);
      ihpVO.setCreditAccountNo(th.getDebitAcc());
      ihpVO.setRemark("Transfer from escrow account to destination account");
      String resIHP=inHousePayment(ihpVO);
      logger.info("Finished transfer to destination account with response: "+resIHP);
    }catch (Exception e){
      th.setStatus("Error");
      th.setRemark("Exception during transfer to destination account");
      thRepo.save(th);
      throw new NostraException("Exception during transfer to destination account",StatusCode.ERROR);
    }
    th.setStatus("Success");
    th.setRemark("Success");
    thRepo.save(th);

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
