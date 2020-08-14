package com.kaspro.bank.persistance.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "INCREASE_LIMIT")
@DynamicUpdate
@Data
public class IncreaseLimit extends Base {

  @Column(name = "PARTNER_ID")
  private String partnerId;

  @Column(name = "PARTNER_NAME")
  private String partnerName;

  @Column(name = "MEMBER_ID")
  private String memberId;

  @Column(name = "MEMBER_NAME")
  private String memberName;

  @Column(name = "DESTINATION")
  private String destination;

  @Column(name = "START_DATE")
  private Date startDate;

  @Column(name = "END_DATE")
  private Date endDate;

  @Column(name = "AMOUNT")
  private String amount;

  @Column(name = "STATUS")
  private String status;

}
