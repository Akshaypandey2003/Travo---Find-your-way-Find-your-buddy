/* eslint-disable no-unused-vars */

import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { addBlog, filterBlog, updatePostLike, updatePostViews } from "../Redux/Slices/blogsSlice";
import { apiRequest, getApiErrorMessage } from "../lib/api";
import { notifyApiError, notifyApiSuccess } from "../lib/apiNotifications";

const useBlog = ()=>{
   
  const dispatch = useDispatch();
  const navigate = useNavigate();

   const uploadImageToCloudinary = async (file) => {

    const formData = new FormData();
    formData.append("file", file);
    formData.append("upload_preset", "TravoApp");  // Replace with your preset
    formData.append("cloud_name", "dwg7vniow");          // Replace with your Cloudinary cloud name
  
    const { data } = await apiRequest(`https://api.cloudinary.com/v1_1/dwg7vniow/image/upload`, {
      method: "POST",
      body: formData,
    });

     console.log("Image uploaded successfully: ",data);
    return  { 
      url: data.secure_url, 
      public_id: data.public_id 
    }; // This is the image URL & public id associated with that image
  };

 const postBlog = async (blogData) => {
  try {

    const token = localStorage.getItem("token");
    console.log("Received blog data is: ", blogData);

    const imageUploadPromises = blogData.imageUrls.map(async (imageFile) => {

      if (imageFile instanceof File) {
        return await uploadImageToCloudinary(imageFile);
      }
    });

    const uploadedImages = await Promise.all(imageUploadPromises);

    const imageUrls = uploadedImages.map((img) => img.url);
    const publicIds = uploadedImages.map((img) => img.public_id);

    const finalBlogData = {
      ...blogData,
      imageUrls: imageUrls, // replace File objects with Cloudinary URLs
      cloudinaryPublicIds: publicIds, // optional, in case you need to delete later
    };

    console.log("final blog data after uploading images: ",finalBlogData);

    const { raw: data } = await apiRequest(
        `http://localhost:8085/api/v1/blogs`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
          },
          body: JSON.stringify(finalBlogData),
        }
      );
        console.log("Blog Created Successfully: ", data);
        dispatch(addBlog(data?.data));
        notifyApiSuccess(dispatch, data?.message);
      
      navigate("/dashboard");

    // console.log("Final blog data to send to backend: ", finalBlogData);
    
  } catch (error) {
    notifyApiError(dispatch, getApiErrorMessage(error));
    console.error("Something went wrong while creating blog", error);
  }
};

const updateBlog = async (blogData, blogId) => {
  console.log("Received blog id in update blog function: ", blogId);
  console.log("Received blog in update blog function: ", blogData);

  const token = localStorage.getItem("token");

  try {
    const imageUploadPromises = blogData.blogImages.map(async (imageFile, index) => {
      if (imageFile instanceof File) {
        const uploaded = await uploadImageToCloudinary(imageFile);
        return {
          url: uploaded?.url,
          public_id: uploaded?.public_id,
        };
      } else {
        // Use existing URL and its matching public_id
        return {
          url: imageFile,
          public_id: blogData.cloudinaryImagePublicIds?.[index] || null,
        };
      }
    });

    const uploadedImages = await Promise.all(imageUploadPromises);

    const imageUrls = uploadedImages.map((img) => img?.url);
    const publicIds = uploadedImages.map((img) => img?.public_id);

    const finalBlogData = {
      ...blogData,
      blogImages: imageUrls,
      cloudinaryImagePublicIds: publicIds,
    };

    console.log("Blog data to update after image uploads: ", finalBlogData);

    const { raw: data } = await apiRequest(
      `http://localhost:8085/api/v1/blogs/${blogId}`,
      {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`,
        },
        body: JSON.stringify(finalBlogData),
      }
    );

    console.log("Blog updated Successfully: ", data);
    dispatch(addBlog(data?.data));
    notifyApiSuccess(dispatch, data?.message);
  } catch (error) {
    notifyApiError(dispatch, getApiErrorMessage(error));
    console.error("Something went wrong while updating blog", error);
  }
};


const getAllBlogs = async(page)=>{
  const token = localStorage.getItem("token");
  try {
      const { raw: data } = await apiRequest(
        `http://localhost:8085/api/v1/blogs/get-all?page=${page}&size=10`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
          },
        }
      );
        console.log("All Blogs fetched successfully: ", data);
        dispatch(addBlog(data?.data?.content || data?.data || data));
        return data;
  } catch (error) {
        notifyApiError(dispatch, getApiErrorMessage(error));
        console.log("Some error occured while fetching blogs", error);
  } 
}

const getUserFeed = async (limit = 20) => {
  const token = localStorage.getItem("token");
  try {
    const { data } = await apiRequest(
      `http://localhost:8085/api/v1/feed?limit=${limit}`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`,
        },
      },
    );

    return Array.isArray(data) ? data : data?.content || [];
  } catch (error) {
    notifyApiError(dispatch, getApiErrorMessage(error), "getUserFeed");
    console.error("Some error occurred while fetching the user feed", error);
    return [];
  }
};

const updateBlogLike = async(blogId, userId)=>{
  console.log("User with id : ",userId," is liking blog with id: ",blogId);

  const token = localStorage.getItem("token");
  try {
    
      const { raw: data } = await apiRequest(
        `http://localhost:8085/blog/like-blog/${blogId}/${userId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
          },
        }
      );
        console.log("Blog liked successfully: ", data);
        dispatch(updatePostLike({blogId,userId}));
        notifyApiSuccess(dispatch, data?.message);
  } catch (error) {
        notifyApiError(dispatch, getApiErrorMessage(error));
        console.log("Some error occured while liking blogs",error);
  } 
}

const updateBlogViews = async(blogId, userId)=>{

  const token = localStorage.getItem("token");
  if(!userId)
  {
    console.log("User id is not present");
    return;
  }
    try {
    
      const { raw: data } = await apiRequest(
        `http://localhost:8085/blog/update-blog-views/${blogId}/${userId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
          },
        }
      );
        console.log("Blog view updated successfully: ", data);
        dispatch(updatePostViews({blogId,userId}));
        notifyApiSuccess(dispatch, data?.message);
  } catch (error) {
        notifyApiError(dispatch, getApiErrorMessage(error));
        console.log("Some error occured while updating blog views",error);
  } 
}
const deleteBlog = async(blogId)=>{
  const token = localStorage.getItem("token");
  try {
      const { raw: data } = await apiRequest(
        `http://localhost:8085/blog/delete-blog/${blogId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
          },
        }
      );
        console.log("Blog Deleted Successfully: ",data);
        dispatch(filterBlog({blogId}));
        notifyApiSuccess(dispatch, data?.message);
  } catch (error) {
        notifyApiError(dispatch, getApiErrorMessage(error));
        console.log("Some error occured while Deleting blog",error);
  } 
}


    return {postBlog,updateBlog,getAllBlogs,getUserFeed,deleteBlog,updateBlogLike,updateBlogViews};
}
export default useBlog;