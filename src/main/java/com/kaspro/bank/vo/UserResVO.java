package com.kaspro.bank.vo;

import lombok.Data;

@Data
public class UserResVO {
    private String id;
    private String username;
    private String email;
    private String[] roles;
}
