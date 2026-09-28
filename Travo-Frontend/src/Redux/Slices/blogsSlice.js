/* eslint-disable no-unused-vars */
import { createSlice } from "@reduxjs/toolkit";

const blogsSlice = createSlice({
  name: "blog",
  initialState: {
    blogs: [],
    nextPageToken: true,
  },
  reducers: {
    addBlog: (state, action) => {

       const blogs = Array.isArray(action.payload)
        ? action.payload
        : [action.payload];

    state.blogs.push(...blogs);
    },
    filterBlog: (state, action) => {
      const { blogId } = action.payload;
      state.blogs = state.blogs.filter((blog) => blog?.blogId != blogId);
    },
    clearBlogsData: (state, action) => {
      state.blogs = [];
    },
    updatePostViews: (state,action)=>{
      const {blogId,userId} = action.payload;
      
      const blogIndex = state.blogs.findIndex((blog) => blog?.blogId == blogId);
      if (blogIndex !== -1) {
        const blog = state.blogs[blogIndex];

        // Ensure blogLikes exists
        if (!blog.blogViews) {
          blog.blogViews = [];
        }

        const userViews = blog.blogViews;

        console.log("Liked users: ", userViews);

        if (!userViews.includes(userId)) {
           blog.blogViews.push(userId);
        }
      }
    },
    updatePostLike: (state, action) => {
      const { blogId, userId } = action.payload;
      console.log("User id : ", userId, " blog id: ", blogId);

      const blogIndex = state.blogs.findIndex((blog) => blog?.blogId == blogId);
      if (blogIndex !== -1) {
        const blog = state.blogs[blogIndex];

        // Ensure blogLikes exists
        if (!blog.blogLikes) {
          blog.blogLikes = [];
        }

        const likedUsers = blog.blogLikes;

        console.log("Liked users: ", likedUsers);

        if (likedUsers.includes(userId)) {
          // Remove like
          blog.blogLikes = likedUsers.filter((user) => user !== userId);
        } else {
          // Add like
          blog.blogLikes.push(userId);
        }
      }
    },
    setBlogsNextPageToken: (state, action) => {
      state.nextPageToken = action.payload;
    }
  },
});
export const {
  addBlog,
  clearBlogsData,
  filterBlog,
  updatePostLike,
  updatePostViews,
  setBlogsNextPageToken,
} = blogsSlice.actions;
export default blogsSlice.reducer;
