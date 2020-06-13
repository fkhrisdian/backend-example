package com.kaspro.bank.persistance.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "TRANSFER_INFO_MEMBER")
@DynamicUpdate
@Data
public class TransferInfoMember extends Base{
    @Column(name = "NAME",nullable = false)
    private String name;

    @Column(name = "VALUE",nullable = false)
    private String value;

    @Column(name = "FLAG",nullable = false)
    private String flag;

    @Column(name = "OWNER_ID",nullable = false)
    private int ownerID;
}
