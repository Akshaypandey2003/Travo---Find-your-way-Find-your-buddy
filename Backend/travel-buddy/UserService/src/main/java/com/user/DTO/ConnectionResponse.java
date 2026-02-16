package com.user.DTO;



import com.user.Enum.ConnectionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConnectionResponse {

    private String connectionId;

    private String requestFrom;

    private String requestTo;

     private ConnectionStatus status;

}