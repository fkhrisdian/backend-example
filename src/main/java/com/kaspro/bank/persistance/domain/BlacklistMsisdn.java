package com.kaspro.bank.persistance.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name = "BLACKLIST_MSISDN")
@DynamicUpdate
@Data
public class BlacklistMsisdn extends Base{
    @Column(name = "MSISDN",nullable = false)
    private String msisdn;

    @Column(name = "REASON",nullable = false)
    private String reason;

    @Column(name = "NAME",nullable = false)
    private String name;

    @Column(name = "EMAIl",nullable = false)
    private String email;

    @Column(name = "VA",nullable = false)
    private String va;

}