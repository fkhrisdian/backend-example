package com.kaspro.bank.services;

import com.kaspro.bank.persistance.domain.Individual;
import com.kaspro.bank.persistance.repository.IndividualRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IndividualService {

    @Autowired
    IndividualRepository iRepo;

    public Individual registerIndividual(Individual vo){
        Individual savedIndividual=iRepo.save(vo);

        return savedIndividual;
    }
}
