package com.kaspro.bank.vo.ogp;

import lombok.Data;

@Data
public class OgpInterBankInquiryRespVO {
  private InquiryResponse getInterbankInquiryResponse;

  @Data
  public static class InquiryResponse {
    private String clientId;
    private Parameter parameters;

    @Data
    public static class Parameter {
      private String responseCode;
      private String responseMessage;
      private String destinationAccountName;
      private String destinationAccountNum;
      private String destinationBankName;
      private String retrievalReffNum;
      private String responseTimestamp;
    }
  }

}
