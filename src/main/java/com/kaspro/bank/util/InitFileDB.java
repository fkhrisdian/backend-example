package com.kaspro.bank.util;

import com.kaspro.bank.persistance.domain.FileConfig;
import com.kaspro.bank.services.FileConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Blob;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Renny on 02/05/18.
 */

@Slf4j
@Component
public class InitFileDB {
    private static final Map<String, Blob> params = new HashMap<String, Blob>();
    private static final Map<String, Blob> defaults = new HashMap<String, Blob>();
    private static final InitFileDB instance = new InitFileDB();
    private static final int PERIOD = 20000;
    private static final String EMPTY = "";
    private static long lastupdate = 0;

    @Autowired
    private FileConfigService service;

    public static InitFileDB getInstance() {
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
        List<FileConfig> xx = service.findAll();
        for (FileConfig i : xx) {
            params.put(i.getParam_name(), i.getParam_value());
            log.info("param_name" + i.getParam_name());
        }
        lastupdate = 0;

    }

    public Blob get(String paramName) {
        Blob result = params.get(paramName);
        if ((result == null) || (result.equals(EMPTY))) {
            result = defaults.get(paramName);
        }
        return result;
    }

    public Object put(String paramName, Blob paramValue) {
        params.put(paramName, paramValue);
        return paramValue;
    }


}
