package com.notification.handler;

import com.events.Entity.NotificationEvent;

public interface NotificationHandler {

    boolean supports(NotificationEvent event);

    void handle(NotificationEvent event);
}