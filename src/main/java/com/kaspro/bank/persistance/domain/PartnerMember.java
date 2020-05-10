package com.kaspro.bank.persistance.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PARTNER_MEMBER")
@DynamicUpdate
@Data
public class PartnerMember extends Base {
  @Column(name = "COMPANY_NAME")
  private String companyName;

  @Column(name = "COMPANY_ADDRESS")
  private String companyAddress;

  @Column(name = "NIB_SIP_TDP")
  private String nibSipTdp;

  @Column(name = "NO_AKTA_PENDIRIAN")
  private String noAktaPendirian;

  @Column(name = "NO_NPWP")
  private String npwp;

  @Column
  private String va;

  @Column(name = "PIC_FULL_NAME")
  private String picFullName;

  @Column(name = "PIC_EMAIL")
  private String picEmail;

  @Column(name = "PIC_ADDRESS")
  private String picAddress;

  @Column(name = "PIC_NO_KTP")
  private String picKtp;

  @Column(name = "PIC_NO_NPWP")
  private String picNpwp;

  @Column(name = "PIC_PHONE_NO")
  private String picPhone;

  @Column
  private String status;

  @Column(name = "PAYMENT_FEE_METHOD")
  private String paymentFeeMethod;

  @Column(name = "TIER_TYPE")
  private int tierType;

  @Column(name = "TRANSFER_SETTINGS")
  private String transferSettings;

  @ManyToOne(fetch= FetchType.LAZY)
  @JoinColumn(name = "PARTNER_ID")
  private Partner partner;
}
