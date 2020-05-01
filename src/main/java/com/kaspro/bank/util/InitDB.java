package com.kaspro.bank.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by Renny on 02/05/18.
 */

@Slf4j
@Component
public class InitDB {

    @PostConstruct
    public void init(){
        log.info("If need to add initial value");
    }
}
