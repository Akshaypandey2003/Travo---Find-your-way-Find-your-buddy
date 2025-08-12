package com.user.DTO;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Author {
    
    private String userId;
    private String name;
    private String profilePic;
}
