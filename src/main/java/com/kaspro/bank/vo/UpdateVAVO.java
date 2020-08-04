package com.kaspro.bank.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateVAVO {
    private String client_id;
    private String trx_amount;
    private String customer_name;
    private String customer_email;
    private String customer_phone;
    private String trx_id;
    private String datetime_expired;
    private String description;
    private String type;
}
