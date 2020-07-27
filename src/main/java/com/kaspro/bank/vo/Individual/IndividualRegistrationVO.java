package com.kaspro.bank.vo.Individual;

import com.kaspro.bank.persistance.domain.Individual;
import com.kaspro.bank.persistance.domain.TransferLimit;
import com.kaspro.bank.persistance.domain.VirtualAccount;
import lombok.Data;

import java.util.List;

@Data
public class IndividualRegistrationVO {
    private Individual individual;
    private VirtualAccount virtualAccount;
    private List<TransferLimit> transferLimits;
}
