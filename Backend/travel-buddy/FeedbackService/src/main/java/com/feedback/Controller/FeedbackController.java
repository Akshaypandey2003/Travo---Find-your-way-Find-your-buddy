package com.feedback.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.feedback.Entity.FeedBack;
import com.feedback.Service.FeedbackService;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

     @PostMapping("/submit")
    public ResponseEntity<?> submitFeedback(@RequestBody FeedBack feedback) {
        
        return ResponseEntity.status(HttpStatus.CREATED).body(feedbackService.submitFeedback(feedback));
    }

    @GetMapping("/get-trip-feedback/{tripId}")
    public ResponseEntity<?> getFeedBack(@PathVariable String tripId) {
        return ResponseEntity.status(HttpStatus.OK).body(feedbackService.getFeedback(tripId));
    }

    @GetMapping("/check/{tripId}/{userId}")
    public boolean checkIfSubmitted(@PathVariable String tripId, @PathVariable String userId) {
        return feedbackService.hasUserAlreadySubmitted(tripId, userId);
    }
}

