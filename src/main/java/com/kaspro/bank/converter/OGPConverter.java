package com.kaspro.bank.converter;

import com.kaspro.bank.persistance.domain.TransactionHistory;
import com.kaspro.bank.persistance.domain.TransactionHistoryStaging;
import com.kaspro.bank.vo.BalanceVO;
import com.kaspro.bank.vo.InHouseInquiryVO;
import com.kaspro.bank.vo.InHousePaymentVO;
import com.kaspro.bank.vo.InterBankInquiryVO;
import com.kaspro.bank.vo.InterBankPaymentVO;
import com.kaspro.bank.vo.PaymentStatusVO;
import com.kaspro.bank.vo.ogp.OgpBalanceReqVO;
import com.kaspro.bank.vo.ogp.OgpInHouseInquiryReqVO;
import com.kaspro.bank.vo.ogp.OgpInterBankInquiryReqVO;
import com.kaspro.bank.vo.ogp.OgpInHousePaymentReqVO;
import com.kaspro.bank.vo.ogp.OgpInterBankPaymentReqVO;
import com.kaspro.bank.vo.ogp.OgpPaymentStatusReqVO;
import org.springframework.stereotype.Component;

@Component
public class OGPConverter {

  public OgpBalanceReqVO convertBalance(BalanceVO vo, String ogpClientId, String signature) {
    OgpBalanceReqVO request = new OgpBalanceReqVO();
    request.setAccountNo(vo.getAccountNo());
    request.setClientId(ogpClientId);
    request.setSignature(signature);
    return request;
  }

  public OgpInHouseInquiryReqVO convertInHouseInquiry(InHouseInquiryVO vo, String ogpClientId, String signature) {
    OgpInHouseInquiryReqVO request = new OgpInHouseInquiryReqVO();
    request.setAccountNo(vo.getAccountNo());
    request.setClientId(ogpClientId);
    request.setSignature(signature);
    return request;
  }

  public OgpInterBankInquiryReqVO convertInterBankInquiry(InterBankInquiryVO vo, String refNo, String ogpClientId, String signature) {
    OgpInterBankInquiryReqVO request = new OgpInterBankInquiryReqVO();
    request.setAccountNum(vo.getAccountNo());
    request.setDestinationBankCode(vo.getDestinationBankCode());
    request.setDestinationAccountNum(vo.getDestinationAccountNo());
    request.setCustomerReferenceNumber(refNo);
    request.setClientId(ogpClientId);
    request.setSignature(signature);
    return request;
  }

  public OgpInHousePaymentReqVO convertInHousePayment(InHousePaymentVO vo, String date, String refNo, String ogpClientId, String signature) {
    OgpInHousePaymentReqVO request = new OgpInHousePaymentReqVO();
    request.setPaymentMethod(vo.getPaymentMethod());
    request.setCustomerReferenceNumber(refNo);
    request.setDebitAccountNo(vo.getDebitAccountNo());
    request.setCreditAccountNo(vo.getCreditAccountNo());
    request.setValueDate(date);
    request.setValueAmount(vo.getAmount());
    request.setValueCurrency("IDR");
    request.setRemark(vo.getRemark());
    request.setClientId(ogpClientId);
    request.setSignature(signature);
    if(!vo.getPaymentMethod().equals("0")){
      request.setDestinationBankCode(vo.getDestinationBankCode());
    }
    if(vo.getChargingModelId()==null){
      request.setChargingModelId("OUR");
    }else{
      request.setChargingModelId(vo.getChargingModelId());
    }
    return request;
  }

  public OgpInterBankPaymentReqVO convertInterBankPayment(InterBankPaymentVO vo, String refNo, String ogpClientId, String signature) {
    OgpInterBankPaymentReqVO request = new OgpInterBankPaymentReqVO();
    request.setCustomerReferenceNumber(refNo);
    request.setAmount(vo.getAmount());
    request.setDestinationAccountNum(vo.getDestinationAccountNo());
    request.setDestinationAccountName(vo.getDestinationAccountName());
    request.setDestinationBankCode(vo.getDestinationBankCode());
    request.setDestinationBankName(vo.getDestinationBankName());
    request.setAccountNum(vo.getAccountNo());
    request.setRetrievalReffNum(vo.getRetrievalReffNo());
    request.setClientId(ogpClientId);
    request.setSignature(signature);
    return request;
  }

  public OgpPaymentStatusReqVO convertPaymentStatus(PaymentStatusVO vo, String ogpClientId, String signature) {
    OgpPaymentStatusReqVO request = new OgpPaymentStatusReqVO();
    request.setCustomerReferenceNumber(vo.getCustomerReferenceNumber());
    request.setClientId(ogpClientId);
    request.setSignature(signature);
    return request;
  }

  public TransactionHistory convertTransactionHistory(TransactionHistoryStaging thSTG){
    TransactionHistory th=new TransactionHistory();
    th.setChargingModelId(thSTG.getChargingModelId());
    th.setPaymentMethod(thSTG.getPaymentMethod());
    th.setDebitName(thSTG.getDebitName());
    th.setCreditName(thSTG.getCreditName());
    th.setRemark(thSTG.getRemark());
    th.setStatus(thSTG.getStatus());
    th.setSku(thSTG.getSku());
    th.setTotalAmount(thSTG.getTotalAmount());
    th.setInterBankFee(thSTG.getInterBankFee());
    th.setAdminFee(thSTG.getAdminFee());
    th.setTid(thSTG.getTid());
    th.setCreditAcc(thSTG.getCreditAcc());
    th.setDebitAcc(thSTG.getDebitAcc());
    th.setCurrency(thSTG.getCurrency());
    th.setAmount(thSTG.getAmount());
    th.setBankRef(thSTG.getBankRef());
    th.setCustRef(thSTG.getCustRef());
    th.setDestinationBankCode(thSTG.getDestinationBankCode());
    th.setPartnerId(thSTG.getPartnerId());
    th.setAccType(thSTG.getAccType());
    th.setSenderId(thSTG.getSenderId());
    th.setMsisdn(thSTG.getMsisdn());
    th.setPartnerName(thSTG.getPartnerName());
    th.setSender(thSTG.getSender());
    th.setDest(thSTG.getDest());

    return th;
  }
}
