package com.kaspro.bank.persistance.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "TRANSFER_FEE")
@DynamicUpdate
@Data
public class TransferFee extends Base {
}
