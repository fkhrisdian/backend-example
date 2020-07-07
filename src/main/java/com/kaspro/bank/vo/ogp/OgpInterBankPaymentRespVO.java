package com.kaspro.bank.vo.ogp;

import lombok.Data;

@Data
public class OgpInterBankPaymentRespVO {
  private PaymentResponse getInterbankPaymentResponse;

  @Data
  public static class PaymentResponse {
    private Parameter parameters;

    @Data
    public static class Parameter {
      private String responseCode;
      private String responseMessage;
      private String destinationAccountNum;
      private String destinationAccountName;
      private String destinationBankName;
      private String retrievalReffNum;
    }
  }
}
