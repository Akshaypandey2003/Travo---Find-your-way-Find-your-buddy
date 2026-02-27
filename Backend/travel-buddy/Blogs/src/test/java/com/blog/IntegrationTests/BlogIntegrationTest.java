package com.blog.IntegrationTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.blog.Client.UserServiceClient;
import com.blog.DTO.CommentRequestDTO;
import com.blog.DTO.CommentResponseDTO;
import com.blog.DTO.CreateBlogRequest;
import com.blog.DTO.UpdateBlogRequest;
import com.blog.Entity.Blog;
import com.blog.Repositories.BlogRepo;
import com.blog.Repositories.CommentRepository;
import com.blog.Repositories.LikeRepository;
import com.blog.Services.CloudinaryService;
import com.blog.Services.EventPublisher;
import com.blog.Services.NotificationProducer;
import com.blog.ServicesImpl.BlogServiceImpl;
import com.blog.ServicesImpl.CommentServiceImpl;

@DataMongoTest
@Testcontainers(disabledWithoutDocker = true)
class BlogIntegrationTest {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void registerMongo(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @Autowired
    private BlogRepo blogRepo;
    @Autowired
    private CommentRepository commentRepo;
    @Autowired
    private LikeRepository likeRepo;

    private BlogServiceImpl blogService;
    private CommentServiceImpl commentService;

    private CloudinaryService cloudinaryService;
    private NotificationProducer notificationProducer;
    private UserServiceClient userServiceClient;
    private EventPublisher eventPublisher;

    @BeforeEach
    void setUp() throws Exception {
        cloudinaryService = Mockito.mock(CloudinaryService.class);
        notificationProducer = Mockito.mock(NotificationProducer.class);
        userServiceClient = Mockito.mock(UserServiceClient.class);
        eventPublisher = Mockito.mock(EventPublisher.class);

        doNothing().when(eventPublisher).publishBlogCreated(any(Blog.class));
        doNothing().when(eventPublisher).publishBlogDeleted(anyString(), anyString());
        doNothing().when(notificationProducer).postBlogNotification(anyString(), anyString(), anyString(), anyString());
        doNothing().when(notificationProducer).sendCommentNotification(anyString(), anyString(), anyString(), anyString());
        doNothing().when(cloudinaryService).deleteImage(anyString());
        when(userServiceClient.getFriendsByUser(anyString())).thenReturn(List.of("f1", "f2"));

        blogService = new BlogServiceImpl(
                blogRepo,
                cloudinaryService,
                notificationProducer,
                userServiceClient,
                likeRepo,
                commentRepo,
                eventPublisher,
                100);

        commentService = new CommentServiceImpl(
                commentRepo,
                blogService,
                likeRepo,
                notificationProducer);
    }

    @AfterEach
    void cleanup() {
        likeRepo.deleteAll();
        commentRepo.deleteAll();
        blogRepo.deleteAll();
    }

    @Test
    void createAndReadBlog_success() {
        CreateBlogRequest req = CreateBlogRequest.builder()
                .title("Integration Title")
                .content("Integration Content")
                .category("Tech")
                .authorName("Author")
                .build();

        Blog created = blogService.createBlog(req, "author-1").getData();
        Blog fetched = blogService.getBlogById(created.getId()).getData();

        assertThat(fetched.getTitle()).isEqualTo("Integration Title");
        assertThat(fetched.getAuthorId()).isEqualTo("author-1");
    }

    @Test
    void likeBlog_toggle_updatesLikeCount() {
        Blog blog = blogService.createBlog(CreateBlogRequest.builder()
                .title("Likeable")
                .content("x")
                .category("Tech")
                .build(), "owner").getData();

        Blog first = blogService.likeBlog(blog.getId(), "u1").getData();
        assertThat(first.getLikeCount()).isEqualTo(1);

        Blog second = blogService.likeBlog(blog.getId(), "u1").getData();
        assertThat(second.getLikeCount()).isZero();
    }

    @Test
    void addCommentAndQueryTopLevel_success() {
        Blog blog = blogService.createBlog(CreateBlogRequest.builder()
                .title("Commentable")
                .content("x")
                .category("Tech")
                .build(), "owner").getData();

        commentService.addComment(CommentRequestDTO.builder()
                .blogId(blog.getId())
                .content("first")
                .build(), "user-2");

        Page<CommentResponseDTO> page =
                commentService.getTopLevelCommentsByBlogId(blog.getId(), PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getContent()).isEqualTo("first");
    }

    @Test
    void updateAndDeleteBlog_cascadesToCommentsAndLikes() {
        Blog blog = blogService.createBlog(CreateBlogRequest.builder()
                .title("ToUpdate")
                .content("x")
                .category("Tech")
                .build(), "owner").getData();

        commentService.addComment(CommentRequestDTO.builder()
                .blogId(blog.getId())
                .content("c1")
                .build(), "u2");
        blogService.likeBlog(blog.getId(), "u2");

        UpdateBlogRequest update = new UpdateBlogRequest();
        update.setTitle("Updated");
        blogService.updateBlog(blog.getId(), update, "owner");
        assertThat(blogService.getBlogById(blog.getId()).getData().getTitle()).isEqualTo("Updated");

        blogService.deleteBlog(blog.getId(), "owner");
        assertThat(blogRepo.findById(blog.getId())).isEmpty();
        assertThat(commentRepo.findByBlogIdAndParentCommentIdIsNullOrderByCreatedAtDesc(
                blog.getId(), PageRequest.of(0, 10)).getTotalElements()).isZero();
        assertThat(likeRepo.countByResourceIdAndResourceType(
                blog.getId(), com.blog.Enum.ResourceType.BLOG)).isZero();
    }
}
