package com.review.Services;

import java.util.List;

import com.review.Entity.Review;

public interface ReviewService {
    
    public Review createReview(Review review);
    public List<Review> getReviewByHotelId(String hotelId);
    public List<Review> getReviewByUserId(String userId);
    public Review getReviewByReviewId(String reviewId);
    public Review updateReview(String reviewId, Review review);
    public Review deleteReview(String reviewId);
    public Review updateHotelResponse(String reviewId, String hotelResponse);
    public Review updateReviewLikes(String reviewId, int reviewLikes);
    public Review updateIsEdited(String reviewId, boolean isEdited);
}
