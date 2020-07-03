package com.kaspro.bank.persistance.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigInteger;

@Entity
@Table(name = "USAGE_ACCUMULATOR")
@DynamicUpdate
@Data
public class UsageAccumulator extends Base{
    @Column(name = "OWNER_ID")
    private String ownerId;

    @Column(name = "TIER")
    private String tier;

    @Column(name = "DEST")
    private String destination;

    @Column(name = "USAGE")
    private BigInteger usage;

}
