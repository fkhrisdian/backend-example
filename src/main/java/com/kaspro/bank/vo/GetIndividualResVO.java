package com.kaspro.bank.vo;

import lombok.Data;

@Data
public class GetIndividualResVO {
    private int id;
    private String name;
    private String msisdn;
    private String email;
    private String idType;
}
