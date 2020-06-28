package com.kaspro.bank.vo.ogp;

import lombok.Data;

@Data
public class OgpPaymentStatusRespVO {
  private PaymentStatusResponse getPaymentStatusResponse;

  @Data
  public static class PaymentStatusResponse {
    private Parameter parameters;

    @Data
    public static class Parameter {
      private String responseCode;
      private String responseMessage;
      private String bankReference;
      private String customerReference;
      private PreviousResponse previousResponse;

      @Data
      public static class PreviousResponse {
        private String transactionStatus;
        private String debitAccountNo;
        private String creditAccountNo;
        private String valueAmount;
        private String valueCurrency;
      }
    }
  }

}
