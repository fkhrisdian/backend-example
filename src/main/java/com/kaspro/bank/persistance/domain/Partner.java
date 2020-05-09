package com.kaspro.bank.persistance.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "PARTNER")
@DynamicUpdate
@Data
public class Partner extends Base {

    @Column(nullable = false)
    private String name;

    @Column
    private String email;

    @Column(name = "ALAMAT")
    private String address;

    @Column(name = "NIB_SIP_TDP")
    private String nibSipTdp;

    @Column(name = "NO_AKTA_PENDIRIAN")
    private String noAktaPendirian;

    @Column
    private String npwp;

    @Column
    private String status;
}
