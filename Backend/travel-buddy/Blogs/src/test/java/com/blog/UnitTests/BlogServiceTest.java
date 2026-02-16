package com.blog.UnitTests;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blog.Client.UserServiceClient;
import com.blog.DTO.ApiResponse;
import com.blog.Entity.Blog;
import com.blog.Exceptions.BlogNotFoundException;
import com.blog.Repositories.BlogRepo;
import com.blog.Services.CloudinaryService;
import com.blog.Services.NotificationProducer;
import com.blog.ServicesImpl.BlogServiceImpl;

@ExtendWith(MockitoExtension.class)
class BlogServiceTest {

    @InjectMocks
    private BlogServiceImpl blogService;

    @Mock
    private BlogRepo blogRepo;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private NotificationProducer notificationProducer;

    @Mock
    private UserServiceClient userServiceClient;

    // ---------- CREATE BLOG ----------

    @Test
    void createBlog_success_shouldNotifyFriends() {
        Blog blog = Blog.builder()
                .blogAuthorId("user1")
                .blogTitle("Test Blog")
                .build();

        when(blogRepo.save(any(Blog.class))).thenReturn(blog);
        when(userServiceClient.getFriendsByUser("user1"))
                .thenReturn(List.of("f1", "f2", "f2")); // duplicate

        ApiResponse<Blog> saved = blogService.createBlog(blog);

        assertThat(saved).isNotNull();
        verify(notificationProducer, times(2))
                .postBlogNotification(eq("user1"), anyString(), any(), eq("Test Blog"));
    }

    // ---------- GET BLOG BY ID ----------

    @Test
    void getBlogById_success() {
        Blog blog = new Blog();
        when(blogRepo.findById("1")).thenReturn(Optional.of(blog));

        ApiResponse<Blog> result = blogService.getBlogById("1");

        assertThat(result.getData()).isSameAs(blog);
    }

    @Test
    void getBlogById_notFound() {
        when(blogRepo.findById("1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> blogService.getBlogById("1"))
                .isInstanceOf(BlogNotFoundException.class);
    }

    // ---------- UPDATE BLOG ----------

    @Test
    void updateBlog_success_withImageDeletion() throws Exception {
        Blog existing = Blog.builder()
                .blogImages(List.of("old1", "old2"))
                .cloudinaryImagePublicIds(List.of("pid1", "pid2"))
                .build();

        Blog update = Blog.builder()
                .blogImages(List.of("old2")) // old1 removed
                .cloudinaryImagePublicIds(List.of("pid2"))
                .build();

        when(blogRepo.findById("1")).thenReturn(Optional.of(existing));
        when(blogRepo.save(any())).thenReturn(existing);

        ApiResponse<Blog> result = blogService.updateBlog("1", update);

        verify(cloudinaryService).deleteImage("pid1");
        assertThat(result).isNotNull();
    }

    @Test
    void updateBlog_notFound() {
        when(blogRepo.findById("1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> blogService.updateBlog("1", new Blog()))
                .isInstanceOf(BlogNotFoundException.class);
    }

    // ---------- DELETE BLOG ----------

    @Test
    void deleteBlog_success_shouldDeleteImages() throws Exception {
        Blog blog = Blog.builder()
                .cloudinaryImagePublicIds(List.of("pid1", "pid2"))
                .build();

        when(blogRepo.findById("1")).thenReturn(Optional.of(blog));

        blogService.deleteBlog("1");

        verify(cloudinaryService).deleteImage("pid1");
        verify(cloudinaryService).deleteImage("pid2");
        verify(blogRepo).delete(blog);
    }

    @Test
    void deleteBlog_notFound() {
        when(blogRepo.findById("1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> blogService.deleteBlog("1"))
                .isInstanceOf(BlogNotFoundException.class);
    }

    // ---------- GET ALL BLOGS ----------

    @Test
    void getAllBlogs_success() {
        when(blogRepo.findAll()).thenReturn(List.of(new Blog()));

        assertThat(blogService.getAllBlogs().getData()).hasSize(1);
    }

    @Test
    void getAllBlogs_empty() {
        when(blogRepo.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> blogService.getAllBlogs())
                .isInstanceOf(BlogNotFoundException.class);
    }

    // ---------- LIKE BLOG ----------

    @Test
    void likeBlog_addLike_shouldNotify() {
        Blog blog = Blog.builder()
                .blogAuthorId("author")
                .blogLikes(new ArrayList<>())
                .build();

        when(blogRepo.findById("1")).thenReturn(Optional.of(blog));
        when(blogRepo.save(any())).thenReturn(blog);

        blogService.likeBlog("1", "user");

        assertThat(blog.getBlogLikes()).contains("user");
        verify(notificationProducer).likeBlogNotification(
                "user", "author", blog.getBlogId(), blog.getBlogTitle());
    }

    @Test
    void likeBlog_removeLike() {
        Blog blog = Blog.builder()
                .blogLikes(new ArrayList<>(List.of("user")))
                .build();

        when(blogRepo.findById("1")).thenReturn(Optional.of(blog));
        when(blogRepo.save(any())).thenReturn(blog);

        blogService.likeBlog("1", "user");

        assertThat(blog.getBlogLikes()).doesNotContain("user");
    }

    // ---------- UPDATE VIEWS ----------

    @Test
    void updateBlogViews_success() {
        Blog blog = new Blog();

        when(blogRepo.findById("1")).thenReturn(Optional.of(blog));
        when(blogRepo.save(any())).thenReturn(blog);

        blogService.updateBlogViews("1", "user");

        assertThat(blog.getBlogViews()).contains("user");
    }

    // ---------- FILTER METHODS ----------

    @Test
    void getBlogsByCategory_success() {
        when(blogRepo.findByBlogCategory("tech"))
                .thenReturn(List.of(new Blog()));

        assertThat(blogService.getBlogsByCategory("tech").getData()).hasSize(1);
    }

    @Test
    void getBlogsByCategory_empty() {
        when(blogRepo.findByBlogCategory("tech"))
                .thenReturn(List.of());

        assertThatThrownBy(() -> blogService.getBlogsByCategory("tech"))
                .isInstanceOf(BlogNotFoundException.class);
    }

    @Test
    void getBlogsByAuthor_empty() {
        when(blogRepo.findByBlogAuthorId("user"))
                .thenReturn(List.of());

        assertThatThrownBy(() -> blogService.getBlogsByAuthor("user"))
                .isInstanceOf(BlogNotFoundException.class);
    }

    @Test
    void getBlogsByKeyword_success() {
        Blog blog = Blog.builder()
                .blogTitle("Spring Boot")
                .blogContent("Mockito testing")
                .postedDate(LocalDateTime.now())
                .build();

        when(blogRepo.findAll()).thenReturn(List.of(blog));

        assertThat(blogService.getBlogsByKeyword("spring").getData()).hasSize(1);
    }
}
