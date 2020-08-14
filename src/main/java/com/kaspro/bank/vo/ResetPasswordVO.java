package com.kaspro.bank.vo;

import lombok.Data;

@Data
public class ResetPasswordVO {
  private String token;
  private String newPassword;
}
