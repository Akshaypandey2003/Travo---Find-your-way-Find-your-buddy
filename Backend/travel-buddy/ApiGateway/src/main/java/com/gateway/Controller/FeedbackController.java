package com.gateway.Controller;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.gateway.Entity.Blog;
import com.gateway.Entity.FeedBack;
import com.gateway.Entity.MessageResponse;
import org.springframework.core.ParameterizedTypeReference;

@RestController
@RequestMapping("/auth/api/feedback")
public class FeedbackController {
    
  

}
