package com.notification.handler;


import org.springframework.stereotype.Component;

import com.events.Notification.NotificationEvent;
import com.notification.Clients.UserServiceClient;
import com.notification.Controller.NotificationSocketController;
import com.notification.DTO.NotificationMapper;
import com.notification.DTO.UserSummary;
import com.notification.Entity.Notification;
import com.notification.Service.NotificationService;

@Component
public class InAppNotificationHandler implements NotificationHandler {

    private final NotificationService notificationService;
    private final UserServiceClient userServiceClient;
    private final NotificationSocketController socketController;

    public InAppNotificationHandler(
            NotificationService notificationService,
            UserServiceClient userServiceClient,
            NotificationSocketController socketController) {

        this.notificationService = notificationService;
        this.userServiceClient = userServiceClient;
        this.socketController = socketController;
    }

    @Override
    public boolean supports(NotificationEvent event) {

        return "FRIEND_REQUEST_SENT".equals(event.getType())
                || "FRIEND_REQUEST_ACCEPTED".equals(event.getType())
                || "TRIP_INVITE".equals(event.getType())
                || "TRIP_CREATED".equals(event.getType())
                || "TRIP_REQUEST_SENT".equals(event.getType())
                || "TRIP_REQUEST_ACCEPTED".equals(event.getType())
                || "TRIP_START_REMINDER".equals(event.getType())
                || "TRIP_END_REMINDER".equals(event.getType())
                || "COMPANION_REVIEW_SUBMITTED".equals(event.getType());
    }

    @Override
    public void handle(NotificationEvent event) {

        UserSummary sender =
                userServiceClient.getUserSummaryById(event.getSenderId());

        Notification notification =
                NotificationMapper.toEntity(
                        event,
                        sender,
                        event.getReceiverId()
                );

        notificationService.saveNotification(notification);

        socketController.sendNotification(
                event.getReceiverId(),
                notification
        );
    }
}
