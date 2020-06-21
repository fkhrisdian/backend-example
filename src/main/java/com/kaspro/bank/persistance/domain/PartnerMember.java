package com.kaspro.bank.persistance.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "PARTNER_MEMBER")
@DynamicUpdate
@Data
public class PartnerMember extends Base {
  @Column(name = "NAMA",nullable = false)
  private String name;

  @Column(name = "PARTNER_ALIAS")
  private String partnerAlias;

  @Column(name = "ALAMAT")
  private String address;

  @Column(name = "NIB_SIP_TDP")
  private String nibSipTdp;

  @Column(name = "NO_AKTA_PENDIRIAN")
  private String noAktaPendirian;

  @Column(name = "NPWP")
  private String npwp;


  @Column(name = "STATUS")
  private String status;

  @Column(name = "PARTNER_MEMBER_CODE")
  private String partnerMemberCode;

  @ManyToOne
  @JoinColumn(name = "PARTNER_ID")
  private Partner partner;
}
