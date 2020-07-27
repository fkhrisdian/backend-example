package com.kaspro.bank.services;

import com.kaspro.bank.converter.BlacklistMsisdnConverter;
import com.kaspro.bank.persistance.domain.BlacklistMsisdn;
import com.kaspro.bank.persistance.domain.User;
import com.kaspro.bank.persistance.repository.BlacklistMsisdnRepository;
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

    Logger logger = LoggerFactory.getLogger(BlacklistMsisdn.class);

    private List<BlacklistMsisdn> parseCSVFile(final MultipartFile file) throws Exception {
        final List<BlacklistMsisdn> bms = new ArrayList<>();
        try {
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    final String[] data = line.split(",");
                    final BlacklistMsisdn bm = new BlacklistMsisdn();
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
    public CompletableFuture<List<BlacklistMsisdn>> saveBms(MultipartFile file) throws Exception {
        long start = System.currentTimeMillis();
        List<BlacklistMsisdn> bms = parseCSVFile(file);
        logger.info("saving list of users of size {}", bms.size(), "" + Thread.currentThread().getName());
        bms = bmRepo.saveAll(bms);
        long end = System.currentTimeMillis();
        logger.info("Total time {}", (end - start),"ms");
        return CompletableFuture.completedFuture(bms);
    }

    public BlacklistMsisdn add(BlacklistMsisdnVO vo){
        BlacklistMsisdn savedBm=bmRepo.save(converter.voToDomain(vo));
        return savedBm;
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
