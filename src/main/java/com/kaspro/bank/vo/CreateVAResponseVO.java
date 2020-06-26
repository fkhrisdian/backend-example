package com.kaspro.bank.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateVAResponseVO {
    private String client_id;
    private String virtual_account;
    private String data;
    private String status;
    private String message;
}
