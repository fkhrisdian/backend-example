package com.kaspro.bank.persistance.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "PARTNER_MEMBER_TOKEN")
@DynamicUpdate
@Data
public class PartnerMemberToken {
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "pmt_generator")
    @SequenceGenerator(name="pmt_generator", sequenceName = "pmt_seq",allocationSize = 1)
    @Column(name = "ID")
    @Id
    Long id;

    @Column(name = "PARTNER_CODE")
    String partnerCode;

    @Column(name = "PARTNER_MEMBER_CODE")
    Long partnerMemberCode;
}
