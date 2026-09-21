/* eslint-disable no-unused-vars */
import { createSlice } from "@reduxjs/toolkit";

const commentSlice = createSlice({
  name: "comment",
  initialState: {
    comments: [],
    nextPageTokens: {},
    success: false,
    failure: false,
    message: "",
  },
  reducers: {
    // addComment: (state, action) => {
    //   const incomingComments = Array.isArray(action.payload)
    //     ? action.payload
    //     : [action.payload];

    //   const filteredComments = state.comments.filter(
    //     (comment) =>
    //       comment?.commentId != null &&
    //       !incomingComments?.some(
    //         (existingcomment) => existingcomment.commentId === comment.commentId
    //       )
    //   );

    //   state.comments = Array.isArray(filteredComments)
    //     ? [...incomingComments,...filteredComments]
    //     : [...incomingComments];
    // },
    addComment: (state, action) => {
      const incomingComments = Array.isArray(action.payload)
        ? action.payload
        : [action.payload];

      // Merge existing and new comments (avoiding duplicates)
      const mergedComments = [
        ...state.comments.filter(
          (existing) =>
            existing?.commentId != null &&
            !incomingComments.some(
              (newComment) => newComment.commentId === existing.commentId
            )
        ),
        ...incomingComments,
      ];

      // Sort comments by createdAt in descending order (latest first)
      state.comments = mergedComments.sort(
        (a, b) => new Date(b.createdAt) - new Date(a.createdAt)
      );
    },

    filtercomment: (state, action) => {
      const { commentId } = action.payload;
      state.comments = state.comments.filter(
        (comment) => comment?.commentId != commentId
      );
    },
    updateSuccess: (state, action) => {
      const { success, message } = action.payload;
      state.success = success;
      state.message = message;
    },
    updateFailure: (state, action) => {
      const { failure, message } = action.payload;
      state.failure = failure;
      state.message = message;
    },
    clearCommentsData: (state) => {
      state.comments = [];
      state.success = false;
      state.failure = false;
      state.message = "";
      state.nextPageToken = true;
    },
    updateCommentLike: (state, action) => {
      const { commentId, userId } = action.payload;
      console.log("User id : ", userId, " comment id: ", commentId);

      const commentIndex = state.comments.findIndex(
        (comment) => comment?.commentId == commentId
      );
      if (commentIndex !== -1) {
        const comment = state.comments[commentIndex];

        // Ensure commentLikes exists
        if (!comment.commentLikes) {
          comment.commentLikes = [];
        }

        const likedUsers = comment.commentLikes;

        console.log("Liked users: ", likedUsers);

        if (likedUsers.includes(userId)) {
          // Remove like
          comment.commentLikes = likedUsers.filter((user) => user !== userId);
        } else {
          // Add like
          comment.commentLikes.push(userId);
        }
      }
    },
    setCommentNextPageToken: (state, action) => {
      const {blogId, nextPageToken} = action.payload;
      if (!nextPageToken) {
        state.nextPageTokens[blogId] = !nextPageToken;
      }
    },
  },
});
export const {
  addComment,
  updateSuccess,
  updateFailure,
  clearCommentsData,
  filtercomment,
  updateCommentLike,
  setCommentNextPageToken,
} = commentSlice.actions;
export default commentSlice.reducer;
