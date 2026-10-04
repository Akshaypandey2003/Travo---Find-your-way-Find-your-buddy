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

      const existingIds = new Set(
        state.blogs.map((blog) => blog?.resourceId || blog?.eventId || blog?.blogId || blog?.id),
      );

      const newBlogs = blogs.filter(
        (blog) => {
          const id = blog?.resourceId || blog?.eventId || blog?.blogId || blog?.id;
          return id && !existingIds.has(id);
        },
      );

      state.blogs.push(...newBlogs);
    },
    filterBlog: (state, action) => {
      const { blogId } = action.payload;
      state.blogs = state.blogs.filter((blog) => blog?.blogId != blogId);
    },
    clearBlogsData: (state, action) => {
      state.blogs = [];
    },
    updatePostViews: (state, action) => {
      const { blogId, viewsCount } = action.payload;
      const blog = state.blogs.find(
        (item) => (item?.resourceId || item?.blogId || item?.id) === blogId,
      );
      if (blog && Number.isFinite(viewsCount)) blog.viewsCount = viewsCount;
    },
    // updatePostLike: (state, action) => {
    //   const { blogId, userId } = action.payload;
    //   console.log("User id : ", userId, " blog id: ", blogId);

    //   const blogIndex = state.blogs.findIndex((blog) => blog?.blogId == blogId);
    //   if (blogIndex !== -1) {
    //     const blog = state.blogs[blogIndex];

    //     // Ensure blogLikes exists
    //     if (!blog.blogLikes) {
    //       blog.blogLikes = [];
    //     }

    //     const likedUsers = blog.blogLikes;

    //     console.log("Liked users: ", likedUsers);

    //     if (likedUsers.includes(userId)) {
    //       // Remove like
    //       blog.blogLikes = likedUsers.filter((user) => user !== userId);
    //     } else {
    //       // Add like
    //       blog.blogLikes.push(userId);
    //     }
    //   }
    // },

    updatePostLike: (state,action)=>{
      const { blogId, likesCount, likedByMe } = action.payload;
      const blogIndex = state.blogs.findIndex(
        (blog) => (blog?.resourceId || blog?.blogId || blog?.id) === blogId,
      );
      
       if (blogIndex !== -1) {
        const blog = state.blogs[blogIndex];

        if (Number.isFinite(likesCount)) blog.likesCount = likesCount;
        if (typeof likedByMe === "boolean") blog.likedByMe = likedByMe;
      }

    },
    incrementPostCommentCount: (state, action) => {
      const { blogId } = action.payload;
      const blog = state.blogs.find(
        (item) => (item?.resourceId || item?.blogId || item?.id) === blogId,
      );
      if (blog) blog.commentsCount = (blog.commentsCount || 0) + 1;
    },
    setBlogsNextPageToken: (state, action) => {
      state.nextPageToken = action.payload;
    },
  },
});
export const {
  addBlog,
  clearBlogsData,
  filterBlog,
  updatePostLike,
  incrementPostCommentCount,
  updatePostViews,
  setBlogsNextPageToken,
} = blogsSlice.actions;
export default blogsSlice.reducer;
