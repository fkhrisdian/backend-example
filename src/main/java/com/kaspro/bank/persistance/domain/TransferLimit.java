package com.kaspro.bank.persistance.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "TRANSFER_LIMIT")
@DynamicUpdate
@Data
public class TransferLimit extends Base {
  @Column(name = "DEST")
  private String destination;

  @Column(name = "TRX_LIMIT")
  private BigDecimal transactionLimit;

  @Column(name = "TIER_TYPE")
  private String tierType;
}
