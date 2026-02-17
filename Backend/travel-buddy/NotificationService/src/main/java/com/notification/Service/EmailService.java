package com.notification.Service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.internet.MimeMessage;

@Service
@SuppressWarnings("null")
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendPasswordResetEmail(String toEmail, String name, String resetLink) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);

        message.setSubject("Reset Your Password - TravelBuddy");

        message.setText(
                "Hello " + name + ",\\n\\n" +
                        "Click the link below to reset your password:\n\n" +
                        resetLink +
                        "\n\nThis link will expire in 15 minutes.\n\n" +
                        "TravelBuddy Team");

        mailSender.send(message);
    }

    public void sendWelcomeEmail(String toEmail, String username) {

        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("exploreUrl",
                "http://localhost:3000");

        String htmlContent = templateEngine.process(
                "email/welcome-email",
                context);

        MimeMessage message = mailSender.createMimeMessage();

        try {

            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    true,
                    "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(
                    "Welcome to TravelBuddy 🌍");

            helper.setText(
                    htmlContent,
                    true // important -> HTML enabled
            );

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to send email", e);
        }
    }

    public void sendPasswordResetSuccess(String toEmail, String name) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);

        message.setSubject("Password recovery was successfull!!");

        message.setText(
                "Hello " + name + ",\n\n" +
                        "Your password was successfully reset. If this was not you, please contact support immediately.\n\n");

        mailSender.send(message);
    }
}