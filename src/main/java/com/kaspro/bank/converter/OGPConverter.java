package com.kaspro.bank.converter;

import com.kaspro.bank.vo.BalanceVO;
import com.kaspro.bank.vo.InHouseInquiryVO;
import com.kaspro.bank.vo.InHousePaymentVO;
import com.kaspro.bank.vo.InterBankInquiryVO;
import com.kaspro.bank.vo.ogp.OgpBalanceReqVO;
import com.kaspro.bank.vo.ogp.OgpInHouseInquiryReqVO;
import com.kaspro.bank.vo.ogp.OgpInterBankInquiryReqVO;
import com.kaspro.bank.vo.ogp.OgpInHousePaymentReqVO;
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

  public OgpInHousePaymentReqVO convertPayment(InHousePaymentVO vo, String date, String refNo, String ogpClientId, String signature) {
    OgpInHousePaymentReqVO request = new OgpInHousePaymentReqVO();
    request.setPaymentMethod("0");
    request.setCustomerReferenceNumber(refNo);
    request.setDebitAccountNo(vo.getDebitAccountNo());
    request.setCreditAccountNo(vo.getCreditAccountNo());
    request.setValueDate(date);
    request.setValueAmount(vo.getAmount());
    request.setValueCurrency("IDR");
    request.setRemark(vo.getRemark());
    request.setClientId(ogpClientId);
    request.setSignature(signature);
    return request;
  }
}
