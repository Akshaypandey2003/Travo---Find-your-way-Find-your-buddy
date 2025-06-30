package com.room.ServiceImpl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.room.Entity.Room;
import com.room.Exceptions.ResourceNotFoundException;
import com.room.Repository.RoomRespository;
import com.room.Service.RoomService;

@Service
public class RoomServiceImpl implements RoomService{

    @Autowired
    RoomRespository roomRepo;

    @Override
    public Room addRoom(Room room) {
       return roomRepo.save(room);
    }

    @Transactional
    @Override
    public Room updateRoom(String roomId,Room room) 
    {
      Room existingRom = roomRepo.findById(roomId).orElseThrow(()->new ResourceNotFoundException("Room Not Found"));

      BeanUtils.copyProperties(room,existingRom);
      return roomRepo.save(existingRom);
    }

    @Transactional
    @Override
    public Room updateRoomAvailability(String roomId, int rooms) {
        Room room = roomRepo.findById(roomId).orElseThrow(()->new ResourceNotFoundException("Room Not Found"));
  
       int currAvailableRooms = room.getAvailableRooms();
       int updatedAvailableRooms = currAvailableRooms+rooms;

       if(updatedAvailableRooms>room.getTotalRooms())
       updatedAvailableRooms = room.getTotalRooms();

       if(updatedAvailableRooms<0)
         updatedAvailableRooms = 0;

       room.setAvailableRooms(updatedAvailableRooms);
       return roomRepo.save(room);
    }

    @Override
    public List<Room> getRoomByHotelId(String hotelId) {
      List<Room> rooms = roomRepo.findByHotelId(hotelId);
      if(rooms.isEmpty())
      {
        throw new ResourceNotFoundException("No Room Found for this Hotel");
      }
      return rooms;
    }

    @Override
    public List<Room> getRoomByRoomCapacity(String hotelId,int roomCapacity) 
    {
      List<Room> rooms = roomRepo.findByHotelIdAndRoomCapacity(hotelId, roomCapacity);
      if(rooms.isEmpty())
      {
        throw new ResourceNotFoundException("No Room Found for this Hotel");
      }
      return rooms;
    }
    
    @Override
    public Room removeRoom(String roomId) {
        Room room = roomRepo.findById(roomId).orElseThrow(()->new ResourceNotFoundException("Room Not Found"));
        roomRepo.deleteById(roomId);
        return room;
    }
}
