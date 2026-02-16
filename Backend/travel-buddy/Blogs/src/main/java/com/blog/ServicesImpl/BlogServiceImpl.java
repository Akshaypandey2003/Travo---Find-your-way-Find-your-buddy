package com.blog.ServicesImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blog.Client.UserServiceClient;
import com.blog.DTO.ApiResponse;
import com.blog.Entity.Blog;
import com.blog.Exceptions.BlogNotFoundException;
import com.blog.Repositories.BlogRepo;
import com.blog.Services.BlogService;
import com.blog.Services.CloudinaryService;
import com.blog.Services.NotificationProducer;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogRepo blogRepo;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private NotificationProducer notificationProducer;

    @Autowired
    private UserServiceClient userServiceClient;


    @Override
    @Transactional
    public ApiResponse<Blog> createBlog(Blog blog) {
         blog.setPostedDate(LocalDateTime.now());
        Blog savedBlog = blogRepo.save(blog);

        List<String> friends = userServiceClient.getFriendsByUser(blog.getBlogAuthorId());
        Set<String> friendSet = new HashSet<>(friends);

        for(String friend: friendSet)
        {
                 notificationProducer.postBlogNotification(
                    savedBlog.getBlogAuthorId(), friend, 
                    savedBlog.getBlogId(),savedBlog.getBlogTitle());

        }
       
        return new ApiResponse<>(true,savedBlog,"Blog created successfully");
    }

    @Override
    public ApiResponse<Blog> getBlogById(String blogId) {
       Blog blog = blogRepo.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with id: " + blogId));

        return new ApiResponse<>(true, blog, "Blog fetched successfully");
    }

    @Override
    public ApiResponse<Blog> updateBlog(String blogId, Blog blog) {

    Blog existingBlog = blogRepo.findById(blogId)
            .orElseThrow(() -> new BlogNotFoundException("Blog not found with id: " + blogId));

    // Update text fields
    if (blog.getBlogTitle() != null)
        existingBlog.setBlogTitle(blog.getBlogTitle());

    if (blog.getBlogContent() != null)
        existingBlog.setBlogContent(blog.getBlogContent());

    if (blog.getBlogCategory() != null)
        existingBlog.setBlogCategory(blog.getBlogCategory());

    existingBlog.setUpdatedDate(LocalDateTime.now());

    // Handle blog images
    List<String> existingImageUrls = existingBlog.getBlogImages();
    List<String> newImageUrls = blog.getBlogImages();
    List<String> existingPublicIds = existingBlog.getCloudinaryImagePublicIds();

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
                System.err.println("Failed to delete image from Cloudinary: " + publicId);
                e.printStackTrace();
            }
        }
    }

    // Set updated image URLs and public IDs
    existingBlog.setBlogImages(newImageUrls);
    existingBlog.setCloudinaryImagePublicIds(blog.getCloudinaryImagePublicIds());

    Blog updatedBlog = blogRepo.save(existingBlog);

    return new ApiResponse<>(true,updatedBlog,"Blog Updated Successfylly.");
}

    @Override
    public ApiResponse<Void> deleteBlog(String blogId) {
        Blog blog = blogRepo.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with id: " + blogId));
        
        for(String publicId: blog.getCloudinaryImagePublicIds())
        {
             try {
                cloudinaryService.deleteImage(publicId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        blogRepo.delete(blog);
       return new ApiResponse<>(true, null, "Blog deleted successfully");
    }

    @Override
    public ApiResponse<List<Blog>> getAllBlogs() {
        List<Blog> blogs = blogRepo.findAll();
        if (blogs != null && !blogs.isEmpty()) {
            return new ApiResponse<>(true,blogs,"Blogs fetched Successfylly.");
        }
        throw new BlogNotFoundException("No blogs found");
    }

    @Override
    public ApiResponse<List<Blog>> getBlogsByCategory(String category) {

        List<Blog> blogs = blogRepo.findByBlogCategory(category);
        if (blogs != null && !blogs.isEmpty()) {
           return new ApiResponse<>(true,blogs,"Blogs fetched Successfylly.");
        }
        throw new BlogNotFoundException("No blogs found for category: " + category);
    }

    @Override
    public ApiResponse<List<Blog>> getBlogsByAuthor(String authorId) {
        List<Blog> blogs = blogRepo.findByBlogAuthorId(authorId);
        if (blogs != null && !blogs.isEmpty()) {
           return new ApiResponse<>(true,blogs,"Blogs fetched Successfylly.");
        }
        throw new BlogNotFoundException("No blogs found for author: " + authorId);

    }

    @Override
    public ApiResponse<List<Blog>> getBlogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {

        List<Blog> blogs = blogRepo.findAll();
        if (blogs != null && !blogs.isEmpty()) {
            List<Blog> fetchedBlogs = blogs.stream()
                    .filter(blog -> blog.getPostedDate().isAfter(startDate) && blog.getPostedDate().isBefore(endDate))
                    .toList();
             return new ApiResponse<>(true,fetchedBlogs,"Blogs fetched Successfylly.");
        } else {
            throw new BlogNotFoundException("No blogs found for the given date range: " + startDate + " to " + endDate);
        }
    }

    @Override
    public ApiResponse<List<Blog>> getBlogsByKeyword(String keyword) {

        List<Blog> blogs = blogRepo.findAll();
        if (blogs != null && !blogs.isEmpty()) {
            List<Blog> fetchedBlogs = blogs.stream()
                    .filter(blog -> blog.getBlogTitle().toLowerCase().contains(keyword.toLowerCase())
                            || blog.getBlogContent().toLowerCase().contains(keyword.toLowerCase()))
                    .toList();
            return new ApiResponse<>(true,fetchedBlogs,"Blogs fetched Successfylly.");
        } else {
            throw new BlogNotFoundException("No blogs found for the given keyword: " + keyword);
        }

    }

    @Override
    public ApiResponse<List<Blog>> getBlogsByTitle(String title) {
        List<Blog> blogs = blogRepo.findByBlogTitle(title);
        if (blogs != null && !blogs.isEmpty()) {
             return new ApiResponse<>(true,blogs,"Blogs fetched Successfylly.");
        } else {
            throw new BlogNotFoundException("No blogs found for the given title: " + title);
        }
    }

    @Override
    public ApiResponse<Blog> likeBlog(String blogId, String userId) 
    {
         Blog blog = blogRepo.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with id: " + blogId));

        List<String> likedUsers = blog.getBlogLikes();
        if (likedUsers == null) {
            likedUsers = new ArrayList<>();
        }
        if (likedUsers.contains(userId)) {
            likedUsers.remove(userId);
        } else {
            likedUsers.add(userId);
            notificationProducer.likeBlogNotification(
                userId, 
                blog.getBlogAuthorId(), blog.getBlogId(),
                blog.getBlogTitle());
        }
        Blog updatedBlog = blogRepo.save(blog);

        return new ApiResponse<>(true,updatedBlog,"Blog Liked Successfully.");
    }

    @Override
    public ApiResponse<Blog> updateBlogViews(String blogId, String userId) {
        Blog blog = blogRepo.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with id: " + blogId));
       Set<String> views = blog.getBlogViews();
        if (views == null) {
            views = new HashSet<>();
        }
        views.add(userId);
        blog.setBlogViews(views);
       Blog updatedBlog =  blogRepo.save(blog);

        return new ApiResponse<>(true,updatedBlog,"Blog views updated Successfully.");
    }
}
