package com.kaspro.bank.services;

import com.kaspro.bank.converter.BlacklistMsisdnConverter;
import com.kaspro.bank.enums.StatusCode;
import com.kaspro.bank.exception.NostraException;
import com.kaspro.bank.persistance.domain.BlacklistMsisdn;
import com.kaspro.bank.persistance.domain.Individual;
import com.kaspro.bank.persistance.domain.User;
import com.kaspro.bank.persistance.repository.BlacklistMsisdnRepository;
import com.kaspro.bank.persistance.repository.IndividualRepository;
import com.kaspro.bank.vo.BlacklistMsisdn.BlacklistMsisdnVO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class BlacklistMsisdnService {

    @Autowired
    BlacklistMsisdnRepository bmRepo;

    @Autowired
    BlacklistMsisdnConverter converter;

    @Autowired
    IndividualRepository iRepo;

    Logger logger = LoggerFactory.getLogger(BlacklistMsisdn.class);

    private List<BlacklistMsisdnVO> parseCSVFile(final MultipartFile file) throws Exception {
        final List<BlacklistMsisdnVO> bms = new ArrayList<>();
        try {
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    final String[] data = line.split(",");
                    final BlacklistMsisdnVO bm = new BlacklistMsisdnVO();
                    bm.setMsisdn(data[0]);
                    bm.setReason(data[1]);
                    bm.setName(data[2]);
                    bm.setEmail(data[3]);
                    bm.setVa(data[4]);
                    bms.add(bm);
                }
                return bms;
            }
        } catch (final IOException e) {
            logger.error("Failed to parse CSV file {}", e);
            throw new Exception("Failed to parse CSV file {}", e);
        }
    }

    @Async
    public CompletableFuture<List<BlacklistMsisdnVO>> saveBms(MultipartFile file) throws Exception {
        List<BlacklistMsisdnVO> bms = parseCSVFile(file);
        for(BlacklistMsisdnVO bm:bms){
            this.add(bm);
        }
        return CompletableFuture.completedFuture(bms);
    }

    public BlacklistMsisdn delete(String msisdn){
        BlacklistMsisdn bm=bmRepo.findByMSISDN(msisdn);
        if(bm==null){
            throw new NostraException("No Blacklisted MSISDN found");
        }else{
            Individual individual=iRepo.findByMsisdn2(msisdn);
            if(individual==null){
                bmRepo.delete(bm);
                return bm;
            }else{
                bmRepo.delete(bm);
                individual.setStatus("ACTIVE");
                iRepo.save(individual);
                return bm;
            }
        }
    }

    public BlacklistMsisdn add(BlacklistMsisdnVO vo){
        BlacklistMsisdn bm=bmRepo.findByMSISDN(vo.getMsisdn());
        Individual individual=iRepo.findByMsisdn2(vo.getMsisdn());
        if(individual==null){
            if(bm==null){
                BlacklistMsisdn savedBm=bmRepo.save(converter.voToDomain(vo,"1"));
                return savedBm;
            }else {
                return bm;
            }
        }else {
            if(bm==null){
                vo.setEmail(individual.getEmail());
                vo.setName(individual.getName());
                vo.setVa(individual.getVa());
                BlacklistMsisdn savedBm=bmRepo.save(converter.voToDomain(vo,"0"));
                individual.setStatus("BLACKLISTED");
                iRepo.save(individual);
                return savedBm;
            }else{
                bm.setEmail(individual.getEmail());
                bm.setName(individual.getName());
                bm.setVa(individual.getVa());
                bm.setStatus("0");
                BlacklistMsisdn savedBm=bmRepo.save(bm);
                individual.setStatus("BLACKLISTED");
                iRepo.save(individual);
                return savedBm;
            }
        }

    }

    public List<BlacklistMsisdn> findAllBms(){
        logger.info("get list of user by "+Thread.currentThread().getName());
        List<BlacklistMsisdn> bms=bmRepo.findAll();
        return bms;
    }

    public BlacklistMsisdn findById(String id){
        BlacklistMsisdn bm = bmRepo.findByBMId(id);
        return bm;
    }
}
