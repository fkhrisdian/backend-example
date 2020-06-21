package com.kaspro.bank.persistance.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "PARTNER")
@DynamicUpdate
@Data
public class Partner extends Base {

    @Column(name = "NAMA",nullable = false)
    private String name;

    @Column(name = "ALIAS",nullable = false)
    private String alias;

    @Column(name = "ALAMAT")
    private String address;

    @Column(name = "NIB_SIP_TDP")
    private String nibSipTdp;

    @Column(name = "NO_AKTA_PENDIRIAN")
    private String noAktaPendirian;

    @Column(name = "NPWP")
    private String npwp;

    @Column(name = "TIERS")
    private String tiers;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "PARTNER_CODE")
    private String partnerCode;
}
