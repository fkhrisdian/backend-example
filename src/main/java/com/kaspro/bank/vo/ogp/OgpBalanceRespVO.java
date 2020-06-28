package com.kaspro.bank.vo.ogp;

import lombok.Data;

@Data
public class OgpBalanceRespVO {
  private BalanceResponse getBalanceResponse;

  @Data
  public static class BalanceResponse {
    private Parameter parameters;

    @Data
    public static class Parameter {
      private String responseCode;
      private String responseMessage;
      private String customerName;
      private String accountCurrency;
      private String accountBalance;
    }
  }
}
