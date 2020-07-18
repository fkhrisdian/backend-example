package com.kaspro.bank.vo;

import com.kaspro.bank.persistance.domain.Individual;
import com.kaspro.bank.persistance.domain.TransferLimit;
import com.kaspro.bank.persistance.domain.VirtualAccount;
import lombok.Data;

import java.util.List;

@Data
public class RoleReqVO {
    private String name;
    private String[] pages;
}
