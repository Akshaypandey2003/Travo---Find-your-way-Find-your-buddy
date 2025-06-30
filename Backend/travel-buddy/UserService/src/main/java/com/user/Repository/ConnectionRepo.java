package com.user.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.user.Entity.Connections;
import java.util.List;


public interface ConnectionRepo extends MongoRepository<Connections,String> {
    
    List<Connections> findByRequestFromAndStatus(String requetFrom,boolean status);
    List<Connections> findByRequestToAndStatus(String requestTo, boolean status);

    Connections findByRequestFromAndRequestTo(String requestFrom, String requestTo);
}
