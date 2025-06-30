/* eslint-disable no-unused-vars */

import { useDispatch } from "react-redux";
import { addBlog, filterBlog, updatePostLike, updatePostViews, updateSuccess } from "../Redux/Slices/blogsSlice";

const useBlog = ()=>{
   
  const dispatch = useDispatch();


   const uploadImageToCloudinary = async (file) => {

    const formData = new FormData();
    formData.append("file", file);
    formData.append("upload_preset", "TravoApp");  // Replace with your preset
    formData.append("cloud_name", "dwg7vniow");          // Replace with your Cloudinary cloud name
  
    const response = await fetch(`https://api.cloudinary.com/v1_1/dwg7vniow/image/upload`, {
      method: "POST",
      body: formData,
    });
  
    if (!response.ok) {
      throw new Error("Image upload failed");
    }
  
    const data = await response.json();
     console.log("Image uploaded successfully: ",data);
    return  { 
      url: data.secure_url, 
      public_id: data.public_id 
    }; // This is the image URL & public id associated with that image
  };

 const postBlog = async (blogData) => {

  try {
    console.log("Received blog data is: ", blogData);

    const imageUploadPromises = blogData.blogImages.map(async (imageFile) => {

      if (imageFile instanceof File) {
        return await uploadImageToCloudinary(imageFile);
      }
    });

    const uploadedImages = await Promise.all(imageUploadPromises);

    const imageUrls = uploadedImages.map((img) => img.url);
    const publicIds = uploadedImages.map((img) => img.public_id);

    const finalBlogData = {
      ...blogData,
      blogImages: imageUrls, // replace File objects with Cloudinary URLs
      cloudinaryImagePublicIds: publicIds, // optional, in case you need to delete later
    };

    const response = await fetch(
        `http://localhost:8085/auth/blog/create-blog`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          //   "Authorization": `Bearer ${token}`,
          },
          body: JSON.stringify(finalBlogData),
        }
      );
      const data = await response.json();
      if (!response.ok)
      {
         throw new Error(data.message || "Blog Creation failed");
      }
      else
      {
          console.log("Blog Created Successfully: ",data);
          dispatch(addBlog(data));
          dispatch(updateSuccess({success:true,message:data?.messageResponse?.message}));
      }
        

    // console.log("Final blog data to send to backend: ", finalBlogData);
    
  } catch (error) {
    console.error("Something went wrong while creating blog", error.message);
  }
};

const updateBlog = async (blogData, blogId) => {
  console.log("Received blog id in update blog function: ", blogId);
  console.log("Received blog in update blog function: ", blogData);

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

    const response = await fetch(
      `http://localhost:8085/auth/blog/update-blog/${blogId}`,
      {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          // "Authorization": `Bearer ${token}`,
        },
        body: JSON.stringify(finalBlogData),
      }
    );

    const data = await response.json();

    if (!response.ok) {
      throw new Error(data.message || "Blog Update failed");
    } else {
      console.log("Blog updated Successfully: ", data);
      dispatch(addBlog(data));
      dispatch(updateSuccess({ success: true, message: data?.messageResponse?.message }));
    }
  } catch (error) {
    console.error("Something went wrong while updating blog", error.message);
  }
};


const getAllBlogs = async()=>{
  try {
      const response = await fetch(
        `http://localhost:8085/auth/blog/get-all-blogs`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          //   "Authorization": `Bearer ${token}`,
          },
        }
      );
      const data = await response.json();
      if (!response.ok)
      {
         throw new Error(data.message || "Blog Fetching failed");
      }
      else
      {
          console.log("All Blogs fetched successfully: ",data);
          dispatch(addBlog(data));
          // dispatch(updateSuccess({success:true,message:data?.messageResponse?.message}));
      }
  } catch (error) {
      console.log("Some error occured while fetching blogs");
  } 
}

const updateBlogLike = async(blogId, userId)=>{
  console.log("User with id : ",userId," is liking blog with id: ",blogId);
  try {
    
      const response = await fetch(
        `http://localhost:8085/auth/blog/like-blog/${blogId}/${userId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          //   "Authorization": `Bearer ${token}`,
          },
        }
      );
      const data = await response.json(); 
      if (!response.ok)
      {
         throw new Error(data.message || "Liking Blog failed");
      }
      else
      {
          console.log("Blog liked successfully: ",data);
          dispatch(updatePostLike({blogId,userId}));
          // dispatch(updateSuccess({success:true,message:data?.messageResponse?.message}));
      }
  } catch (error) {
      console.log("Some error occured while liking blogs",error.message);
  } 
}

const updateBlogViews = async(blogId, userId)=>{
  if(!userId)
  {
    console.log("User id is not present");
    return;
  }
    try {
    
      const response = await fetch(
        `http://localhost:8085/auth/blog/update-blog-views/${blogId}/${userId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          //   "Authorization": `Bearer ${token}`,
          },
        }
      );
      const data = await response.json();
      if (!response.ok)
      {
         throw new Error(data.message || "Blog view update failed");
      }
      else
      {
          console.log("Blog view updated successfully: ",data);
          dispatch(updatePostViews({blogId,userId}));
          // dispatch(updateSuccess({success:true,message:data?.messageResponse?.message}));
      }
  } catch (error) {
      console.log("Some error occured while updating blog views",error.message);
  } 
}
const deleteBlog = async(blogId)=>{
  try {
      const response = await fetch(
        `http://localhost:8085/auth/blog/delete-blog/${blogId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
          //   "Authorization": `Bearer ${token}`,
          },
        }
      );
      const data = await response.json();
      if (!response.ok)
      {
         throw new Error(data.message || "Blog Deletion failed");
      }
      else
      {
          console.log("Blog Deleted Successfully: ",data);
          dispatch(filterBlog({blogId}));
          dispatch(updateSuccess({success:true,message:data?.message}));
      }
  } catch (error) {
      console.log("Some error occured while Deleting blog",error.message);
  } 
}


    return {postBlog,updateBlog,getAllBlogs,deleteBlog,updateBlogLike,updateBlogViews};
}
export default useBlog;