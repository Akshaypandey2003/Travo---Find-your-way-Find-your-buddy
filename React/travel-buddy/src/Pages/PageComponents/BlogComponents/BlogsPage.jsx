/* eslint-disable no-unused-vars */
import { shallowEqual, useSelector } from "react-redux";
import useUserData from "../../../CustomHooks/useUserData";
import BlogCard from "./BlogCard";
import { ScrollArea } from "@/components/ui/scroll-area";
import { useNavigate } from "react-router-dom";
import { useEffect, useRef } from "react";
import useBlog from "../../../CustomHooks/useBlog";

export const BlogsPage = () => {
  const navigate = useNavigate();
  const scrollRef = useRef(null); // 👈 create ref

  const user = useSelector((store) => store.auth, shallowEqual);
  const blog = useSelector((store) => store.blog, shallowEqual);
  // console.log("Blog data: ", blog);
  const { updateBlogViews } = useBlog();
  const loggedInUser = user?.user;
  const currentBlogs = blog?.blogs;
  console.log("Current blogs: ", currentBlogs);

  useEffect(() => {
    const scrollContainer = scrollRef.current?.querySelector(
      "[data-radix-scroll-area-viewport]"
    );


    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          // console.log("Intersection entry:", entry);
          if (entry.isIntersecting) {
            const blogId = entry.target.getAttribute("data-id");
           
            navigate(`/blogs/${blogId}`, { replace: true });
             if (!blog?.blogViews?.includes(loggedInUser?.userId)) {
              // console.log("Updating blog views for ID:", blogId);
              if(loggedInUser)
              updateBlogViews(blogId, loggedInUser?.userId);
            }
          }
        });
      },
      {
        root: scrollContainer,
        threshold: 0.5,
      }
    );
    const blogElements = scrollContainer.querySelectorAll(".blog");
    // console.log("Blog elements: ", blogElements);
    blogElements.forEach((el) => observer.observe(el));

    return () => {
      blogElements.forEach((el) => observer.unobserve(el));
    };
  }, [navigate]);

  // useEffect(() => {
  //   const scrollContainer = scrollRef.current?.querySelector(
  //     "[data-radix-scroll-area-viewport]"
  //   ); // 👈 get actual scrollable element

  //   if (!scrollContainer) return;

  //   const options = {
  //     root: scrollContainer,
  //     rootMargin: "0px",
  //     threshold: 0.6,
  //   };

  //   const observer = new IntersectionObserver((entries) => {
  //     entries.forEach((entry) => {
  //       if (entry.isIntersecting) {
  //         const blogId = entry.target.getAttribute("data-id");
  //         navigate(`/blogs/${blogId}`, { replace: true });
  //       }
  //     });
  //   }, options);

  //   // ✅ Select only .blog elements inside scrollable container
  //   const blogElements = scrollContainer.querySelectorAll(".blog");
  //   blogElements.forEach((el) => observer.observe(el));

  //   return () => {
  //     blogElements.forEach((el) => observer.unobserve(el));
  //   };
  // }, [navigate]);
  // const allComments = useSelector(
  //   (store) => store.comment.comments,
  //   shallowEqual
  // );
  // console.log("All comments: ", allComments);
  return (
    <div>
      <ScrollArea ref={scrollRef} className="h-[80vh] overflow-y-auto border">
        <div className="py-10">
          {currentBlogs?.map((item, index) => (
            <div key={item?.blogId} data-id={item?.blogId} className="blog">
              <BlogCard key={index} blog={item} />
            </div>
          ))}
        </div>
      </ScrollArea>
    </div>
  );
};

export default BlogsPage;
