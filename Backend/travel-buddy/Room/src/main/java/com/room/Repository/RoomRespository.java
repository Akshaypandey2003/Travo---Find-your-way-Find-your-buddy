package com.room.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.room.Entity.Room;

public interface RoomRespository extends MongoRepository<Room,String> {
    
    public List<Room> findByHotelId(String hotelId);
    List<Room> findByHotelIdAndRoomCapacity(String hotelId, int roomCapacity);

}   
