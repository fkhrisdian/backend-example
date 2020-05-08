package com.kaspro.bank.persistance.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "TRANSFER_LIMIT")
@DynamicUpdate
@Data
public class TransferLimit extends Base {
}
