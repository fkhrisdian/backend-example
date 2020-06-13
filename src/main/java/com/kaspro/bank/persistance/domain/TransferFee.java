package com.kaspro.bank.persistance.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import javax.persistence.*;

@Entity
@Table(name = "TRANSFER_FEE")
@DynamicUpdate
@Data
public class TransferFee extends Base {

  @Column(name = "DEST")
  private String destination;

  @Column(name = "FEE")
  private BigDecimal fee;

  @Column(name = "OWNER_ID",nullable = false)
  private int ownerID;
}
