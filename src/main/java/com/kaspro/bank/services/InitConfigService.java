package com.kaspro.bank.services;

import com.kaspro.bank.persistance.domain.KasprobankConfig;
import com.kaspro.bank.persistance.repository.KasproBankConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InitConfigService {

    @Autowired
    private KasproBankConfigRepository kasproBankConfigRepository;

    Object target;
    Logger logger = LoggerFactory.getLogger(InitConfigService.class);

    public List<KasprobankConfig> findAll(){
        List<KasprobankConfig> result = kasproBankConfigRepository.findAll();
        logger.info("get initial param "+Thread.currentThread().getName());
        return result;
    }



    public KasprobankConfig add(KasprobankConfig kasprobankConfig){
        KasprobankConfig saved = kasproBankConfigRepository.save(kasprobankConfig);
        return saved;
    }
}
