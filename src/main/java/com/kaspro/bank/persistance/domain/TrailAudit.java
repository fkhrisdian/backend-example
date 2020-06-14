package com.kaspro.bank.persistance.domain;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name = "AUDIT_TRAIL")
@DynamicUpdate
@Data
public class TrailAudit extends Base{

    @Column(name = "FIELD")
    private String field;

    @Column(name = "VALUE_BEFORE",columnDefinition="LONGTEXT")
    private String valueBefore;

    @Column(name = "VALUE_AFTER",columnDefinition="LONGTEXT")
    private String valueAfter;

    @Column(name = "USER")
    private String user;

    @Column(name = "START_DTM")
    private Date startDtm;

    @Column(name = "END_DTM")
    private Date endDtm;

    @Column(name = "OWNER_ID")
    private String ownerID;
}
