package com.kaspro.bank.vo.ogp;

import lombok.Data;

@Data
public class OgpInterBankPaymentRespVO {
  private PaymentResponse getInterbankPaymentResponse;

  @Data
  public static class PaymentResponse {
    private String clientId;
    private Parameter parameters;

    @Data
    public static class Parameter {
      private String responseCode;
      private String responseMessage;
      private String errorMessage;
      private String responseTimestamp;
      private String destinationAccountNum;
      private String destinationAccountName;
      private String customerReffNum;
      private String accountName;
    }
  }
}
