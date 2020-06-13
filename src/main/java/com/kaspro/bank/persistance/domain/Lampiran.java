package com.kaspro.bank.persistance.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "LAMPIRAN")
@DynamicUpdate
@Data
public class Lampiran extends Base {

    @Column(name = "NAME",nullable = false)
    private String name;

    @Column(name = "URL",nullable = false)
    private String url;

    @Column(name = "FLAG",nullable = false)
    private String flag;

    @Column(name = "OWNER_ID",nullable = false)
    private int ownerID;
}
