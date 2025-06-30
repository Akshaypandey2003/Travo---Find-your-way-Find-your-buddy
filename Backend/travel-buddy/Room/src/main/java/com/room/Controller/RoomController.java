package com.room.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.room.Entity.Room;
import com.room.Service.RoomService;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @PostMapping("/addRoom")
    public ResponseEntity<?> addRoom(@RequestBody Room room) {
        try {
            Room addedRoom = roomService.addRoom(room);
            return new ResponseEntity<>(addedRoom, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to add room: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateRoom/{roomId}")
    public ResponseEntity<?> updateRoom(@PathVariable String roomId, @RequestBody Room room) {
        try {
            Room updatedRoom = roomService.updateRoom(roomId, room);
            if (updatedRoom == null) {
                return new ResponseEntity<>("Room not found with ID: " + roomId, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(updatedRoom, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update room: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateRoomAvailability/{roomId}")
    public ResponseEntity<?> updateRoomAvailability(@PathVariable String roomId, @RequestParam int rooms) {
        try {
            Room room = roomService.updateRoomAvailability(roomId, rooms);
            if (room == null) {
                return new ResponseEntity<>("Room not found with ID: " + roomId, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(room, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update room availability: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<?> getRoomByHotelId(@PathVariable String hotelId) {
        try {
            List<Room> rooms = roomService.getRoomByHotelId(hotelId);
            if (rooms.isEmpty()) {
                return new ResponseEntity<>("No rooms found for hotel ID: " + hotelId, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(rooms, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to fetch rooms: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/capacity/{hotelId}")
    public ResponseEntity<?> getRoomByRoomCapacity(@PathVariable String hotelId, @RequestParam int roomCapacity) {
        try {
            List<Room> rooms = roomService.getRoomByRoomCapacity(hotelId, roomCapacity);
            if (rooms.isEmpty()) {
                return new ResponseEntity<>("No rooms found with capacity " + roomCapacity + " for hotel ID: " + hotelId, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(rooms, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to fetch rooms: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/removeRoom/{roomId}")
    public ResponseEntity<?> removeRoom(@PathVariable String roomId) {
        try {
            Room removedRoom = roomService.removeRoom(roomId);
            if (removedRoom == null) {
                return new ResponseEntity<>("Room not found with ID: " + roomId, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Room removed successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to remove room: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
