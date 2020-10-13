package com.be.sample.validator;

import com.be.sample.config.Messages;
import com.be.sample.enums.StatusCode;
import com.be.sample.exception.SampleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationValidator {

    @Autowired
    Messages messages;

    public void validate(String token){
        if (false) {
            throw new SampleException(messages.get("error.invalid.login"), StatusCode.UNAUTHORIZED);
        }
    }
}
