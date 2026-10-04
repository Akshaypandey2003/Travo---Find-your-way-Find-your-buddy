package com.blog.UnitTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.blog.Client.UserServiceClient;
import com.blog.DTO.ApiResponse;
import com.blog.DTO.CreateBlogRequest;
import com.blog.DTO.UpdateBlogRequest;
import com.blog.Entity.Blog;
import com.blog.Entity.Like;
import com.blog.Enum.ResourceType;
import com.blog.Exceptions.BlogNotFoundException;
import com.blog.Exceptions.UnauthorizedAccessException;
import com.blog.Repositories.BlogRepo;
import com.blog.Repositories.CommentRepository;
import com.blog.Repositories.LikeRepository;
import com.blog.Services.CloudinaryService;
import com.blog.Services.EventPublisher;
import com.blog.Services.NotificationProducer;
import com.blog.ServicesImpl.BlogServiceImpl;

@ExtendWith(MockitoExtension.class)
class BlogServiceTest {

    @Mock
    private BlogRepo blogRepo;
    @Mock
    private CloudinaryService cloudinaryService;
    @Mock
    private NotificationProducer notificationProducer;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private LikeRepository likeRepo;
    @Mock
    private CommentRepository commentRepo;
    @Mock
    private EventPublisher eventPublisher;
        @Mock
        private MongoTemplate mongoTemplate;

    private BlogServiceImpl blogService;

    @BeforeEach
    void setUp() {
        blogService = new BlogServiceImpl(
                blogRepo,
                cloudinaryService,
                notificationProducer,
                userServiceClient,
                likeRepo,
                commentRepo,
                eventPublisher,
                mongoTemplate,
                50);
    }

    @Test
    void createBlog_success_savesNotifiesAndPublishesEvent() {
        CreateBlogRequest request = CreateBlogRequest.builder()
                .title("Title")
                .content("Content")
                .category("Tech")
                .authorName("Author")
                .build();

        Blog persisted = Blog.builder()
                .id("blog-1")
                .title("Title")
                .authorId("author-1")
                .build();

        when(blogRepo.save(any(Blog.class))).thenReturn(persisted);
        when(userServiceClient.getFriendsByUser("author-1"))
                .thenReturn(List.of("f1", "f1", "f2"));

        ApiResponse<Blog> response = blogService.createBlog(request, "author-1");

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getId()).isEqualTo("blog-1");
        verify(notificationProducer, times(2))
                .postBlogNotification(eq("author-1"), any(String.class), eq("blog-1"), eq("Title"));
        verify(eventPublisher).publishBlogCreated(persisted);
    }

    @Test
    void updateBlog_throwsForbidden_whenAuthorMismatch() {
        Blog existing = Blog.builder().id("b1").authorId("owner").build();
        when(blogRepo.findById("b1")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> blogService.updateBlog("b1", new UpdateBlogRequest(), "other"))
                .isInstanceOf(UnauthorizedAccessException.class);
    }

    @Test
    void deleteBlog_success_deletesResourcesAndEmitsEvent() throws Exception {
        Blog blog = Blog.builder()
                .id("b1")
                .authorId("owner")
                .cloudinaryPublicIds(List.of("p1"))
                .build();
        when(blogRepo.findById("b1")).thenReturn(Optional.of(blog));

        blogService.deleteBlog("b1", "owner");

        verify(cloudinaryService).deleteImage("p1");
        verify(likeRepo).deleteByResourceIdAndResourceType("b1", ResourceType.BLOG);
        verify(commentRepo).deleteByBlogId("b1");
        verify(eventPublisher).publishBlogDeleted("b1", "owner");
        verify(blogRepo).delete(blog);
    }

    @Test
    void deleteBlog_throwsNotFound_whenMissing() {
        when(blogRepo.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> blogService.deleteBlog("missing", "owner"))
                .isInstanceOf(BlogNotFoundException.class);
    }

    @Test
    void likeBlog_togglesLikeAndUpdatesCount() {
        Blog blog = Blog.builder().id("b1").build();
        Like existingLike = Like.builder().id("l1").build();

        when(blogRepo.findById("b1")).thenReturn(Optional.of(blog));
        when(likeRepo.findByResourceIdAndResourceTypeAndUserId("b1", ResourceType.BLOG, "u1"))
                .thenReturn(Optional.of(existingLike))
                .thenReturn(Optional.empty());
        when(likeRepo.countByResourceIdAndResourceType("b1", ResourceType.BLOG))
                .thenReturn(0)
                .thenReturn(1);
        ApiResponse<com.blog.DTO.BlogEngagementResponse> first = blogService.likeBlog("b1", "u1");
        assertThat(first.getData().getLikesCount()).isZero();

        ApiResponse<com.blog.DTO.BlogEngagementResponse> second = blogService.likeBlog("b1", "u1");
        assertThat(second.getData().getLikesCount()).isEqualTo(1);
        verify(likeRepo).delete(existingLike);
        verify(likeRepo).save(any(Like.class));
        verify(mongoTemplate, times(2)).updateFirst(any(Query.class), any(Update.class), eq(Blog.class));
    }

    @Test
    void getEngagement_returnsCountsAndWhetherViewerLikedEachBlog() {
        Blog blog = Blog.builder()
                .id("b1")
                .likeCount(4)
                .commentCount(2)
                .viewCount(9)
                .build();
        when(blogRepo.findAllById(List.of("b1"))).thenReturn(List.of(blog));
        when(likeRepo.findByResourceTypeAndResourceIdInAndUserId(ResourceType.BLOG, List.of("b1"), "u1"))
                .thenReturn(List.of(Like.builder().resourceId("b1").build()));

        List<com.blog.DTO.BlogEngagementResponse> result = blogService.getEngagement(List.of("b1"), "u1");

        assertThat(result).singleElement().satisfies(engagement -> {
            assertThat(engagement.getLikesCount()).isEqualTo(4);
            assertThat(engagement.getCommentsCount()).isEqualTo(2);
            assertThat(engagement.getViewsCount()).isEqualTo(9);
            assertThat(engagement.isLikedByMe()).isTrue();
        });
    }

    @Test
    void updateBlogViews_usesAtomicIncrementAndReturnsCurrentEngagement() {
        Blog updated = Blog.builder().id("b1").viewCount(5).build();
        when(blogRepo.findById("b1")).thenReturn(Optional.of(updated));
        when(likeRepo.findByResourceIdAndResourceTypeAndUserId("b1", ResourceType.BLOG, "u1"))
                .thenReturn(Optional.empty());

        var response = blogService.updateBlogViews("b1", "u1");

        assertThat(response.getData().getViewsCount()).isEqualTo(5);
        assertThat(response.getData().isLikedByMe()).isFalse();
        verify(mongoTemplate).updateFirst(any(Query.class), any(Update.class), eq(Blog.class));
    }

    @Test
    void getBlogsByDateRange_throwsBadRequest_whenInvalidRange() {
        Instant now = Instant.now();

        assertThatThrownBy(() -> blogService.getBlogsByDateRange(now, now.minusSeconds(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getAllBlogs_capsPageSizeToConfiguredMaximum() {
        Page<Blog> page = new PageImpl<>(List.of(Blog.builder().id("b1").build()));
        when(blogRepo.findAll(any(Pageable.class))).thenReturn(page);

        ApiResponse<Page<Blog>> response = blogService.getAllBlogs(0, 999);

        assertThat(response.isSuccess()).isTrue();
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(blogRepo).findAll(pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(50);
    }

    @Test
    void updateBlog_ignoresNullImageUpdates() throws Exception {
        Blog existing = Blog.builder()
                .id("b1")
                .authorId("owner")
                .imageUrls(List.of("u1"))
                .cloudinaryPublicIds(List.of("p1"))
                .build();
        when(blogRepo.findById("b1")).thenReturn(Optional.of(existing));
        when(blogRepo.save(any(Blog.class))).thenAnswer(i -> i.getArgument(0));

        UpdateBlogRequest request = new UpdateBlogRequest();
        request.setTitle("Updated");

        ApiResponse<Blog> response = blogService.updateBlog("b1", request, "owner");

        assertThat(response.getData().getTitle()).isEqualTo("Updated");
        assertThat(response.getData().getImageUrls()).containsExactly("u1");
        verify(cloudinaryService, never()).deleteImage(any(String.class));
    }

}
