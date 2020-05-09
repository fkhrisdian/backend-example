package com.kaspro.bank.persistance.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "TRANSFER_FEE")
@DynamicUpdate
@Data
public class TransferFee extends Base {
  @Column(name = "DEST")
  private String destination;

  @Column(name = "FEE")
  private BigDecimal fee;
}
