package com.kaspro.bank.persistance.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name = "INDIVIDUAL")
@DynamicUpdate
@Data
public class Individual extends Base{
    @Column(name = "MSISDN")
    private String msisdn;

    @Column(name = "NAME")
    private String name;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PHOTO")
    private String photo;

    @Column(name = "BIRTH_PLACE")
    private String birth_place;

    @Column(name = "BIRTH_DATE")
    private String birth_date;

    @Column(name = "ID_TYPE")
    private String id_type;

    @Column(name = "ID_NO")
    private String id_no;

    @Column(name = "ID_PHOTO")
    private String id_photo;

    @Column(name = "COUNTRY_CODE")
    private String country_code;

    @Column(name = "GENDER")
    private String gender;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "CITY")
    private String city;

    @Column(name = "PROVINCE")
    private String province;

    @Column(name = "ZIP_CODE")
    private String zip_code;

    @Column(name = "ADDITIONAL_INFO")
    private String additional_info;

    @Column(name = "TIER")
    private String tier;

    @Column(name = "STATUS")
    private String status;

}
