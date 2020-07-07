package com.kaspro.bank.services;

import com.kaspro.bank.persistance.domain.KasprobankConfig;
import com.kaspro.bank.persistance.repository.KasproBankConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
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

    public KasprobankConfig update(KasprobankConfig kasprobankConfig){
        Optional<KasprobankConfig> saved = kasproBankConfigRepository.findById(kasprobankConfig.getId());
        KasprobankConfig config = saved.get();
        config.setParam_value(kasprobankConfig.getParam_value());
        config.setParam_name(kasprobankConfig.getParam_name());
        return kasproBankConfigRepository.save(config);
    }
}
