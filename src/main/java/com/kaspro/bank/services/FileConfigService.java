package com.kaspro.bank.services;

import com.kaspro.bank.enums.StatusCode;
import com.kaspro.bank.exception.NostraException;
import com.kaspro.bank.persistance.domain.FileConfig;
import com.kaspro.bank.persistance.repository.FileConfigRepository;
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
public class FileConfigService {

    @Autowired
    private FileConfigRepository fileConfigRepository;

    Object target;
    Logger logger = LoggerFactory.getLogger(FileConfigService.class);

    public List<FileConfig> findAll(){
        List<FileConfig> result = fileConfigRepository.findAll();
        logger.info("get initial param "+Thread.currentThread().getName());
        return result;
    }



    public FileConfig add(FileConfig kasprobankConfig){
        FileConfig saved = fileConfigRepository.save(kasprobankConfig);
        return saved;
    }

    public FileConfig getByName (String name){
        FileConfig kasprobankConfig=fileConfigRepository.selectByParamName(name);
        return kasprobankConfig;
    }

    public FileConfig detail(int id) {
        return fileConfigRepository.findById(id).get();
    }

    public FileConfig update(FileConfig kasprobankConfig){
        Optional<FileConfig> saved = fileConfigRepository.findById(kasprobankConfig.getId());
        FileConfig config = saved.get();
        config.setParam_value(kasprobankConfig.getParam_value());
        config.setParam_name(config.getParam_name());
        fileConfigRepository.save(config);
        return config;
    }

    public List<String> findNameByPrefix(String prefix){
        List<String> names = fileConfigRepository.selectNameByPrefix(prefix);
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

    public List<FileConfig> findConfigByPrefix(String prefix){
        List<FileConfig> configs = fileConfigRepository.selectByPrefix(prefix);
        if(configs==null){
            throw new NostraException("Prefix not found", StatusCode.DATA_NOT_FOUND);
        }else{
            return configs;
        }
    }
}
