package com.review.ServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.review.Entity.Review;
import com.review.Exceptions.ResourceNotFoundException;
import com.review.Repository.ReviewRepo;
import com.review.Services.ReviewService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService{


    @Autowired
    private ReviewRepo reviewRepo;

    @Override
    public Review createReview(Review review) {
        return reviewRepo.save(review);
    }

    @Override
    public List<Review> getReviewByUserId(String userId) {
       List<Review> list = reviewRepo.findByUserId(userId);
       if(list.isEmpty())
       {
        throw new ResourceNotFoundException("No review submitted by this user.");
       }
       return list;
    }

    @Override
    public List<Review> getReviewByHotelId(String hotelId) {
        List<Review> reviews = reviewRepo.findByHotelId(hotelId);
        if(reviews.isEmpty())
        {
            throw new ResourceNotFoundException("No review available for this hotel.");
        }
        return reviews;
    }

    @Override
    public Review getReviewByReviewId(String reviewId) {
        return reviewRepo.findById(reviewId).orElseThrow(()->new ResourceNotFoundException("No review found with this reviewId"));
    }

    @Transactional
    @Override
    public Review updateReview(String reviewId, Review review) {
       Review r = reviewRepo.findById(reviewId).orElseThrow(()->new ResourceNotFoundException("No review found with this reviewId"));
       
       review.setReviewId(reviewId);
       review.setUserId(r.getUserId());
       review.setHotelId(r.getHotelId());
       review.setEdited(true);
       return reviewRepo.save(review);
    }

    @Override
    public Review deleteReview(String reviewId) {
        Review review = reviewRepo.findById(reviewId).orElseThrow(()->new ResourceNotFoundException("No review found with this reviewId"));
        reviewRepo.deleteById(reviewId);
        return review;
    }

    @Transactional
    @Override
    public Review updateHotelResponse(String reviewId, String hotelResponse) {
       Review review = reviewRepo.findById(reviewId).orElseThrow(()->new ResourceNotFoundException("No review found with this reviewId"));
       review.setHotelResponse(hotelResponse);
       return reviewRepo.save(review);
    }

    @Transactional
    @Override
    public Review updateReviewLikes(String reviewId, int reviewLikes) {
       Review review = reviewRepo.findById(reviewId).orElseThrow(()->new ResourceNotFoundException("No review found with this reviewId"));
      
       review.setReviewLikes(reviewLikes);
       return reviewRepo.save(review);
    }

    @Transactional
    @Override
    public Review updateIsEdited(String reviewId, boolean isEdited) {
       Review review = reviewRepo.findById(reviewId).orElseThrow(()->new ResourceNotFoundException("No review found with this reviewId"));
       review.setEdited(isEdited);
       return reviewRepo.save(review);
    }
    
}
