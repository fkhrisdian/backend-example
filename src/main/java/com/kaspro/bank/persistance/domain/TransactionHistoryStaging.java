package com.kaspro.bank.persistance.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "TRANSACTION_HISTORY_STG")
@DynamicUpdate
@Data
public class TransactionHistoryStaging extends Base{
    @Column(name = "TID")
    private String tid;

    @Column(name = "PARTNER_ID")
    private String partnerId;

    @Column(name = "PARTNER_NAME")
    private String partnerName;

    @Column(name = "SENDER_ID")
    private String senderId;

    @Column(name = "MSISDN")
    private String msisdn;

    @Column(name = "ACC_TYPE")
    private String accType;

    @Column(name = "DEBIT_ACC")
    private String debitAcc;

    @Column(name = "DEBIT_NAME")
    private String debitName;

    @Column(name = "CREDIT_ACC")
    private String creditAcc;

    @Column(name = "CREDIT_NAME")
    private String creditName;

    @Column(name = "SKU")
    private String sku;

    @Column(name = "AMOUNT")
    private String amount;

    @Column(name = "DEST_BANK_CODE")
    private String destinationBankCode;

    @Column(name = "PAYMENT_METHOD")
    private String paymentMethod;

    @Column(name = "CHARGING_MODEL")
    private String chargingModelId;

    @Column(name = "ADMIN_FEE")
    private String adminFee;

    @Column(name = "INTERBANK_FEE")
    private String interBankFee;

    @Column(name = "TOTAL_AMOUNT")
    private String totalAmount;

    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "BANK_REFERENCE")
    private String bankRef;

    @Column(name = "CUSTOMER_REFERENCE")
    private String custRef;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "REMARK")
    private String remark;

    @Column(name = "SENDER")
    private String sender;

    @Column(name = "DESTINATION")
    private String dest;

}
