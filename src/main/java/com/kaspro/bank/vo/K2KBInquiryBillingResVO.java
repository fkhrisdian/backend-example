package com.kaspro.bank.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class K2KBInquiryBillingResVO {
    private String client_id;
    private String trx_id;
    private String trx_amount;
    private String virtual_account;
    private String customer_name;
    private String customer_phone;
    private String customer_email;
    private String datetime_created;
    private String datetime_expired;
    private String datetime_last_updated;
    private String datetime_payment;
    private String payment_ntb;
    private String payment_amount;
    private String va_status;
    private String billing_type;
    private String description;
    private String datetime_created_iso8601;
    private String datetime_expired_iso8601;
    private String datetime_last_updated_iso8601;
    private String datetime_payment_iso8601;
}