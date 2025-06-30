import { useDispatch } from "react-redux";
import {
  addComment,
  setCommentNextPageToken,
  updateCommentLike,
} from "../Redux/Slices/commentSlice";

/* eslint-disable no-unused-vars */
const useComments = () => {
  const dispatch = useDispatch();

  const postComment = async (commentData,blogAuthorId) => {
    console.log("Received comment data is: ", commentData);
    try {
      const response = await fetch(
        `http://localhost:8085/auth/api/comment/post/${blogAuthorId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            //   "Authorization": `Bearer ${token}`,
          },
          body: JSON.stringify(commentData),
        }
      );
      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.message || "Comment posting failed");
      } else {
        console.log("Comment posted successfully: ", data);
        dispatch(addComment(data));
        // dispatch(updateSuccess({success:true,message:data?.messageResponse?.message}));
      }
    } catch (error) {
      console.log("Some error occured while posting comment", error.message);
    }
  };
  const getComments = async (blogId, page) => {
    console.log("Fetching comments for blogId: ", blogId);
    try {
      const response = await fetch(
        `http://localhost:8085/auth/api/comment/get-comments/${blogId}?page=${page}&size=5`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            //   "Authorization": `Bearer ${token}`,
          },
        }
      );
      const data = await response.json();
      if (!response.ok) {
        throw new Error(data?.message || "Comments Fetching failed");
      } else {
        console.log("All Comments fetched successfully: ", data);
        dispatch(addComment(data?.comments));
        // Check for last page
        if (data?.lastPage) {
          console.log("This is last page");
          dispatch(setCommentNextPageToken({ blogId, nextPageToken: false }));
        }
      }
      return data;
    } catch (error) {
      console.log("Some error occured while fetching comments",error.message);
    }
  };

  const likeComment = async (commentId, userId) => {
    try {
      const response = await fetch(
        `http://localhost:8085/auth/api/comment/like-comment/${commentId}/${userId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            //   "Authorization": `Bearer ${token}`,
          },
        }
      );
      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.message || "Comments liking failed");
      } else {
        console.log("Comment liked successfully: ", data);
        dispatch(updateCommentLike({ commentId, userId }));
      }
    } catch (error) {
      console.log("Some error occured while liking comment", error.message);
    }
  };
  return { getComments, postComment, likeComment };
};
export default useComments;
