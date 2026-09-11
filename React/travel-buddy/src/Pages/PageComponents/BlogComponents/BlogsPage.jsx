/* eslint-disable react-hooks/exhaustive-deps */
// /* eslint-disable react-hooks/exhaustive-deps */
// /* eslint-disable no-unused-vars */
// import { shallowEqual, useDispatch, useSelector } from "react-redux";
// import useUserData from "../../../CustomHooks/useUserData";
// import BlogCard from "./BlogCard";
// import { ScrollArea } from "@/components/ui/scroll-area";
// import { useNavigate } from "react-router-dom";
// import { useCallback, useEffect, useRef, useState } from "react";
// import useBlog from "../../../CustomHooks/useBlog";
// import { current } from "@reduxjs/toolkit";
// import { setBlogsNextPageToken } from "../../../Redux/Slices/blogsSlice";

// export const BlogsPage = () => {

//   const navigate = useNavigate();
//   const scrollRef = useRef(null); // 👈 create ref

//   const user = useSelector((store) => store.auth, shallowEqual);
//   const blog = useSelector((store) => store.blog, shallowEqual);
//   const [loading,setLoading] = useState(false);
//   const [page,setPage] = useState(0);
//   const { getAllBlogs } = useBlog();
//   const dispatch = useDispatch();
//   const loadMoreRef = useRef(null);
//   const { updateBlogViews } = useBlog();
//   const loggedInUser = user?.user;
//   const currentBlogs = blog?.blogs;
  
//   console.log("Current blogs: ", currentBlogs);

//   // useEffect(() => {
//   //   const scrollContainer = scrollRef.current?.querySelector(
//   //     "[data-radix-scroll-area-viewport]"
//   //   );


//   //   const observer = new IntersectionObserver(
//   //     (entries) => {
//   //       entries.forEach((entry) => {
//   //         if (entry.isIntersecting) {
//   //           const blogId = entry.target.getAttribute("data-id");
           
//   //           navigate(`/blogs/${blogId}`, { replace: true });
//   //            if (!blog?.blogViews?.includes(loggedInUser?.userId)) {
//   //             if(loggedInUser)
//   //             updateBlogViews(blogId, loggedInUser?.userId);
//   //           }
//   //         }
//   //       });
//   //     },
//   //     {
//   //       root: scrollContainer,
//   //       threshold: 0.5,
//   //     }
//   //   );
//   //   const blogElements = scrollContainer.querySelectorAll(".blog");
//   //   blogElements.forEach((el) => observer.observe(el));

//   //   return () => {
//   //     blogElements.forEach((el) => observer.unobserve(el));
//   //   };
//   // }, [navigate,blog?.blogViews, loggedInUser, updateBlogViews]);


//   useEffect(() => {
//   if (!currentBlogs?.length) return;

//   const scrollContainer = scrollRef.current?.querySelector(
//     "[data-radix-scroll-area-viewport]"
//   );

//   if (!scrollContainer) return;

//   const observer = new IntersectionObserver(
//     (entries) => {
//       entries.forEach((entry) => {
//         if (entry.isIntersecting) {
//           const blogId = entry.target.getAttribute("data-id");

//           if (
//             loggedInUser &&
//             !blog?.blogViews?.includes(loggedInUser?.userId)
//           ) {
//             updateBlogViews(blogId, loggedInUser.userId);
//           }
//         }
//       });
//     },
//     {
//       root: scrollContainer,
//       threshold: 0.5,
//     }
//   );

//   const blogElements = scrollContainer.querySelectorAll(".blog");
//   blogElements.forEach((el) => observer.observe(el));

//   return () => observer.disconnect();
// }, [currentBlogs, loggedInUser, blog?.blogViews, updateBlogViews]);


//   const blogNextPageToken = useSelector(
//     (store) => store.blog.nextPageToken,
//     shallowEqual
//   );

//   const loadMoreBlogs = useCallback(async () => {
//     if (!blogNextPageToken || loading) return;

//     setLoading(true);
//     setPage((page) => page + 1);
//     const newUsers = await getAllBlogs(page);
//     const pageSize = 10 + Math.pow(page, 2);

//     if (newUsers && newUsers.length < pageSize) {
//       dispatch(setBlogsNextPageToken(false));
//     }
//     setLoading(false);
//   }, [page, loading]);

//   useEffect(() => {
//     const observer = new IntersectionObserver(
//       (entries) => {
//         if (entries[0].isIntersecting) {
//           loadMoreBlogs();
//         }
//       },
//       {
//         root: scrollRef.current?.querySelector(
//           "[data-radix-scroll-area-viewport]"
//         ),
//         threshold: 1.0,
//       }
//     );
//     const loadMoreDiv = loadMoreRef.current;
//     if (loadMoreDiv) observer.observe(loadMoreDiv);

//     return () => {
//       if (loadMoreDiv) observer.unobserve(loadMoreDiv);
//     };
//   }, [loadMoreBlogs, currentBlogs]);


//   useEffect(()=>{
//     if(!currentBlogs || currentBlogs?.length===0)
//      getAllBlogs(page);
//   },[]);

  
//   return (
//     <div className="mt-24 w-[100vw]">
//       <ScrollArea ref={scrollRef} className="h-[82vh] overflow-y-auto">
//         <div className="py-10">
//           {currentBlogs?.map((item, index) => (
//             <div key={item?.blogId} data-id={item?.blogId} className="blog">
//               <BlogCard key={index} blog={item} />
//             </div>
//           ))}
//         </div>
//       </ScrollArea>
//     </div>
//   );
// };

// export default BlogsPage;





// -------------------- Updated Blogs Page code ---------------
import { shallowEqual, useDispatch, useSelector } from "react-redux";
import BlogCard from "./BlogCard";
import { ScrollArea } from "@/components/ui/scroll-area";
import { useCallback, useEffect, useRef, useState } from "react";
import useBlog from "../../../CustomHooks/useBlog";
import { addBlog, setBlogsNextPageToken } from "../../../Redux/Slices/blogsSlice";

export const BlogsPage = () => {

  const dispatch = useDispatch();
  const scrollRef = useRef(null);
  const loadMoreRef = useRef(null);

  const { getAllBlogs } = useBlog();

  const currentBlogs = useSelector((store) => store.blog.blogs, shallowEqual);
  const blogNextPageToken = useSelector(
    (store) => store.blog.nextPageToken,
    shallowEqual
  );

  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(false);

  // ✅ Load blogs function (correct pagination)
  const loadMoreBlogs = useCallback(async () => {
    if (loading || !blogNextPageToken) return;

    setLoading(true);

    const nextPage = page + 1;
    const data = await getAllBlogs(nextPage);

    if (!data || data.length === 0) {
      dispatch(setBlogsNextPageToken(false));
    } else {
      dispatch(addBlog(data));
      setPage(nextPage);
    }

    setLoading(false);
  }, [page, loading, blogNextPageToken]);

  

  // ✅ Infinite scroll observer (ONLY this observer in page)
  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          loadMoreBlogs();
        }
      },
      {
        root: scrollRef.current?.querySelector(
          "[data-radix-scroll-area-viewport]"
        ),
        threshold: 1.0,
      }
    );

    if (loadMoreRef.current) {
      observer.observe(loadMoreRef.current);
    }

    return () => observer.disconnect();
  }, [loadMoreBlogs]);

  // ✅ First load
  useEffect(() => {
    if (currentBlogs.length === 0) {

      console.log("Fetching initial blogs...");
      loadMoreBlogs();
    }
  }, []);

  return (
    <div className="mt-24 w-[100vw]">
      <ScrollArea ref={scrollRef} className="h-[82vh] overflow-y-auto">
        <div className="py-10">
          {currentBlogs?.map((blog) => (
            <BlogCard key={blog.blogId} blog={blog} />
          ))}

          {/* ✅ Sentinel div for infinite scroll */}
          <div
            ref={loadMoreRef}
            className="h-20 flex justify-center items-center"
          >
            {loading && <p>Loading more blogs...</p>}
          </div>
        </div>
      </ScrollArea>
    </div>
  );
};

export default BlogsPage;

