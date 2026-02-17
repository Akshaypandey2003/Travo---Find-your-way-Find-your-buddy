package com.notification.handler;


import org.springframework.stereotype.Component;

import com.events.Entity.NotificationEvent;
import com.notification.Service.EmailService;

@Component
public class EmailNotificationHandler implements NotificationHandler {

    private final EmailService emailService;

    public EmailNotificationHandler(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public boolean supports(NotificationEvent event) {

        return "PASSWORD_RESET".equals(event.getType())
                || "WELCOME".equals(event.getType())||"PASSWORD_RESET_SUCCESS".equals(event.getType());
    }

    @Override
    public void handle(NotificationEvent event) {

        if ("PASSWORD_RESET".equals(event.getType())) {

            String email = event.getMetadata().get("email");
            String name = event.getMetadata().get("name");
            String resetLink = event.getMetadata().get("resetLink");

            emailService.sendPasswordResetEmail(email,name, resetLink);
        }

        if ("WELCOME".equals(event.getType())) {

            String email = event.getMetadata().get("email");
            String name = event.getMetadata().get("name");

            emailService.sendWelcomeEmail(email, name);
        }
        if ("PASSWORD_RESET_SUCCESS".equals(event.getType())) {

            String email = event.getMetadata().get("email");
            String name = event.getMetadata().get("name");

            emailService.sendWelcomeEmail(email,name);
        }
    }
}