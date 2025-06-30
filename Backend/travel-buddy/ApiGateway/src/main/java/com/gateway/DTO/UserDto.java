package com.gateway.DTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)  // Ignore missing fields in JSON
public class UserDto {
    
    String name;
    String email;
    String password;
    String gender;
}
