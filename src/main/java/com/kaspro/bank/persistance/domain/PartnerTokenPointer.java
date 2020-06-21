package com.kaspro.bank.persistance.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "PARTNER_TOKEN_POINTER")
@DynamicUpdate
@Data
public class PartnerTokenPointer {
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "ptp_generator")
    @SequenceGenerator(name="ptp_generator", sequenceName = "ptp_seq",allocationSize = 1)
    @Column(name = "ID")
    @Id
    Long id;
}
