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
@Table(name = "MEMBER_DOCUMENT")
@DynamicUpdate
@Data
public class PartnerMemberDocument extends Base {
  @Column(name = "DOC_TYPE")
  private String documentType;

  @Column(name = "DOC_PATH")
  private String documentPath;

  @ManyToOne(fetch= FetchType.LAZY)
  @JoinColumn(name = "MEMBER_ID")
  private PartnerMember partnerMember;
}
