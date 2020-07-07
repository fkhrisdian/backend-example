package com.kaspro.bank.controller;

import com.kaspro.bank.persistance.domain.KasprobankConfig;
import com.kaspro.bank.services.InitConfigService;
import com.kaspro.bank.util.InitDB;
import com.kaspro.bank.util.InitDBHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class InitConfigController {

    @Autowired
    private InitConfigService initConfigService;

    @PostMapping(value = "/KasprobankConfig", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public KasprobankConfig add(@RequestBody KasprobankConfig input){


        return initConfigService.add(input);
    }
    @GetMapping(value = "/KasprobankConfig", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<KasprobankConfig> findAll(){

        return initConfigService.findAll();
    }

    @GetMapping(value = "/KasprobankConfigReload", produces = MediaType.APPLICATION_JSON_VALUE)
    public String reLoad(){
        List<KasprobankConfig> listX = initConfigService.findAll();
        InitDB x  = InitDB.getInstance();
        for (KasprobankConfig i : listX) {
            x.put(i.getParam_name(), i.getParam_value());
        }
        String result = "Reload Config ok";
        return result;
    }

    @GetMapping(value = "/KasprobankConfigGet",produces = MediaType.APPLICATION_JSON_VALUE)
    public String get(@RequestParam String Name){
        String result = InitDBHandler.paramName(Name);
        log.info("Loading Param :" +result);
        return result;
    }

    @PostMapping(value = "/KasprobankConfig/Update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public KasprobankConfig update(@RequestBody KasprobankConfig input){
        return initConfigService.add(input);
    }
}
