package com.room.Service;

import java.util.List;

import com.room.Entity.Room;

public interface RoomService 
{
    public Room addRoom(Room room);
    public Room updateRoom(String roomId,Room room);
    public Room updateRoomAvailability(String roomId, int rooms);
    public List<Room> getRoomByHotelId(String hotelId);
    public List<Room> getRoomByRoomCapacity(String hotelId,int roomCapacity);
    public Room removeRoom(String roomId);
}
