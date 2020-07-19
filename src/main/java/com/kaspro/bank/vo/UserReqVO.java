package com.kaspro.bank.vo;

import lombok.Data;

@Data
public class UserReqVO {
    private String username;
    private String email;
    private String[] roles;
}
