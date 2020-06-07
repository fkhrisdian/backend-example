package com.kaspro.bank.persistance.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "DATA_PIC")
@DynamicUpdate
@Data
public class DataPIC extends Base{
    @Column(name = "NAMA",nullable = false)
    private String name;

    @Column(name = "ALAMAT",nullable = false)
    private String alamat;

    @Column(name = "KTP",nullable = false)
    private String ktp;

    @Column(name = "NPWP",nullable = false)
    private String npwp;

    @Column(name = "MSISDN",nullable = false)
    private String msisdn;

    @Column(name = "EMAIL",nullable = false)
    private String email;

    @OneToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "PARTNER_ID")
    private Partner partner;
}
