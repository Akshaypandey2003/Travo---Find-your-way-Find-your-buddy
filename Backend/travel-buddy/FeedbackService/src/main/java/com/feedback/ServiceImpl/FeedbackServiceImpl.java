package com.feedback.ServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.feedback.Clients.TripClient;
import com.feedback.DTO.TripSummary;
import com.feedback.Entity.FeedBack;
import com.feedback.Repository.FeedbackRepo;
import com.feedback.Service.FeedbackService;
import com.feedback.Service.NotificationProducer;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private NotificationProducer notificationProducer;
    
    @Autowired
    private FeedbackRepo feedbackRepo;

    @Autowired
    private TripClient tripClient;

     @Override
    public FeedBack submitFeedback(FeedBack feedback) {

        if (feedbackRepo.findByTripIdAndAuthorId(feedback.getTripId(), feedback.getAuthorId()).isPresent()) {
            throw new RuntimeException("Feedback already submitted for this trip by the user.");
        }

        TripSummary tripSummary = tripClient.getTripSummaryById(feedback.getTripId());

        for(String member :  tripSummary.getMembers()){
               
            notificationProducer.sendFeedbackNotification(
            feedback.getAuthorId(),
            member,
            tripSummary.getTripId(),
            tripSummary.getTripName()
        );
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
