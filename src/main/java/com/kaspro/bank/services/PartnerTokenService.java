package com.kaspro.bank.services;

import com.kaspro.bank.enums.StatusCode;
import com.kaspro.bank.exception.NostraException;
import com.kaspro.bank.persistance.domain.PartnerToken;
import com.kaspro.bank.persistance.repository.PartnerTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PartnerTokenService {
    @Autowired
    PartnerTokenRepository ptRepo;

    public PartnerToken add(PartnerToken pt){
        PartnerToken existingPT=ptRepo.findPC(pt.getPartnerCode());
        if(existingPT!=null){
            throw new NostraException("Partner Code already exist", StatusCode.DATA_INTEGRITY);
        }
        PartnerToken savedPT=ptRepo.save(pt);
        return savedPT;
    }

    public PartnerToken delete(Long id){
        PartnerToken deletedPT=ptRepo.findPT(id);
        if(deletedPT==null){
            throw new NostraException("Partner Token doesn't exist", StatusCode.DATA_NOT_FOUND);
        }
        ptRepo.delete(deletedPT);
        return deletedPT;
    }

    public PartnerToken findById(Long id){
        PartnerToken pt=ptRepo.findPT(id);
        if(ptRepo==null){
            throw new NostraException("Partner Token doesn't exist", StatusCode.DATA_NOT_FOUND);
        }
        return pt;
    }
}
