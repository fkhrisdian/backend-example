package com.kaspro.bank.vo;

import lombok.Data;

@Data
public class BNINotifPlainVO {
    private String trx_id;
    private String virtual_account;
    private String customer_name;
    private String trx_amount;
    private String payment_amount;
    private String cumulative_payment_amount;
    private String payment_ntb;
    private String datetime_payment;
    private String datetime_payment_iso8601;
    private String client_id;
}
