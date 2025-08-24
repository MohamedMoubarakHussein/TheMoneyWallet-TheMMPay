package com.themoneywallet.notificationservice.service.notificationservices;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import com.themoneywallet.sharedUtilities.ports.EmailSenderPort;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService implements EmailSenderPort {

    
    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    public void sendTemplateEmailInternal(String to, String subject, Context context, String template) throws MessagingException  {

        String htmlContent = templateEngine.process(template, context);

        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);  

        log.info("step point number four ");
        mailSender.send(message);
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent){
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        }catch(Exception e){
            log.error("Failed to send email: {}", e.getMessage());
        }
    }
}