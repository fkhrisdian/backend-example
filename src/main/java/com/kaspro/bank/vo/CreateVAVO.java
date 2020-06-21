package com.kaspro.bank.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class CreateVAVO {
    private String client_id;
    private String trx_amount;
    private String customer_name;
    private String customer_email;
    private String customer_phone;
    private String virtual_account;
    private String trx_id;
    private String datetime_expired;
    private String description;
    private String type;
    private String data;
    private String status;
    private String message;
}
