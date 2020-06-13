package com.kaspro.bank.util;

import com.kaspro.bank.controller.InitConfigController;

public class InitDBHandler {


    public static String paramName(String inputName) {
        InitDB initDB = InitDB.getInstance();
        String result = initDB.get(inputName);
        if(result.equals("")){
            InitConfigController initConfigController = new InitConfigController();
            initConfigController.reLoad();
        }

        return result;
    }

}
