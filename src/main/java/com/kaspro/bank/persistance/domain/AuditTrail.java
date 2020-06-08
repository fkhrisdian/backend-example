package com.kaspro.bank.persistance.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name = "KASPROBANKAPP_AUDIT_TRAIL")
@TypeDef(name = "json", typeClass = JsonBinaryType.class)
@DynamicUpdate
@Data
public class AuditTrail {
    @Id
    @Column(name = "PARTITION_KEY",nullable = false)
    private Date partitionKey;

    @Column(name = "TRX_ID",nullable = false)
    private int trxId;

    @Column(name = "SERVICE_NAME",nullable = false)
    private String serviceName;

    @Type(type = "json")
    @Column(name = "VALUE_BEFORE",nullable = false, columnDefinition = "json")
    private String valueBefore;

    @Type(type = "json")
    @Column(name = "VALUE_AFTER",nullable = false, columnDefinition = "json")
    private String valueAfter;

    @Column(name = "USER_APP",nullable = false)
    private String userApp;

    @Column(name = "START_DTM",nullable = false)
    private Date startDtm;

    @Column(name = "END_DTM",nullable = false)
    private Date endDtm;
}
