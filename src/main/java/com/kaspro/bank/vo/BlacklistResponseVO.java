package com.kaspro.bank.vo;

import lombok.Data;

import java.util.Date;

@Data
public class BlacklistResponseVO {
  private Date creationDate;
  private String msisdn;
  private String reason;
  private String name;
  private String email;
  private String va;
}
