package com.hotelInfo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hotelInfo.Entity.AggregatedInfo;
import com.hotelInfo.Entity.Hotel;
import com.hotelInfo.Entity.Review;
import com.hotelInfo.Entity.Room;
import com.hotelInfo.Microservices.HotelService;
import com.hotelInfo.Microservices.ReviewService;
import com.hotelInfo.Microservices.RoomService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@RestController
@RequestMapping("/hotelInfo")
public class HotelInfoController {
    
    @Autowired
    private HotelService hotelService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private RoomService roomService;

    // ========================= Hotel Service Endpoints =========================

    @PostMapping("/hotel/add-hotel")
    public ResponseEntity<Hotel> addHotel(@RequestBody Hotel hotel) {
        return hotelService.addHotel(hotel);
    }

    @GetMapping("/hotel/all")
    public ResponseEntity<List<Hotel>> getAllHotels() {
        return hotelService.getAllHotels();
    }

    @GetMapping("/hotel/{hotelId}")
    @CircuitBreaker(name="getHotelInfoBreaker", fallbackMethod = "getHotelByIdFallback")
    public ResponseEntity<AggregatedInfo> getHotelById(@PathVariable String hotelId) {

         // Fetch hotel details
         ResponseEntity<Hotel> hotelResponse = hotelService.getHotelById(hotelId);
         Hotel hotel = hotelResponse.getBody();
 
         if (hotel == null) {
             return ResponseEntity.notFound().build();
         }
 
         // Fetch reviews
         ResponseEntity<List<Review>> reviewResponse = (ResponseEntity<List<Review>>) reviewService.getReviewByHotelId(hotelId);
         List<Review> reviews = reviewResponse.getBody();
 
         // Fetch rooms
        //  ResponseEntity<List<Room>> roomResponse = (ResponseEntity<List<Room>>) roomService.getRoomByHotelId(hotelId);
        //  List<Room> rooms = roomResponse.getBody();
 
         // Merge into HotelInfoResponse
         AggregatedInfo hotelInfo = new AggregatedInfo();
         hotelInfo.setHotel(hotel);
        //  hotelInfo.setRooms(rooms);
         hotelInfo.setReviews(reviews);

         return ResponseEntity.ok(hotelInfo);
    }
    //creating fallback method for getHotelById
    public ResponseEntity<AggregatedInfo> getHotelByIdFallback(String hotelId, Exception ex) {
        
        System.out.println("Fallback method called");
        return ResponseEntity.ok(new AggregatedInfo());
    }

    @GetMapping("/hotels/category")
    public ResponseEntity<List<Hotel>> getHotelByCategory(@RequestParam List<String> category) {
        return hotelService.getHotelByCategory(category);
    }

    @GetMapping("/hotels/type")
    public ResponseEntity<List<Hotel>> getHotelByType(@RequestParam List<String> type) {
        return hotelService.getHotelByType(type);
    }

    @GetMapping("/hotels/city/{city}")
    public ResponseEntity<List<Hotel>> getHotelByCity(@PathVariable String city) {
        return hotelService.getHotelByCity(city);
    }

    @GetMapping("/hotels/state/{state}")
    public ResponseEntity<List<Hotel>> getHotelByState(@PathVariable String state) {
        return hotelService.getHotelByState(state);
    }

    // ========================= Review Service Endpoints =========================

    @PostMapping("/review/add")
    public ResponseEntity<?> addReview(@RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @GetMapping("/reviews/user/{userId}")
    public ResponseEntity<?> getReviewByUserId(@PathVariable String userId) {
        return reviewService.getReviewByUserId(userId);
    }

    @GetMapping("/reviews/hotel/{hotelId}")
    public ResponseEntity<?> getReviewByHotelId(@PathVariable String hotelId) {
        return reviewService.getReviewByHotelId(hotelId);
    }

    @GetMapping("/review/{reviewId}")
    public ResponseEntity<?> getReviewById(@PathVariable String reviewId) {
        return reviewService.getReviewById(reviewId);
    }

    @PutMapping("/review/{reviewId}")
    public ResponseEntity<Review> updateReview(@PathVariable String reviewId, @RequestBody Review review) {
        return reviewService.updateReview(reviewId, review);
    }

    @PutMapping("/review/{reviewId}/hotelResponse")
    public ResponseEntity<Review> updateHotelResponse(@PathVariable String reviewId, @RequestParam String hotelResponse) {
        return reviewService.updateHotelResponse(reviewId, hotelResponse);
    }

    @PutMapping("/review/{reviewId}/reviewLikes")
    public ResponseEntity<Review> updateReviewLikes(@PathVariable String reviewId, @RequestParam int reviewLikes) {
        return reviewService.updateReviewLikes(reviewId, reviewLikes);
    }

    @PutMapping("/review/{reviewId}/isEdited")
    public ResponseEntity<Review> updateIsEdited(@PathVariable String reviewId, @RequestParam boolean isEdited) {
        return reviewService.updateIsEdited(reviewId, isEdited);
    }

    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<Review> deleteReview(@PathVariable String reviewId) {
        return reviewService.deleteReview(reviewId);
    }

    // ========================= Room Service Endpoints =========================

    @PostMapping("/room")
    public ResponseEntity<?> addRoom(@RequestBody Room room) {
        return roomService.addRoom(room);
    }

    @PutMapping("/room/{roomId}")
    public ResponseEntity<?> updateRoom(@PathVariable String roomId, @RequestBody Room room) {
        return roomService.updateRoom(roomId, room);
    }

    @PutMapping("/room/{roomId}/availability")
    public ResponseEntity<?> updateRoomAvailability(@PathVariable String roomId, @RequestParam int rooms) {
        return roomService.updateRoomAvailability(roomId, rooms);
    }

    @GetMapping("/rooms/{hotelId}")
    public ResponseEntity<?> getRoomByHotelId(@PathVariable String hotelId) {
        return roomService.getRoomByHotelId(hotelId);
    }

    @GetMapping("/rooms/capacity/{hotelId}")
    public ResponseEntity<?> getRoomByRoomCapacity(@PathVariable String hotelId, @RequestParam int roomCapacity) {
        return roomService.getRoomByRoomCapacity(hotelId, roomCapacity);
    }

    @DeleteMapping("/room/{roomId}")
    public ResponseEntity<?> removeRoom(@PathVariable String roomId) {
        return roomService.removeRoom(roomId);
    }
}
