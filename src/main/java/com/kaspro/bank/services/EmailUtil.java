package com.kaspro.bank.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailUtil {
    @Autowired
    private JavaMailSender javaMailSender;

    void sendEmail(String to, String subject, String text) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);

        msg.setSubject(subject);
        msg.setText(text);

        javaMailSender.send(msg);

    }
}
