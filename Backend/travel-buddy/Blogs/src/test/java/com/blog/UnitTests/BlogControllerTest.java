package com.blog.UnitTests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.blog.Config.JwtProvider;
import com.blog.Controller.BlogController;
import com.blog.DTO.ApiResponse;
import com.blog.Entity.Blog;
import com.blog.Exceptions.BlogNotFoundException;
import com.blog.Services.BlogService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BlogController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings({ "removal" })
class BlogControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private BlogService blogService;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private JwtProvider jwtProvider;

        private ApiResponse<Blog> blogResponse() {
                Blog blog = new Blog();
                blog.setBlogId("blog123");
                blog.setBlogTitle("Spring Boot Testing");
                blog.setBlogContent("Testing controllers properly");
                blog.setBlogCategory("TECH");
                blog.setBlogAuthorId("user1");
                blog.setPostedDate(LocalDateTime.now());
                blog.setBlogLikes(List.of("user2"));
                blog.setBlogViews(Set.of("user3"));
                return new ApiResponse<>(true, blog, "success");
        }
        /* ---------------- CREATE BLOG ---------------- */

        @Test
        void shouldCreateBlog() throws Exception {
                ApiResponse<Blog> response = blogResponse();

                Mockito.when(blogService.createBlog(any(Blog.class))).thenReturn(response);

                Blog blog = response.getData();

                mockMvc.perform(post("/blog/create-blog")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(blog)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.blogId").value("blog123"))
                                .andExpect(jsonPath("$.data.blogTitle").value("Spring Boot Testing"));
        }

        /* ---------------- UPDATE BLOG ---------------- */

        @Test
        void shouldUpdateBlog() throws Exception {
                ApiResponse<Blog> updatedBlog = blogResponse();
                updatedBlog.getData().setBlogTitle("Updated Title");

                Mockito.when(blogService.updateBlog(eq("blog123"), any(Blog.class)))
                                .thenReturn(updatedBlog);

                mockMvc.perform(put("/blog/update-blog/blog123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedBlog)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.blogTitle").value("Updated Title"));
        }

        /* ---------------- LIKE BLOG ---------------- */

        @Test
        void shouldLikeBlog() throws Exception {
                ApiResponse<Blog> blog = blogResponse();

                Mockito.when(blogService.likeBlog("blog123", "user5"))
                                .thenReturn(blog);

                mockMvc.perform(post("/blog/like-blog/blog123/user5"))
                                .andExpect(status().isOk());
        }

        /* ---------------- DELETE BLOG ---------------- */

        @Test
        void shouldDeleteBlog() throws Exception {

                ApiResponse<Void> response = new ApiResponse<>(true, null, "Blog Deleted Successfully");

                Mockito.when(blogService.deleteBlog("blog123"))
                                .thenReturn(response);

                mockMvc.perform(delete("/blog/delete-blog/blog123"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Blog Deleted Successfully"));
        }

        /* ---------------- GET BLOG BY ID ---------------- */

        @Test
        void shouldGetBlogById() throws Exception {

                Mockito.when(blogService.getBlogById("blog123"))
                                .thenReturn(blogResponse());

                mockMvc.perform(get("/blog/get-blog/blog123"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.blogId").value("blog123"));
        }

        @Test
        void shouldReturn404WhenBlogNotFound() throws Exception {
                Mockito.when(blogService.getBlogById("invalid"))
                                .thenThrow(new BlogNotFoundException("Blog not found"));

                mockMvc.perform(get("/blog/get-blog/invalid"))
                                .andExpect(status().isNotFound());
        }

        /* ---------------- GET ALL BLOGS ---------------- */

        @Test
        void shouldGetAllBlogs() throws Exception {
                ApiResponse<List<Blog>> response = new ApiResponse<>(true, List.of(blogResponse().getData()),
                                "success");

                Mockito.when(blogService.getAllBlogs())
                                .thenReturn(response);

                mockMvc.perform(get("/blog/get-all-blogs"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.length()").value(1));
        }

        /* ---------------- GET BLOGS BY CATEGORY ---------------- */

        @Test
        void shouldGetBlogsByCategory() throws Exception {

                ApiResponse<List<Blog>> response = new ApiResponse<>(true, List.of(blogResponse().getData()),
                                "success");

                Mockito.when(blogService.getBlogsByCategory("TECH"))
                                .thenReturn(response);

                mockMvc.perform(get("/blog/get-blogs-by-category/TECH"))
                                .andExpect(status().isOk());
        }

        /* ---------------- GET BLOGS BY AUTHOR ---------------- */

        @Test
        void shouldGetBlogsByAuthor() throws Exception {

                ApiResponse<List<Blog>> response = new ApiResponse<>(true, List.of(blogResponse().getData()),
                                "success");

                Mockito.when(blogService.getBlogsByAuthor("user1"))
                                .thenReturn(response);

                mockMvc.perform(get("/blog/get-blogs-by-author/user1"))
                                .andExpect(status().isOk());
        }

        /* ---------------- GET BLOGS BY KEYWORD ---------------- */

        @Test
        void shouldGetBlogsByKeyword() throws Exception {

                ApiResponse<List<Blog>> response = new ApiResponse<>(true, List.of(blogResponse().getData()),
                                "success");

                Mockito.when(blogService.getBlogsByKeyword("Spring"))
                                .thenReturn(response);

                mockMvc.perform(get("/blog/get-blogs-by-keyword/Spring"))
                                .andExpect(status().isOk());
        }

        /* ---------------- GET BLOGS BY TITLE ---------------- */

        @Test
        void shouldGetBlogsByTitle() throws Exception {

                ApiResponse<List<Blog>> response = new ApiResponse<>(true, List.of(blogResponse().getData()),
                                "success");

                Mockito.when(blogService.getBlogsByTitle("Spring Boot Testing"))
                                .thenReturn(response);

                mockMvc.perform(get("/blog/get-blogs-by-title/Spring Boot Testing"))
                                .andExpect(status().isOk());
        }

        /* ---------------- UPDATE BLOG VIEWS ---------------- */

        @Test
        void shouldUpdateBlogViews() throws Exception {

                Mockito.when(blogService.updateBlogViews("blog123", "user9"))
                                .thenReturn(blogResponse());

                mockMvc.perform(post("/blog/update-blog-views/blog123/user9"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message")
                                                .value("success"))
                                .andExpect(jsonPath("$.data.blogId").value("blog123"));
        }

}
