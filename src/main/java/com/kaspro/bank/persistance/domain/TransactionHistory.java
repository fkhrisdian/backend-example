package com.kaspro.bank.persistance.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "TRANSACTION_HISTORY")
@DynamicUpdate
@Data
public class TransactionHistory extends Base{
    @Column(name = "TID")
    private String tid;

    @Column(name = "DEBIT_ACC")
    private String debitAcc;

    @Column(name = "CREDIT_ACC")
    private String creditAcc;

    @Column(name = "SKU")
    private String sku;

    @Column(name = "AMOUNT")
    private String amount;

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

}
