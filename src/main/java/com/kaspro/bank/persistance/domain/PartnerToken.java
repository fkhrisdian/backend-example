package com.kaspro.bank.persistance.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "PARTNER_TOKEN")
@DynamicUpdate
@Data
public class PartnerToken extends Base{

}
