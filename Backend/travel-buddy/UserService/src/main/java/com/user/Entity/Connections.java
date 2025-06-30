package com.user.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Document
public class Connections {
    
    @Id
    private String connectionId;
    private String requestFrom;
    private String requestTo;
    private boolean status;
}
