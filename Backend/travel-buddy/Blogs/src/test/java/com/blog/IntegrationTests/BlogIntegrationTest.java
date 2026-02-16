package com.blog.IntegrationTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Duration;
import java.util.*;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.blog.Config.JwtProvider;
import com.blog.Entity.Blog;
import com.blog.Entity.Comment;
import com.blog.Repositories.BlogRepo;
import com.blog.Repositories.CommentRepository;
import com.blog.Services.CloudinaryService;
import com.cloudinary.Cloudinary;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc(addFilters = false)
@EmbeddedKafka(partitions = 1, topics = {
        "notification-events" }, bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@ActiveProfiles("test")
@SuppressWarnings({ "unused", "removal" })
class BlogIntegrationTest {

    @MockBean
    private Cloudinary cloudinary;

    @MockBean
    private CloudinaryService cloudinaryService;

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BlogRepo blogRepo;

    @Autowired
    private CommentRepository commentRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProvider jwtProvider;

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    // -------------------- BLOG CREATION --------------------
    @Test
    void createBlog_shouldPersistAndSendKafkaEvent() throws Exception {

        Blog blog = new Blog();
        blog.setBlogTitle("Spring Boot Integration Test");
        blog.setBlogContent("Real DB + Kafka test");
        blog.setBlogCategory("TECH");
        blog.setBlogAuthorId("user-1");

        mockMvc.perform(post("/blog/create-blog")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(blog)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.blogTitle").value("Spring Boot Integration Test"));

        // DB assertion
        assert blogRepo.findAll().size() == 1;

        // Kafka assertion
        assertKafkaEventPublished();
    }

    // -------------------- LIKE BLOG --------------------
    @Test
    void likeBlog_shouldUpdateLikesAndSendEvent() throws Exception {

        Blog blog = new Blog();
        blog.setBlogTitle("Kafka Blog");
        blog.setBlogAuthorId("author-1");
        blog = blogRepo.save(blog);

        mockMvc.perform(post("/blog/like-blog/" + blog.getBlogId() + "/user-2"))
                .andExpect(status().isOk());

        Blog updated = blogRepo.findById(blog.getBlogId()).get();
        assert updated.getBlogLikes().contains("user-2");

        assertKafkaEventPublished();
    }

    // -------------------- ADD COMMENT --------------------
    @Test
    void addComment_shouldPersistAndSendKafkaEvent() throws Exception {

        Blog blog = new Blog();
        blog.setBlogTitle("Comment Blog");
        blog.setBlogAuthorId("author-1");
        blog = blogRepo.save(blog);

        Comment comment = new Comment();
        comment.setBlogId(blog.getBlogId());
        comment.setAuthorId("user-2");
        comment.setContent("Nice post!");

        mockMvc.perform(post("/comments")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isCreated());

        assert commentRepo.findAll().size() == 1;
        assertKafkaEventPublished();
    }

    // -------------------- PAGINATED COMMENTS --------------------
    @Test
    void getComments_shouldReturnPaginatedResponse() throws Exception {

        Blog blog = new Blog();
        blog.setBlogTitle("Pagination Blog");
        blog.setBlogAuthorId("author-1");
        blog = blogRepo.save(blog);

        Comment comment = new Comment();
        comment.setBlogId(blog.getBlogId());
        comment.setAuthorId("user-1");
        comment.setContent("Top comment");
        commentRepo.save(comment);

        mockMvc.perform(get("/comments/blog/" + blog.getBlogId())
                .param("page", "0")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.comments.length()").value(1))
                .andExpect(jsonPath("$.data.lastPage").value(true))
                .andExpect(jsonPath("$.data.currentPage").value(0))
                .andExpect(jsonPath("$.data.totalPages").value(1));
    }

    // -------------------- DELETE BLOG --------------------
    @Test
    void deleteBlog_shouldRemoveFromDB() throws Exception {

        Blog blog = new Blog();
        blog.setBlogTitle("Delete Me");
        blog = blogRepo.save(blog);

        mockMvc.perform(delete("/blog/delete-blog/" + blog.getBlogId()))
                .andExpect(status().isOk());

        assertThat(blogRepo.findById(blog.getBlogId())).isEmpty();
    }

    // -------------------- KAFKA ASSERTION HELPER --------------------
    private void assertKafkaEventPublished() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                System.getProperty("spring.kafka.bootstrap-servers"));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "blog-test-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singleton("notification-events"));
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
            assert records.count() > 0;
        }
    }
}
