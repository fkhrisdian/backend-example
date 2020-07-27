package com.kaspro.bank.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserResVO {
    private String id;
    private String username;
    private String email;
    private RoleResVO role;
}
