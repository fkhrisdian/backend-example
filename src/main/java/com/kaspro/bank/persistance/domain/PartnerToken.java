package com.kaspro.bank.persistance.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "PARTNER_TOKEN")
@DynamicUpdate
@Data
public class PartnerToken{
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "pt_generator")
    @SequenceGenerator(name="pt_generator", sequenceName = "pt_seq",allocationSize = 1)
    @Column(name = "ID")
    @Id
    Long id;

    @Column(name = "PARNTER_CODE")
    String partnerCode;

}
