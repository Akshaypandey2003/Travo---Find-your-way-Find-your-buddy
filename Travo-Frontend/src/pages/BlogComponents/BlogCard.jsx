/* eslint-disable react/prop-types */
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog";
import { faTrash } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  Camera,
  Heart,
  MessageCircle,
  MoreHorizontal,
  Share2,
  UserPlus,
} from "lucide-react";
import { useEffect, useRef } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import useBlog from "../../CustomHooks/useBlog";
import useHelperMethods from "../../CustomHooks/useHelperMethods";
import ImageCarousel from "./ImageCaraousel";
import BlogComments from "./BlogComments";
import useFriendRequest from "../../CustomHooks/useFriendRequest";

export const BlogCard = ({ post }) => {
  const { deleteBlog, updateBlogLike, updateBlogView } = useBlog();
  const { formatTimeAgo } = useHelperMethods();
  const navigate = useNavigate();
  const cardRef = useRef(null);
  const {sendFriendRequest} = useFriendRequest();

  const loggedInUser = useSelector((store) => store.auth.user);
  console.log("Logged in user is : ", loggedInUser);

  // ✅ View count when card visible
  useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          updateBlogView(blog.blogId, loggedInUser.userId);
          observer.disconnect();
        }
      },
      { threshold: 0.6 },
    );

    if (cardRef.current) observer.observe(cardRef.current);

    return () => observer.disconnect();
  }, []);

  return (
    // <Card
    //   ref={cardRef}
    //   className="w-full max-w-3xl mb-4 p-4 mx-auto cursor-pointer"
    //   onClick={() => navigate(`/blogs/${blog.blogId}`)}
    // >
    //   <CardHeader>
    //     <div className="flex items-center justify-between">
    //       <div className="flex items-center gap-2">
    //         <Avatar className="border-2 border-orange-700">
    //           <AvatarImage
    //             src={
    //               blog.blogAuthorProfilePic
    //                 ? blog.blogAuthorProfilePic
    //                 : DEFAULT_GENERIC_PROFILE_PIC
    //             }
    //           />
    //           <AvatarFallback>
    //             {blog.blogAuthorName?.charAt(0)}
    //           </AvatarFallback>
    //         </Avatar>
    //         <h1 className="text-lg font-semibold">
    //           {blog.blogAuthorName}
    //         </h1>
    //       </div>

    //   {blog.blogAuthorId === loggedInUser.userId && (
    //     <div
    //       className="flex items-center gap-2"
    //       onClick={(e) => e.stopPropagation()}
    //     >
    //       <CreateBlogForm formType="update" blog={blog} />

    //       <AlertDialog>
    //         <AlertDialogTrigger className="border border-orange-700 py-1 px-2">
    //           <FontAwesomeIcon
    //             icon={faTrash}
    //             className="text-orange-700 text-lg"
    //           />
    //         </AlertDialogTrigger>

    //         <AlertDialogContent>
    //           <AlertDialogHeader>
    //             <AlertDialogTitle>
    //               Are you absolutely sure?
    //             </AlertDialogTitle>
    //             <AlertDialogDescription>
    //               This action cannot be undone.
    //             </AlertDialogDescription>
    //           </AlertDialogHeader>

    //           <AlertDialogFooter>
    //             <AlertDialogCancel>Cancel</AlertDialogCancel>
    //             <AlertDialogAction
    //               onClick={() => deleteBlog(blog.blogId)}
    //             >
    //               Continue
    //             </AlertDialogAction>
    //           </AlertDialogFooter>
    //         </AlertDialogContent>
    //       </AlertDialog>
    //     </div>
    //   )}
    //     </div>
    //   </CardHeader>

    //   <CardContent>
    //     <div className="p-4">
    //       <h1 className="font-semibold">
    //         {blog.blogTitle}: {blog.blogCaption}
    //       </h1>
    //       <p className="mt-5">{blog.blogContent}</p>
    //     </div>

    //     {blog.blogImages?.length > 0 && (
    //       <ImageCarousel images={blog.blogImages} />
    //     )}
    //   </CardContent>

    //   <CardFooter>
    //     <div className="w-full">
    //       <div className="flex items-center justify-between">
    //         <div className="flex items-center gap-4">
    //           <div
    //             className="flex items-center gap-1"
    //             onClick={(e) => {
    //               e.stopPropagation();
    //               updateBlogLike(blog.blogId, loggedInUser.userId);
    //             }}
    //           >
    //             <FontAwesomeIcon
    //               icon={faHeart}
    //               size="xl"
    //               className={
    //                 blog.blogLikes?.includes(loggedInUser.userId)
    //                   ? "text-orange-500"
    //                   : "text-orange-300"
    //               }
    //             />
    //             <h1>{blog.blogLikes?.length}</h1>
    //           </div>

    //           <div onClick={(e) => e.stopPropagation()}>
    //             <BlogComments blog={blog} />
    //           </div>

    //           <FontAwesomeIcon icon={faShare} size="xl" />
    //         </div>

    //         <div className="flex items-center gap-1 text-xs">
    //           <FontAwesomeIcon icon={faEye} />
    //           <span>
    //             {blog.blogViews?.length}{" "}
    //             {blog.blogViews?.length > 1 ? "views" : "view"}
    //           </span>
    //         </div>
    //       </div>

    //       <h1 className="font-light text-xs mt-2">
    //         Posted {formatTimeAgo(blog.postedDate)}
    //       </h1>
    //     </div>
    //   </CardFooter>
    // </Card>
    <>
      <div
        key={post.id}
        className="bg-white dark:bg-surface-dark rounded-2xl shadow-sm border border-slate-100 dark:border-gray-900 overflow-hidden"
      >
        <div className="p-4 flex items-center justify-between">
          <div
            className="flex items-center gap-3 cursor-pointer"
            onClick={() => navigate(`/profile/${post.user.userId}`)}
          >
            <div className="relative">
              <img
                src={post.user.avatar}
                className="w-11 h-11 rounded-full object-cover border-2 border-primary/10"
                alt={post.user.name}
              />
              <div className="absolute -bottom-0.5 -right-0.5 w-3.5 h-3.5 bg-emerald-500 rounded-full border-2 border-white dark:border-surface-dark" />
            </div>
            <div>
              <h3 className="font-bold text-sm tracking-tight">
                {post.user.name}
              </h3>
              <p className="text-[10px] text-slate-500 font-bold uppercase tracking-widest flex items-center gap-1.5 mt-0.5">
                <span className="w-1 h-1 bg-slate-300 dark:bg-slate-700 rounded-full" />{" "}
                Posted {formatTimeAgo(post.time)}
              </p>
            </div>
          </div>
          {post?.user?.userId == loggedInUser.userId && (
            <div
              className="flex items-center gap-2"
              onClick={(e) => e.stopPropagation()}
            >
              {/* <CreateBlogForm formType="update" blog={blog} /> */}

              <AlertDialog>
                <AlertDialogTrigger className="py-1 px-2">
                  {/* <FontAwesomeIcon
                  icon={faTrash}
                  className="text-orange-700 text-lg"
                /> */}
                  <MoreHorizontal size={20} />
                </AlertDialogTrigger>

                <AlertDialogContent>
                  <AlertDialogHeader>
                    <AlertDialogTitle>
                      Are you absolutely sure?
                    </AlertDialogTitle>
                    <AlertDialogDescription>
                      <div className="flex flex-col">
                        <button>Edit</button>
                        <button>Delete</button>
                      </div>
                      This action cannot be undone.
                    </AlertDialogDescription>
                  </AlertDialogHeader>

                  <AlertDialogFooter>
                    <AlertDialogCancel>Cancel</AlertDialogCancel>
                    <AlertDialogAction onClick={() => deleteBlog(post?.blogId)}>
                      Continue
                    </AlertDialogAction>
                  </AlertDialogFooter>
                </AlertDialogContent>
              </AlertDialog>
            </div>
          )}
          {/* <button className="text-slate-400 hover:text-primary transition-colors">
                  <MoreHorizontal size={20} />
                </button> */}
        </div>
        <div className="px-5 pb-4">
          <p className="text-sm leading-relaxed mb-4 text-slate-700 dark:text-slate-300 font-medium">
            {post.content}
          </p>
          <div className="flex flex-wrap gap-2">
            {post.tags?.map((tag) => (
              <span
                key={tag}
                className="px-2.5 py-1 rounded-md bg-primary/10 text-primary text-[10px] font-bold uppercase tracking-wider"
              >
                {tag}
              </span>
            ))}
            {post.specialTag && (
              <span className="px-2.5 py-1 rounded-md bg-orange-500/20 text-orange-400 text-[10px] font-bold uppercase tracking-wider">
                {post.specialTag}
              </span>
            )}
          </div>
        </div>

        {post.images?.length > 0 && <ImageCarousel images={post.images} />}

        {/* {post.images && (
          <div className="relative aspect-video">
            <img
              src={post.image}
              className="w-full h-full object-cover"
              alt="Travel post"
            />
            <div className="absolute bottom-4 right-4 bg-black/50 backdrop-blur-md text-white text-[10px] font-bold px-3 py-1 rounded-full flex items-center gap-2 uppercase tracking-widest border border-white/10">
              <Camera size={14} /> {post.user.name}
            </div>
          </div>
        )} */}
        <div className="px-5 py-4 border-t border-slate-100 dark:border-gray-900 flex items-center justify-between">
          <div className="flex gap-8">
            <button
              className="flex items-center gap-2.5 text-slate-500 dark:text-slate-400 hover:text-primary transition-colors group"
              onClick={(e) => {
                e.stopPropagation();
                updateBlogLike(blog.blogId, loggedInUser.userId);
              }}
            >
              <Heart size={20} className="group-hover:fill-current" />
              <span className="text-xs font-bold">{post.likes}</span>
            </button>
            {/* <button className="flex items-center gap-2.5 text-slate-500 dark:text-slate-400 hover:text-primary transition-colors">
              <MessageCircle size={20} />
              <span className="text-xs font-bold">{post.comments}</span>
            </button> */}
            <div onClick={(e) => e.stopPropagation()}>
              <BlogComments blog={post} />
            </div>
            <button className="flex items-center gap-2.5 text-slate-500 dark:text-slate-400 hover:text-primary transition-colors">
              <Share2 size={20} />
            </button>
          </div>
          {post?.user?.userId != loggedInUser.userId && (
          <button className="flex items-center gap-2 text-primary hover:text-primary-hover text-[10px] font-bold uppercase tracking-widest transition-colors"
          onClick={() => sendFriendRequest(post?.user?.userId, post?.user?.name)}>
            <UserPlus size={18} />
            Connect
          </button>
          )}
        </div>
      </div>
    </>
  );
};

export default BlogCard;
