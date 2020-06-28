package com.kaspro.bank.vo.ogp;

import lombok.Data;

@Data
public class OgpPaymentStatusReqVO extends OgpBaseReqVO {
  private String customerReferenceNumber;
}
