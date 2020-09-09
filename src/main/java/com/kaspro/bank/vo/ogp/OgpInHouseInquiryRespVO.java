package com.kaspro.bank.vo.ogp;

import lombok.Data;

@Data
public class OgpInHouseInquiryRespVO {
  private InquiryResponse getInHouseInquiryResponse;

  @Data
  public static class InquiryResponse {
    private String clientId;
    private Parameter parameters;

    @Data
    public static class Parameter {
      private String responseCode;
      private String responseMessage;
      private String customerName;
      private String accountCurrency;
      private String accountNumber;
      private String accountStatus;
      private String accountType;
      private String responseTimestamp;
    }
  }
}
