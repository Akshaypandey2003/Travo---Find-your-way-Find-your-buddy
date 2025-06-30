package com.user.DTO;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationMessage {

    private String notificationId;
    private String notificationFrom;
    private String notificationTo;
    private String message;
    private String senderName;
    private String tripId;
    private String senderProfilePic;;
    private boolean isRead = false;
    private String type;
}
