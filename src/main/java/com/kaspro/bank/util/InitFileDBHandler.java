package com.kaspro.bank.util;

import com.kaspro.bank.controller.FileConfigController;

public class InitFileDBHandler {


    public static Object paramName(String inputName) {
        InitFileDB initDB = InitFileDB.getInstance();
        Object result = initDB.get(inputName);
        if(result.equals("")){
            FileConfigController fileConfigController = new FileConfigController();
            fileConfigController.reLoad();
        }

        return result;
    }

}
