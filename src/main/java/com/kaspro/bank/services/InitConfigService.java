package com.kaspro.bank.services;

import com.kaspro.bank.enums.StatusCode;
import com.kaspro.bank.exception.NostraException;
import com.kaspro.bank.persistance.domain.KasprobankConfig;
import com.kaspro.bank.persistance.repository.KasproBankConfigRepository;
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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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

    public KasprobankConfig detail(int id) {
        return kasproBankConfigRepository.findById(id).get();
    }

    public KasprobankConfig update(KasprobankConfig kasprobankConfig){
        Optional<KasprobankConfig> saved = kasproBankConfigRepository.findById(kasprobankConfig.getId());
        KasprobankConfig config = saved.get();
        config.setParam_value(kasprobankConfig.getParam_value());
        config.setParam_name(config.getParam_name());
        kasproBankConfigRepository.save(config);
        return config;
    }

    private List<KasprobankConfig> parseCSVFile(final MultipartFile file, String prefix) throws Exception {
        final List<KasprobankConfig> bms = new ArrayList<>();
        logger.info("Parsing CSV File");
        try {
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    final String[] data = line.split(",");
                    final KasprobankConfig bm = new KasprobankConfig();
                    bm.setParam_name(prefix+data[0]);
                    logger.info(bm.getParam_name().toString());
                    bm.setParam_value(data[1]);
                    logger.info(bm.getParam_value().toString());
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
    public CompletableFuture<List<KasprobankConfig>> saveBms(MultipartFile file, String prefix) throws Exception {
        logger.info("Start Uploading");
        List<KasprobankConfig> bms = parseCSVFile(file, prefix);
        for(KasprobankConfig bm:bms){
            this.add(bm);
        }
        return CompletableFuture.completedFuture(bms);
    }

    public List<String> findNameByPrefix(String prefix){
        List<String> names = kasproBankConfigRepository.selectNameByPrefix(prefix);
        List<String> result = new ArrayList<>();
        if(names==null){
            throw new NostraException("Prefix not found", StatusCode.DATA_NOT_FOUND);
        }else{
            for(String s:names){
                String name = s.replace(prefix,"");
                result.add(name);
            }
            return result;
        }
    }

    public List<KasprobankConfig> findConfigByPrefix(String prefix){
        List<KasprobankConfig> configs = kasproBankConfigRepository.selectByPrefix(prefix);
        if(configs==null){
            throw new NostraException("Prefix not found", StatusCode.DATA_NOT_FOUND);
        }else{
            return configs;
        }
    }
}
