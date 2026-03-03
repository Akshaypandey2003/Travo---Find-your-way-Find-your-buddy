package com.notification.handler;

import com.events.Notification.NotificationEvent;

public interface NotificationHandler {

    boolean supports(NotificationEvent event);

    void handle(NotificationEvent event);
}