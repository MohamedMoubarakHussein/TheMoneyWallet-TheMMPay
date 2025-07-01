package com.themoneywallet.notificationservice.service.notificationservices;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    
    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    public void sendTemplateEmail(String to, String subject, Context context, String template) throws MessagingException  {

        String htmlContent = templateEngine.process(template, context);

        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);  

        log.info("step point number four ");
        mailSender.send(message);
    }
}