package com.kaspro.bank.persistance.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "KASPROBANKAPP_AUDIT_TRAIL")
@TypeDef(name = "json", typeClass = JsonBinaryType.class)
@DynamicUpdate
@Data
public class AuditTrail extends Base{

    @Column(name = "VALUE_BEFORE",columnDefinition="LONGTEXT")
    private String valueBefore;

    @Column(name = "VALUE_AFTER",columnDefinition="LONGTEXT")
    private String valueAfter;

    @Column(name = "USER_APP",nullable = false)
    private String userApp;

    @Column(name = "START_DTM",nullable = false)
    private Date startDtm;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "PARTNER_ID")
    private Partner partner;
}
