package com.kaspro.bank.services;

import com.kaspro.bank.persistance.domain.KasprobankConfig;
import com.kaspro.bank.persistance.repository.KasproBankConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class InitConfig {

    @Autowired
    private KasproBankConfigRepository kasproBankConfigRepository;

    Object target;
    Logger logger = LoggerFactory.getLogger(InitConfig.class);

    public CompletableFuture<List<KasprobankConfig>> findAll(){
        List<KasprobankConfig> result = new ArrayList<>();
        result = kasproBankConfigRepository.findAll();
        logger.info("get list of user by "+Thread.currentThread().getName());
        return CompletableFuture.completedFuture(result);
    }
}
