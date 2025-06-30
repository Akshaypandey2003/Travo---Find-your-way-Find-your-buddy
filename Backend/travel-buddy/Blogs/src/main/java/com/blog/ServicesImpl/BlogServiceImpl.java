package com.blog.ServicesImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blog.Entity.Blog;
import com.blog.Exceptions.BlogNotFoundException;
import com.blog.Repositories.BlogRepo;
import com.blog.Services.BlogService;
import com.blog.Services.CloudinaryService;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogRepo blogRepo;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    public Blog createBlog(Blog blog) {
        blog.setPostedDate(LocalDateTime.now());
        return blogRepo.save(blog);
    }

    @Override
    public Blog getBlogById(String blogId) {
        return blogRepo.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with id: " + blogId));
    }

    @Override
    public Blog updateBlog(String blogId, Blog blog) {

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

    return blogRepo.save(existingBlog);
}

    @Override
    public void deleteBlog(String blogId) {
        Blog blog = blogRepo.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with id: " + blogId));
        
        for(String publicId: blog.getCloudinaryImagePublicIds())
        {
             try {
                cloudinaryService.deleteImage(publicId);
            } catch (Exception e) {
                System.err.println("Failed to delete image from Cloudinary: " + publicId);
                e.printStackTrace();
            }
        }

        blogRepo.delete(blog);
    }

    @Override
    public List<Blog> getAllBlogs() {
        List<Blog> blogs = blogRepo.findAll();
        if (blogs != null && !blogs.isEmpty()) {
            return blogs;
        }
        throw new BlogNotFoundException("No blogs found");
    }

    @Override
    public List<Blog> getBlogsByCategory(String category) {

        List<Blog> blogs = blogRepo.findByBlogCategory(category);
        if (blogs != null && !blogs.isEmpty()) {
            return blogs;
        }
        throw new BlogNotFoundException("No blogs found for category: " + category);
    }

    @Override
    public List<Blog> getBlogsByAuthor(String authorId) {
        List<Blog> blogs = blogRepo.findByBlogAuthor(authorId);
        if (blogs != null && !blogs.isEmpty()) {
            return blogs;
        }
        throw new BlogNotFoundException("No blogs found for author: " + authorId);

    }

    @Override
    public List<Blog> getBlogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {

        List<Blog> blogs = blogRepo.findAll();
        if (blogs != null && !blogs.isEmpty()) {
            return blogs.stream()
                    .filter(blog -> blog.getPostedDate().isAfter(startDate) && blog.getPostedDate().isBefore(endDate))
                    .toList();
        } else {
            throw new BlogNotFoundException("No blogs found for the given date range: " + startDate + " to " + endDate);
        }
    }

    @Override
    public List<Blog> getBlogsByKeyword(String keyword) {

        List<Blog> blogs = blogRepo.findAll();
        if (blogs != null && !blogs.isEmpty()) {
            return blogs.stream()
                    .filter(blog -> blog.getBlogTitle().toLowerCase().contains(keyword.toLowerCase())
                            || blog.getBlogContent().toLowerCase().contains(keyword.toLowerCase()))
                    .toList();
        } else {
            throw new BlogNotFoundException("No blogs found for the given keyword: " + keyword);
        }

    }

    @Override
    public List<Blog> getBlogsByTitle(String title) {
        List<Blog> blogs = blogRepo.findByBlogTitle(title);
        if (blogs != null && !blogs.isEmpty()) {
            return blogs;
        } else {
            throw new BlogNotFoundException("No blogs found for the given title: " + title);
        }
    }

    @Override
    public Blog likeBlog(String blogId, String userId) 
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
        }
        return blogRepo.save(blog);
    }

    @Override
    public Blog updateBlogViews(String blogId, String userId) {
        Blog blog = blogRepo.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with id: " + blogId));
       Set<String> views = blog.getBlogViews();
        if (views == null) {
            views = new HashSet<>();
        }
        views.add(userId);
        blog.setBlogViews(views);
        return blogRepo.save(blog);
    }
}
