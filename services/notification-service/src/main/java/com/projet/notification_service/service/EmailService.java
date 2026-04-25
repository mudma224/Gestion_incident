package com.projet.notification_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${notification.mail.from:noreply@incidents-app.com}")
    private String fromAddress;

    @Value("${notification.mail.enabled:false}")
    private boolean mailEnabled;

    public void sendHtmlEmail(String recipientEmail, String subject,
                               String notificationType, String messageBody)
            throws MessagingException {

        if (!mailEnabled || mailSender == null) {
            log.info("[NOTIF] Email désactivé — sujet: {}", subject);
            return;
        }

        Context ctx = new Context();
        ctx.setVariable("subject", subject);
        ctx.setVariable("message", messageBody);
        ctx.setVariable("notificationType", notificationType);

        String htmlContent = templateEngine.process("email/notification", ctx);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(fromAddress);
        helper.setTo(recipientEmail);
        helper.setSubject("[Incidents] " + subject);
        helper.setText(htmlContent, true);

        mailSender.send(mimeMessage);
        log.info("[NOTIF] Email envoyé — sujet: {}", subject);
    }
}