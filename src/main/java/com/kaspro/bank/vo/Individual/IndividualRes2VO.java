package com.kaspro.bank.vo.Individual;

import lombok.Data;

@Data
public class IndividualRes2VO {

    private String msisdn;

    private String name;

    private String email;

    private String photo;

    private String birth_place;

    private String birth_date;

    private String id_type;

    private String id_no;

    private String id_photo;

    private String country_code;

    private String gender;

    private String address;

    private String city;

    private String province;

    private String zip_code;

    private String additional_info;

    public boolean status;

    public boolean verification;

    public Long amount;

    public String account_no;

}
