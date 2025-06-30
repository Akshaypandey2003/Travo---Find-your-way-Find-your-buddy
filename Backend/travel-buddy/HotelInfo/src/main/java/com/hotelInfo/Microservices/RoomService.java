package com.hotelInfo.Microservices;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotelInfo.Entity.Room;

@FeignClient(name = "ROOM-SERVICE",url="http://localhost:8082")
public interface RoomService 

{
    @PostMapping("/rooms/addRoom")
    public ResponseEntity<?> addRoom(@RequestBody Room room);

    @PutMapping("/rooms/updateRoom/{roomId}")
    public ResponseEntity<?> updateRoom(@PathVariable String roomId, @RequestBody Room room);

    @PutMapping("/rooms/updateRoomAvailability/{roomId}")
    public ResponseEntity<?> updateRoomAvailability(@PathVariable String roomId, @RequestParam int rooms);

    @GetMapping("/rooms/{hotelId}")
    public ResponseEntity<?> getRoomByHotelId(@PathVariable String hotelId);

    @GetMapping("/rooms/capacity/{hotelId}")
    public ResponseEntity<?> getRoomByRoomCapacity(@PathVariable String hotelId, @RequestParam int roomCapacity);

    @DeleteMapping("/rooms/removeRoom/{roomId}")
    public ResponseEntity<?> removeRoom(@PathVariable String roomId);
}
