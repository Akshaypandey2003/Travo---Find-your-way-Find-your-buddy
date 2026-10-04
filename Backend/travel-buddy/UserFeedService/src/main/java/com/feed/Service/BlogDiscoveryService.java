package com.feed.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.feed.DTO.DiscoveryBlogResponse;
import com.feed.DTO.BlogEngagementResponse;

@Service
public class BlogDiscoveryService {

    private static final Logger logger = LoggerFactory.getLogger(BlogDiscoveryService.class);

    private final RestTemplate restTemplate;

    @Value("${blog.service.base-url:http://localhost:8081}")
    private String blogServiceBaseUrl;

    public BlogDiscoveryService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<DiscoveryBlogResponse> getDiscoveryBlogs(List<String> publicAuthorIds, int limit) {
        return getBlogs(publicAuthorIds, limit);
    }

    public List<DiscoveryBlogResponse> getBlogsByAuthor(String authorId, int limit) {
        return getBlogs(authorId == null ? Collections.emptyList() : List.of(authorId), limit);
    }

    public List<BlogEngagementResponse> getEngagement(List<String> blogIds, String authorization) {
        if (blogIds == null || blogIds.isEmpty() || authorization == null || authorization.isBlank()) {
            return Collections.emptyList();
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, authorization);
            headers.setContentType(MediaType.APPLICATION_JSON);
            ResponseEntity<List<BlogEngagementResponse>> response = restTemplate.exchange(
                    blogServiceBaseUrl + "/api/v1/blogs/engagement/batch",
                    HttpMethod.POST,
                    new HttpEntity<>(Map.of("blogIds", blogIds), headers),
                    new ParameterizedTypeReference<List<BlogEngagementResponse>>() {});
            return response.getBody() == null ? Collections.emptyList() : response.getBody();
        } catch (Exception ex) {
            logger.error("Failed to fetch blog engagement: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    private List<DiscoveryBlogResponse> getBlogs(List<String> authorIds, int limit) {
        if (authorIds.isEmpty() || limit <= 0) {
            return Collections.emptyList();
        }

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(blogServiceBaseUrl + "/api/v1/blogs/internal/discovery")
                .queryParam("size", Math.min(limit, 100));
        authorIds.forEach(authorId -> builder.queryParam("authorIds", authorId));

        try {
            ResponseEntity<List<DiscoveryBlogResponse>> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<DiscoveryBlogResponse>>() {});
            return response.getBody() == null ? Collections.emptyList() : response.getBody();
        } catch (Exception ex) {
            logger.error("Failed to fetch discovery blogs: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }
}