package com.kaspro.bank.util;

import com.kaspro.bank.persistance.domain.KasprobankConfig;
import com.kaspro.bank.services.InitConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Renny on 02/05/18.
 */

@Slf4j
@Component
public class InitDB {
    private static final Map<String, String> params = new HashMap<String, String>();
    private static final Map<String, String> defaults = new HashMap<String, String>();
    private static final InitDB instance = new InitDB();
    private static final int PERIOD = 20000;
    private static final String EMPTY = "";
    private static long lastupdate = 0;

    @Autowired
    private InitConfigService initConfigService;

    public static InitDB getInstance() {
        instance.loadData();
        return instance;
    }

    private void loadData() {
        if ((0 - lastupdate) > PERIOD) {
            init();
        }
    }


    @PostConstruct
    public void init(){
        List<KasprobankConfig> xx = initConfigService.findAll();
        for (KasprobankConfig i : xx) {
            params.put(i.getParam_name(), i.getParam_value());
            log.info("param_name" + i.getParam_name().toString());
        }
        lastupdate = 0;

    }

    public String get(String paramName) {
        String result = params.get(paramName);
        if ((result == null) || (result.equals(EMPTY))) {
            result = defaults.get(paramName);
        }
        return result;
    }

    public String put(String paramName, String paramValue) {
        params.put(paramName, paramValue);
        return paramValue;
    }


}
