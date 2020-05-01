package com.kaspro.bank.validator;

import com.kaspro.bank.config.Messages;
import com.kaspro.bank.enums.StatusCode;
import com.kaspro.bank.exception.NostraException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationValidator {

    @Autowired
    Messages messages;

    public void validate(String token){
        if (false) {
            throw new NostraException(messages.get("error.invalid.login"), StatusCode.UNAUTHORIZED);
        }
    }
}
