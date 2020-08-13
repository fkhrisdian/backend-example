package com.kaspro.bank.services;

import com.jcraft.jsch.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;


@Service
public class EmailUtil {
    @Autowired
    private JavaMailSender javaMailSender;

    void sendEmail(String to, String subject, String text) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setFrom("kasprobank@kaspro.id");
        msg.setSubject(subject);
        msg.setText(text);

        javaMailSender.send(msg);

    }
    
}
