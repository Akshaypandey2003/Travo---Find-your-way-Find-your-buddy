import { useDispatch } from "react-redux";
import {
  addComment,
  setCommentNextPageToken,
  updateCommentLike,
} from "../Redux/Slices/commentSlice";
import { apiRequest, getApiErrorMessage } from "../lib/api";
import { notifyApiError, notifyApiSuccess } from "../lib/apiNotifications";

const useComments = () => {

  const dispatch = useDispatch();

  const postComment = async (commentData,blogAuthorId) => {
    const token = localStorage.getItem("token");
    console.log("Received comment data is: ", commentData);
    try {
      const { data, message } = await apiRequest(
        `http://localhost:8085/comment/post/${blogAuthorId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
              "Authorization": `Bearer ${token}`,
          },
          body: JSON.stringify(commentData),
        }
      );
      console.log("Comment posted successfully: ", data);
      dispatch(addComment(data));
      notifyApiSuccess(dispatch, message || "Comment posted successfully.");
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
      console.log("Some error occured while posting comment", error);
    }
  };


  const getComments = async (blogId, page) => {
    console.log("Fetching comments for blogId: ", blogId);
    const token = localStorage.getItem("token");
    try {
      const { data, raw } = await apiRequest(
        `http://localhost:8085/comment/get-comments/${blogId}?page=${page}&size=5`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
              "Authorization": `Bearer ${token}`,
          },
        }
      );
      if (raw?.data) {
        console.log("All Comments fetched successfully: ", raw);
        dispatch(addComment(data?.comments || data?.content || data));
        // Check for last page
        if (data?.lastPage) {
          console.log("This is last page");
          dispatch(setCommentNextPageToken({ blogId, nextPageToken: false }));
        }
      }
      return raw;
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
      console.log("Some error occured while fetching comments",error.message);
    }
  };

  const likeComment = async (commentId, userId) => {
    const token = localStorage.getItem("token");
    try {
      const { data, message } = await apiRequest(
        `http://localhost:8085/comment/like-comment/${commentId}/${userId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
              "Authorization": `Bearer ${token}`,
          },
        }
      );
      console.log("Comment liked successfully: ", data);
      dispatch(updateCommentLike({ commentId, userId }));
      notifyApiSuccess(dispatch, message || "Comment updated successfully.");
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
      console.log("Some error occured while liking comment", error.message);
    }
  };

  
  return { getComments, postComment, likeComment };
};
export default useComments;
