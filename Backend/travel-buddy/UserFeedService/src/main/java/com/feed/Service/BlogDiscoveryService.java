package com.feed.Service;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.feed.DTO.DiscoveryBlogResponse;

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