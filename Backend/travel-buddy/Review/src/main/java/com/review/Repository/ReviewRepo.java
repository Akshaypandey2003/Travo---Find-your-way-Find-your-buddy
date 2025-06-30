package com.review.Repository;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.review.Entity.Review;

public interface ReviewRepo extends MongoRepository<Review, String> {

    public List<Review> findByUserId(String userId);
    public List<Review> findByHotelId(String hotelId);
    // public List<Review> findByRating(int rating);
    // public List<Review> findByDate(String date);
    // public List<Review> findByIsEdited(boolean isEdited);
    // public List<Review> findByReviewLikes(int reviewLikes);
    // public List<Review> findByTitle(String title);
    // public List<Review> findByHotelResponse(String hotelResponse);
    // public List<Review> findByImages(List<String> images);
    
}
