package com.blog.UnitTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.blog.Controller.BlogController;
import com.blog.DTO.ApiResponse;
import com.blog.DTO.CreateBlogRequest;
import com.blog.DTO.UpdateBlogRequest;
import com.blog.Entity.Blog;
import com.blog.Exceptions.BlogNotFoundException;
import com.blog.Exceptions.InvalidRequestException;
import com.blog.Services.BlogService;

@ExtendWith(MockitoExtension.class)
class BlogControllerTest {

    @Mock
    private BlogService blogService;

    private BlogController blogController;

    @BeforeEach
    void setUp() {
        blogController = new BlogController(blogService);
    }

    @Test
    void createBlog_success_returnsCreated() {
        CreateBlogRequest request = CreateBlogRequest.builder()
                .title("Title")
                .content("Content")
                .category("Tech")
                .build();

        Blog blog = Blog.builder().id("b1").title("Title").authorId("u1").build();
        when(blogService.createBlog(any(CreateBlogRequest.class), eq("u1")))
                .thenReturn(new ApiResponse<>(true, blog, "created"));

        ResponseEntity<ApiResponse<Blog>> response = blogController.createBlog(request, "u1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getId()).isEqualTo("b1");
    }

    @Test
    void createBlog_throwsInvalidRequest_whenUserMissing() {
        CreateBlogRequest request = CreateBlogRequest.builder()
                .title("Title")
                .content("Content")
                .category("Tech")
                .build();

        assertThatThrownBy(() -> blogController.createBlog(request, " "))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void updateBlog_success_returnsOk() {
        UpdateBlogRequest request = new UpdateBlogRequest();
        request.setTitle("Updated");

        Blog updated = Blog.builder().id("b1").title("Updated").build();
        when(blogService.updateBlog("b1", request, "u1"))
                .thenReturn(new ApiResponse<>(true, updated, "updated"));

        ResponseEntity<ApiResponse<Blog>> response = blogController.updateBlog("b1", request, "u1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getTitle()).isEqualTo("Updated");
    }

    @Test
    void getBlog_propagatesServiceException_whenNotFound() {
        when(blogService.getBlogById("missing")).thenThrow(new BlogNotFoundException("missing"));

        assertThatThrownBy(() -> blogController.getBlog("missing"))
                .isInstanceOf(BlogNotFoundException.class);
    }

    @Test
    void getAllBlogs_success_returnsPage() {
        Page<Blog> page = new PageImpl<>(List.of(Blog.builder().id("b1").title("t").build()));
        when(blogService.getAllBlogs(0, 10)).thenReturn(new ApiResponse<>(true, page, "ok"));

        ResponseEntity<ApiResponse<Page<Blog>>> response = blogController.getAllBlogs(0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getTotalElements()).isEqualTo(1);
    }

    @Test
    void getBlogsByDateRange_success_parsesAndDelegates() {
        Instant start = Instant.parse("2025-01-01T00:00:00Z");
        Instant end = Instant.parse("2025-01-31T00:00:00Z");
        when(blogService.getBlogsByDateRange(start, end))
                .thenReturn(new ApiResponse<>(true, List.of(), "ok"));

        ResponseEntity<?> response = blogController.getBlogsByDateRange(
                "2025-01-01T00:00:00Z",
                "2025-01-31T00:00:00Z");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
