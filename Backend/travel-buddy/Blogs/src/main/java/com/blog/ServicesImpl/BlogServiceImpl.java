package com.blog.ServicesImpl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.blog.Services.BlogService;
import com.blog.Services.CloudinaryService;
import com.blog.Services.EventPublisher;
import com.blog.Services.NotificationProducer;

@Service
public class BlogServiceImpl implements BlogService {
    private static final Logger logger = LoggerFactory.getLogger(BlogServiceImpl.class);

    private final BlogRepo blogRepo;
    private final CloudinaryService cloudinaryService;
    private final NotificationProducer notificationProducer;
    private final UserServiceClient userServiceClient;
    private final LikeRepository likeRepo;
    private final CommentRepository commentRepo;
    private final EventPublisher eventPublisher;
    private final int maxPageSize;

    @Override
    public Page<Blog> getBlogsByAuthors(List<String> authorIds, int size) {
        if (authorIds == null || authorIds.isEmpty()) {
            return Page.empty();
        }
        Pageable pageable = PageRequest.of(0, normalizePageSize(size), Sort.by(Sort.Direction.DESC, "createdAt"));
        return blogRepo.findByAuthorIdIn(authorIds, pageable);
    }

    public BlogServiceImpl(
            BlogRepo blogRepo,
            CloudinaryService cloudinaryService,
            NotificationProducer notificationProducer,
            UserServiceClient userServiceClient,
            LikeRepository likeRepo,
            CommentRepository commentRepo,
            EventPublisher eventPublisher,
            @Value("${blog.pagination.max-page-size:100}") int maxPageSize) {
        this.blogRepo = blogRepo;
        this.cloudinaryService = cloudinaryService;
        this.notificationProducer = notificationProducer;
        this.userServiceClient = userServiceClient;
        this.likeRepo = likeRepo;
        this.commentRepo = commentRepo;
        this.eventPublisher = eventPublisher;
        this.maxPageSize = maxPageSize;
    }

    @Override
    @Transactional
    public ApiResponse<Blog> createBlog(CreateBlogRequest blog, String authorId) {
        Blog newBlog = Blog.builder()
                .title(blog.getTitle())
                .content(blog.getContent())
                .caption(blog.getCaption())
                .category(blog.getCategory())
                .authorId(authorId)
                .authorName(blog.getAuthorName())
                .authorProfilePic(blog.getAuthorProfilePic())
                .imageUrls(blog.getImageUrls() == null ? Collections.emptyList() : blog.getImageUrls())
                .cloudinaryPublicIds(blog.getCloudinaryPublicIds() == null
                        ? Collections.emptyList()
                        : blog.getCloudinaryPublicIds())
                .createdAt(Instant.now())
                .build();

        Blog savedBlog = blogRepo.save(newBlog);

        List<String> friends = userServiceClient.getFriendsByUser(authorId);

        if (friends == null || friends.isEmpty()) {

            logger.info(
                    "No friends found for authorId: {}",
                    authorId);

        } else {
            Set<String> friendSet = new HashSet<>(friends);

            for (String friend : friendSet) {
                notificationProducer.postBlogNotification(
                        authorId, friend,
                        savedBlog.getId(), savedBlog.getTitle());

            }
        }
        eventPublisher.publishBlogCreated(savedBlog);

        return new ApiResponse<>(true, savedBlog, "Blog created successfully");
    }

    @Override
    public ApiResponse<Blog> getBlogById(String blogId) {
        Blog blog = blogRepo.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with id: " + blogId));

        return new ApiResponse<>(true, blog, "Blog fetched successfully");
    }

    @Override
    public ApiResponse<Blog> updateBlog(String blogId, UpdateBlogRequest updateReq, String authorId) {

        Blog existingBlog = blogRepo.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with id: " + blogId));

        if (!existingBlog.getAuthorId().equals(authorId)) {
            throw new UnauthorizedAccessException("User is not authorized to update this blog.");
        }

        // Update text fields
        if (updateReq.getTitle() != null)
            existingBlog.setTitle(updateReq.getTitle());

        if (updateReq.getContent() != null)
            existingBlog.setContent(updateReq.getContent());

        if (updateReq.getCategory() != null)
            existingBlog.setCategory(updateReq.getCategory());

        if (updateReq.getCaption() != null)
            existingBlog.setCaption(updateReq.getCaption());

        existingBlog.setUpdatedAt(Instant.now());

        // Handle blog images
        List<String> existingImageUrls = existingBlog.getImageUrls();
        List<String> newImageUrls = updateReq.getImageUrls();
        List<String> existingPublicIds = existingBlog.getCloudinaryPublicIds();

        if (newImageUrls != null && existingImageUrls != null && existingPublicIds != null) {
            List<String> publicIdsToDelete = new ArrayList<>();

            for (int i = 0; i < existingImageUrls.size(); i++) {
                String oldUrl = existingImageUrls.get(i);

                if (!newImageUrls.contains(oldUrl)) {
                    // This old image is not present in the new list → should delete it
                    String publicId = existingPublicIds.size() > i ? existingPublicIds.get(i) : null;
                    if (publicId != null) {
                        publicIdsToDelete.add(publicId);
                    }
                }
            }

            // Now delete from Cloudinary
            for (String publicId : publicIdsToDelete) {
                try {
                    cloudinaryService.deleteImage(publicId);
                } catch (Exception e) {
                    logger.warn("Failed to delete image from Cloudinary: {}", publicId, e);
                }
            }
        }

        if (newImageUrls != null) {
            existingBlog.setImageUrls(newImageUrls);
        }
        if (updateReq.getCloudinaryPublicIds() != null) {
            existingBlog.setCloudinaryPublicIds(updateReq.getCloudinaryPublicIds());
        }

        Blog updatedBlog = blogRepo.save(existingBlog);

        return new ApiResponse<>(true, updatedBlog, "Blog Updated Successfylly.");
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteBlog(String blogId, String authorId) {

        Blog blog = blogRepo.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with id: " + blogId));

        if (!blog.getAuthorId().equals(authorId)) {
            throw new UnauthorizedAccessException("User is not authorized to delete this blog.");
        }

        List<String> cloudinaryPublicIds = blog.getCloudinaryPublicIds() == null ? Collections.emptyList()
                : blog.getCloudinaryPublicIds();
        for (String publicId : cloudinaryPublicIds) {
            try {
                cloudinaryService.deleteImage(publicId);
            } catch (Exception e) {
                logger.warn("Failed to delete Cloudinary image while deleting blog: {}", publicId, e);
            }
        }
        likeRepo.deleteByResourceIdAndResourceType(blogId, ResourceType.BLOG);
        commentRepo.deleteByBlogId(blogId);
        eventPublisher.publishBlogDeleted(blogId, authorId);
        blogRepo.delete(blog);
        return new ApiResponse<>(true, null, "Blog deleted successfully");
    }

    @Override
    public ApiResponse<Page<Blog>> getAllBlogs(int page, int size) {

        Pageable pageable = PageRequest.of(page, normalizePageSize(size), Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Blog> blogPage = blogRepo.findAll(pageable);
        return new ApiResponse<>(true, blogPage, "Blogs fetched successfully.");
    }

    @Override
    public ApiResponse<Page<Blog>> getBlogsByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, normalizePageSize(size), Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Blog> blogPage = blogRepo.findByCategoryIgnoreCase(category, pageable);
        return new ApiResponse<>(true, blogPage, "Blogs fetched successfully.");
    }

    @Override
    public ApiResponse<Page<Blog>> getBlogsByAuthor(String authorId, int page, int size) {

        Pageable pageable = PageRequest.of(page, normalizePageSize(size), Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Blog> blogPage = blogRepo.findByAuthorId(authorId, pageable);
        return new ApiResponse<>(true, blogPage, "Blogs fetched successfully.");

    }

    @Override
    public ApiResponse<List<Blog>> getBlogsByDateRange(Instant startDate, Instant endDate) {
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Invalid date range");
        }
        List<Blog> fetchedBlogs = blogRepo.findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate);
        return new ApiResponse<>(true, fetchedBlogs, "Blogs fetched successfully.");
    }

    @Override
    public ApiResponse<Page<Blog>> getBlogsByKeyword(String keyword, int page, int size) {

        Pageable pageable = PageRequest.of(page, normalizePageSize(size), Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Blog> blogPage = blogRepo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword,
                pageable);

        return new ApiResponse<>(true, blogPage, "Blogs fetched successfully.");
    }

    @Override
    public ApiResponse<Page<Blog>> getBlogsByTitle(String title, int page, int size) {

        Pageable pageable = PageRequest.of(page, normalizePageSize(size), Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Blog> blogPage = blogRepo.findByTitleIgnoreCase(title, pageable);
        return new ApiResponse<>(true, blogPage, "Blogs fetched successfully.");
    }

    @Transactional
    @Override
    public ApiResponse<Blog> likeBlog(String blogId, String userId) {
        Blog blog = blogRepo.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with id: " + blogId));

        Optional<Like> like = likeRepo.findByResourceIdAndResourceTypeAndUserId(blogId, ResourceType.BLOG, userId);

        if (like.isPresent()) {
            likeRepo.delete(like.get());
        } else {
            Like newLike = Like.builder()
                    .resourceId(blogId)
                    .resourceType(ResourceType.BLOG)
                    .userId(userId)
                    .build();
            likeRepo.save(newLike);
        }

        int likeCount = likeRepo.countByResourceIdAndResourceType(blogId, ResourceType.BLOG);
        blog.setLikeCount(likeCount);
        Blog updatedBlog = blogRepo.save(blog);

        return new ApiResponse<>(true, updatedBlog, "Blog Liked Successfully.");
    }

    @Override
    public ApiResponse<Blog> updateBlogViews(String blogId) {
        Blog blog = blogRepo.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with id: " + blogId));
        blog.setViewCount(blog.getViewCount() + 1);
        Blog updatedBlog = blogRepo.save(blog);

        return new ApiResponse<>(true, updatedBlog, "Blog views updated Successfully.");
    }

    private int normalizePageSize(int size) {
        if (size <= 0) {
            return 10;
        }
        return Math.min(size, maxPageSize);
    }
}
