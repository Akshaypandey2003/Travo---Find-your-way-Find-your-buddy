package com.hotelInfo.Microservices;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotelInfo.Entity.Hotel;

@FeignClient(name = "HOTEL-SERVICE",url="http://localhost:8080")
public interface HotelService {

    @PostMapping("/hotel/add-hotel")
    ResponseEntity<Hotel> addHotel(@RequestBody Hotel hotel);

    @GetMapping("/hotel/all")
    ResponseEntity<List<Hotel>> getAllHotels();

    @GetMapping("/hotel/{hotelId}")
    ResponseEntity<Hotel> getHotelById(@PathVariable String hotelId);

    @GetMapping("/hotel/category")
    ResponseEntity<List<Hotel>> getHotelByCategory(@RequestParam List<String> category);

    @GetMapping("/hotel/type")
    ResponseEntity<List<Hotel>> getHotelByType(@RequestParam List<String> type);

    @GetMapping("/hotel/city/{city}")
    ResponseEntity<List<Hotel>> getHotelByCity(@PathVariable String city);

    @GetMapping("/hotel/state/{state}")
    ResponseEntity<List<Hotel>> getHotelByState(@PathVariable String state);
}
    

