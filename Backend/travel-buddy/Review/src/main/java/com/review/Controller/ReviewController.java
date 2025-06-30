package com.review.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.review.Entity.Review;
import com.review.Services.ReviewService;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;




@AllArgsConstructor
@NoArgsConstructor
@RestController
@RequestMapping("/review")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;

    @PostMapping("/add")
    public ResponseEntity<?> createReview(@RequestBody Review review) 
    {
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(review));
        }
        catch(Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getReviewByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(reviewService.getReviewByUserId(userId));
    }
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<Review>> getReviewByHotelId(@PathVariable String hotelId) {
        return ResponseEntity.ok(reviewService.getReviewByHotelId(hotelId));
    }
    @GetMapping("/{reviewId}")
    public ResponseEntity<Review> getReviewByReviewId(@PathVariable String reviewId) {
        return ResponseEntity.ok(reviewService.getReviewByReviewId(reviewId));
    }
    @PutMapping("/{reviewId}")
    public ResponseEntity<Review> updateReview(@PathVariable String reviewId,@RequestBody Review review) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, review));
    }
    @PutMapping("/{reviewId}/hotelResponse")
    public ResponseEntity<Review> updateHotelResponse(@PathVariable String reviewId, @RequestParam String hotelResponse) {
        return ResponseEntity.ok(reviewService.updateHotelResponse(reviewId, hotelResponse));
    }
    @PutMapping("/{reviewId}/reviewLikes")
    public ResponseEntity<Review> updateReviewLikes(@PathVariable String reviewId, @RequestParam int reviewLikes) {
        return ResponseEntity.ok(reviewService.updateReviewLikes(reviewId, reviewLikes));
    }
    @PutMapping("/{reviewId}/isEdited")
       public ResponseEntity<Review> updateIsEdited(@PathVariable String reviewId, @RequestParam boolean isEdited) {
            return ResponseEntity.ok(reviewService.updateIsEdited(reviewId, isEdited));
        }    
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<Review> deleteReview(@PathVariable String reviewId) {
        return ResponseEntity.ok(reviewService.deleteReview(reviewId));
    }
}
