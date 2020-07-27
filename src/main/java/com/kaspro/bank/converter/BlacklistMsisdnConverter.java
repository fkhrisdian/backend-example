package com.kaspro.bank.converter;

import com.kaspro.bank.persistance.domain.BlacklistMsisdn;
import com.kaspro.bank.vo.BlacklistMsisdn.BlacklistMsisdnVO;
import org.springframework.stereotype.Component;

@Component
public class BlacklistMsisdnConverter {

    public BlacklistMsisdn voToDomain(BlacklistMsisdnVO vo){
        BlacklistMsisdn result=new BlacklistMsisdn();
        result.setVa(vo.getVa());
        result.setEmail(vo.getEmail());
        result.setName(vo.getName());
        result.setReason(vo.getReason());
        result.setMsisdn(vo.getMsisdn());
        return result;
    }
}
