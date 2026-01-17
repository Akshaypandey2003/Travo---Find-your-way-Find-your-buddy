package com.feedback.ServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.feedback.Entity.FeedBack;
import com.feedback.Repository.FeedbackRepo;
import com.feedback.Service.FeedbackService;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    
      @Autowired
    private FeedbackRepo feedbackRepo;

     @Override
    public FeedBack submitFeedback(FeedBack feedback) {
        if (feedbackRepo.findByTripIdAndAuthorId(feedback.getTripId(), feedback.getAuthorId()).isPresent()) {
            throw new RuntimeException("Feedback already submitted for this trip by the user.");
        }

        return feedbackRepo.save(feedback);
    }

    @Override
    public List<FeedBack> getFeedback(String tripId) {
        return feedbackRepo.findAllByTripId(tripId);
    }

    @Override
    public boolean hasUserAlreadySubmitted(String tripId, String userId) {
        return feedbackRepo.findByTripIdAndAuthorId(tripId, userId).isPresent();
    }
}
