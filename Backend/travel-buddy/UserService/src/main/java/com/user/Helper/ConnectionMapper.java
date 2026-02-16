package com.user.Helper;



import org.springframework.stereotype.Component;

import com.user.DTO.ConnectionResponse;
import com.user.Entity.Connections;

@Component
public class ConnectionMapper {

    public ConnectionResponse toResponse(Connections connection) {

        return ConnectionResponse.builder()
                .connectionId(connection.getConnectionId())
                .requestFrom(connection.getFollowerId())
                .requestTo(connection.getFollowingId())
                .status(connection.getStatus())
                .build();
    }
}