package com.hotel.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.hotel.Entities.Hotel;
import com.hotel.Services.HotelService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/hotel")
public class HotelController {
    
    @Autowired
    private HotelService hotelService;

    @PostMapping("/add-hotel")
    public ResponseEntity<Hotel> addHotel(@RequestBody Hotel hotel) {
        
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.addHotel(hotel));
    }
    @GetMapping("/all")
    public ResponseEntity<List<Hotel>> getAllHotels() {
        return ResponseEntity.status(HttpStatus.OK).body(hotelService.getAllHotels());
    }
    @GetMapping("/{hotelId}")
    public ResponseEntity<Hotel> getHotelById(@PathVariable String hotelId) {
        return ResponseEntity.status(HttpStatus.OK).body(hotelService.getHotelById(hotelId));
    }
    @GetMapping("/category")
    public ResponseEntity<List<Hotel>> getHotelsByCategory(@RequestParam List<String> category) {
        return ResponseEntity.ok(hotelService.getHotelsByCategory(category));
    }
    @GetMapping("/type")
    public ResponseEntity<List<Hotel>> getHotelsByType(@RequestParam List<String> type) {
        return ResponseEntity.ok(hotelService.getHotelsByType(type));
    }
    @GetMapping("/city/{city}")
    public ResponseEntity<List<Hotel>> getHotelsByCity(@PathVariable String city) {

        return ResponseEntity.ok(hotelService.getHotelsByCity(city));
    }
    @GetMapping("/state/{state}")
    public ResponseEntity<List<Hotel>> getHotelsByState(@PathVariable String state) {

        return ResponseEntity.ok(hotelService.getHotelsByState(state));
    }
    
    
    
    
    
}
