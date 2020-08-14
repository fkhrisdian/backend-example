package com.kaspro.bank.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.kaspro.bank.persistance.domain.BlacklistMsisdn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service
public class EmailUtil {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private Configuration config;

    Logger logger = LoggerFactory.getLogger(BlacklistMsisdn.class);

    @Async
    public CompletableFuture<String> sendEmail2 (String to, String subject, String template, Map<String, Object> model) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            // set mediaType
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            // add attachment

            Template t = config.getTemplate(template);
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

            helper.setTo(to);
            helper.setText(html, true);
            helper.setSubject(subject);
            helper.setFrom("kasprobank@kaspro.id");
            javaMailSender.send(message);

        } catch (IOException | TemplateException | MessagingException e) {
            logger.info("Error while sending Email with error : "+e.getMessage());
            return CompletableFuture.completedFuture(e.getMessage());
        }
        logger.info("Success sending email to : "+to);
        return CompletableFuture.completedFuture("Success");
    }

    void sendEmail(String to, String subject, String text) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setFrom("kasprobank@kaspro.id");
        msg.setSubject(subject);
        msg.setText(text);

        javaMailSender.send(msg);

    }
    
}
