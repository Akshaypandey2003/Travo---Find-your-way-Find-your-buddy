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

import com.hotelInfo.Entity.Review;

@FeignClient(name = "REVIEW-SERVICE",url="http://localhost:8081")
public interface ReviewService {
    
    @PostMapping("/review/add")
    public ResponseEntity<?> addReview(@RequestBody Review review);

    @GetMapping("/review/user/{userId}")
    public ResponseEntity<?> getReviewByUserId(@PathVariable String userId);

    @GetMapping("/review/hotel/{hotelId}")
    public ResponseEntity<?> getReviewByHotelId(@PathVariable String hotelId);

    @GetMapping("/review/{reviewId}")
    public ResponseEntity<?> getReviewById(@PathVariable String reviewId);

    @PutMapping("/review/{reviewId}")
    public ResponseEntity<Review> updateReview(@PathVariable String reviewId,@RequestBody Review review);


    @PutMapping("/review/{reviewId}/hotelResponse")
    public ResponseEntity<Review> updateHotelResponse(@PathVariable String reviewId, @RequestParam String hotelResponse);

    @PutMapping("/review/{reviewId}/reviewLikes")
    public ResponseEntity<Review> updateReviewLikes(@PathVariable String reviewId, @RequestParam int reviewLikes);

    @PutMapping("/review/{reviewId}/isEdited")
       public ResponseEntity<Review> updateIsEdited(@PathVariable String reviewId, @RequestParam boolean isEdited);
       
    @DeleteMapping("/review/delete/{reviewId}")
    public ResponseEntity<Review> deleteReview(@PathVariable String reviewId);

}
