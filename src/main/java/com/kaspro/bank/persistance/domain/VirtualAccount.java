package com.kaspro.bank.persistance.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "VIRTUAL_ACCOUNT")
@DynamicUpdate
@Data
public class VirtualAccount extends Base{
    @Column(name = "VA",nullable = false)
    private String va;

    @Column(name = "STATUS",nullable = false)
    private String status;

    @Column(name = "FLAG",nullable = false)
    private String flag;

    @Column(name = "MSISDN",nullable = false)
    private String msisdn;

    @Column(name = "START_EFF_DATE",nullable = false)
    private Date startEffDate;

    @Column(name = "END_EFF_DATE",nullable = false)
    private Date endEffDate;

    @OneToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "PARTNER_ID")
    private Partner partner;

}
