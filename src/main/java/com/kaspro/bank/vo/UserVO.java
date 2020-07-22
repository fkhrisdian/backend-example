package com.kaspro.bank.vo;

import lombok.Data;

@Data
public class UserVO {
  private String username;
  private String email;
  private String roles;
  private String token;
}
