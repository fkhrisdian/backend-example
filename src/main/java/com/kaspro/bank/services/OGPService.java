package com.kaspro.bank.services;

import com.google.gson.Gson;
import com.kaspro.bank.converter.OGPConverter;
import com.kaspro.bank.vo.BalanceVO;
import com.kaspro.bank.vo.InHouseInquiryVO;
import com.kaspro.bank.vo.InHousePaymentVO;
import com.kaspro.bank.vo.InterBankInquiryVO;
import com.kaspro.bank.vo.InterBankPaymentVO;
import com.kaspro.bank.vo.PaymentStatusVO;
import com.kaspro.bank.vo.ogp.OgpBalanceReqVO;
import com.kaspro.bank.vo.ogp.OgpBalanceRespVO;
import com.kaspro.bank.vo.ogp.OgpInHouseInquiryReqVO;
import com.kaspro.bank.vo.ogp.OgpInHouseInquiryRespVO;
import com.kaspro.bank.vo.ogp.OgpInterBankInquiryReqVO;
import com.kaspro.bank.vo.ogp.OgpInterBankInquiryRespVO;
import com.kaspro.bank.vo.ogp.OgpInHousePaymentReqVO;
import com.kaspro.bank.vo.ogp.OgpInHousePaymentRespVO;
import com.kaspro.bank.vo.ogp.OgpInterBankPaymentReqVO;
import com.kaspro.bank.vo.ogp.OgpInterBankPaymentRespVO;
import com.kaspro.bank.vo.ogp.OgpPaymentStatusReqVO;
import com.kaspro.bank.vo.ogp.OgpPaymentStatusRespVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Slf4j
@Service
public class OGPService {

  @Value("${ogp.url.balance}")
  private String ogpBalanceUrl;

  @Value("${ogp.url.inhouse.inquiry}")
  private String ogpInHouseInquiryUrl;

  @Value("${ogp.url.interbank.inquiry}")
  private String ogpInterBankInquiryUrl;

  @Value("${ogp.url.inhouse.payment}")
  private String ogpInHousePaymentUrl;

  @Value("${ogp.url.interbank.payment}")
  private String ogpInterBankPaymentUrl;

  @Value("${ogp.url.payment.status}")
  private String ogpPaymentStatusUrl;

  @Value("${ogp.client.id}")
  private String ogpClientId;

  @Autowired
  OGPEncryptionService encryptionService;

  @Autowired
  OGPHttpService ogpHttpService;

  @Autowired
  OGPConverter OGPConverter;

  private Gson gson = new Gson();

  public OgpBalanceRespVO balance(BalanceVO vo) {
    OgpBalanceReqVO request = OGPConverter.convertBalance(
        vo, ogpClientId, encryptionService.encrypt(ogpClientId + vo.getAccountNo()));

    String responseBody = ogpHttpService.callHttpPost(ogpBalanceUrl, request);
    return gson.fromJson(responseBody, OgpBalanceRespVO.class);
  }

  public OgpInHouseInquiryRespVO inHouseInquiry(InHouseInquiryVO vo) {
    OgpInHouseInquiryReqVO request = OGPConverter.convertInHouseInquiry(
        vo, ogpClientId, encryptionService.encrypt(ogpClientId + vo.getAccountNo()));

    String responseBody = ogpHttpService.callHttpPost(ogpInHouseInquiryUrl, request);
    return gson.fromJson(responseBody, OgpInHouseInquiryRespVO.class);
  }

  public OgpInterBankInquiryRespVO interBankInquiry(InterBankInquiryVO vo) {
    OgpInterBankInquiryReqVO request = OGPConverter.convertInterBankInquiry(
        vo, getCustomerReferenceNumber(new Date()), ogpClientId,
        encryptionService.encrypt(
            ogpClientId + vo.getDestinationBankCode() + vo.getDestinationAccountNo() + vo.getAccountNo()));

    String responseBody = ogpHttpService.callHttpPost(ogpInterBankInquiryUrl, request);
    return gson.fromJson(responseBody, OgpInterBankInquiryRespVO.class);
  }

  public OgpInHousePaymentRespVO inHousePayment(InHousePaymentVO vo) {
    Date currentTime = new Date();
    String referenceNumber = getCustomerReferenceNumber(currentTime);
    OgpInHousePaymentReqVO request = OGPConverter.convertInHousePayment(
        vo, getValueDate(currentTime), referenceNumber, ogpClientId,
        encryptionService.encrypt(
            ogpClientId + referenceNumber + "0" + vo.getDebitAccountNo() + vo.getCreditAccountNo() + vo.getAmount() + "IDR"));

    String responseBody = ogpHttpService.callHttpPost(ogpInHousePaymentUrl, request);
    return gson.fromJson(responseBody, OgpInHousePaymentRespVO.class);
  }

  public OgpInterBankPaymentRespVO interBankPayment(InterBankPaymentVO vo) {
    Date currentTime = new Date();
    String referenceNumber = getCustomerReferenceNumber(currentTime);
    OgpInterBankPaymentReqVO request = OGPConverter.convertInterBankPayment(
        vo, referenceNumber, ogpClientId,
        encryptionService.encrypt(
            ogpClientId + vo.getDestinationAccountNo() + vo.getDestinationBankCode() + vo.getAccountNo() + vo.getAmount() + vo.getRetrievalReffNo())
    );

    String responseBody = ogpHttpService.callHttpPost(ogpInterBankPaymentUrl, request);
    return gson.fromJson(responseBody, OgpInterBankPaymentRespVO.class);
  }

  public OgpPaymentStatusRespVO paymentStatus(PaymentStatusVO vo) {
    OgpPaymentStatusReqVO request = OGPConverter.convertPaymentStatus(
        vo, ogpClientId, encryptionService.encrypt(ogpClientId + vo.getCustomerReferenceNumber()));

    String responseBody = ogpHttpService.callHttpPost(ogpPaymentStatusUrl, request);
    return gson.fromJson(responseBody, OgpPaymentStatusRespVO.class);
  }

  private String getValueDate(Date date) {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    return formatter.format(date);
  }

  public String getCustomerReferenceNumber(Date date) {
    Random random = new Random();
    return getValueDate(date).concat(String.valueOf(random.nextInt(900) + 100));
  }
}
