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
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Card, CardContent, CardFooter, CardHeader } from "@/components/ui/card";
import {
  faEye,
  faHeart,
  faShare,
  faTrash,
} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useEffect, useRef } from "react";
import { useSelector } from "react-redux";
import { DEFAULT_GENERIC_PROFILE_PIC} from "../../../Constants/constants";
import CreateBlogForm from "./CreateBlogForm";
import { useNavigate } from "react-router-dom";
import useBlog from "../../../CustomHooks/useBlog";
import useHelperMethods from "../../../CustomHooks/useHelperMethods";
import BlogComments from "./BlogComments";
import ImageCarousel from "./ImageCaraousel";

export const BlogCard = ({ blog }) => {
  const { deleteBlog, updateBlogLike, updateBlogView } = useBlog();
  const { formatTimeAgo } = useHelperMethods();
  const navigate = useNavigate();
  const cardRef = useRef(null);

  const loggedInUser = useSelector((store) => store.auth.user);

  // ✅ View count when card visible
  useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          updateBlogView(blog.blogId, loggedInUser.userId);
          observer.disconnect();
        }
      },
      { threshold: 0.6 }
    );

    if (cardRef.current) observer.observe(cardRef.current);

    return () => observer.disconnect();
  }, []);

  return (
    <Card
      ref={cardRef}
      className="w-full max-w-3xl mb-4 p-4 mx-auto cursor-pointer"
      onClick={() => navigate(`/blogs/${blog.blogId}`)}
    >
      <CardHeader>
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Avatar className="border-2 border-orange-700">
              <AvatarImage
                src={
                  blog.blogAuthorProfilePic
                    ? blog.blogAuthorProfilePic
                    : DEFAULT_GENERIC_PROFILE_PIC
                }
              />
              <AvatarFallback>
                {blog.blogAuthorName?.charAt(0)}
              </AvatarFallback>
            </Avatar>
            <h1 className="text-lg font-semibold">
              {blog.blogAuthorName}
            </h1>
          </div>

          {blog.blogAuthorId === loggedInUser.userId && (
            <div
              className="flex items-center gap-2"
              onClick={(e) => e.stopPropagation()}
            >
              <CreateBlogForm formType="update" blog={blog} />

              <AlertDialog>
                <AlertDialogTrigger className="border border-orange-700 py-1 px-2">
                  <FontAwesomeIcon
                    icon={faTrash}
                    className="text-orange-700 text-lg"
                  />
                </AlertDialogTrigger>

                <AlertDialogContent>
                  <AlertDialogHeader>
                    <AlertDialogTitle>
                      Are you absolutely sure?
                    </AlertDialogTitle>
                    <AlertDialogDescription>
                      This action cannot be undone.
                    </AlertDialogDescription>
                  </AlertDialogHeader>

                  <AlertDialogFooter>
                    <AlertDialogCancel>Cancel</AlertDialogCancel>
                    <AlertDialogAction
                      onClick={() => deleteBlog(blog.blogId)}
                    >
                      Continue
                    </AlertDialogAction>
                  </AlertDialogFooter>
                </AlertDialogContent>
              </AlertDialog>
            </div>
          )}
        </div>
      </CardHeader>

      <CardContent>
        <div className="p-4">
          <h1 className="font-semibold">
            {blog.blogTitle}: {blog.blogCaption}
          </h1>
          <p className="mt-5">{blog.blogContent}</p>
        </div>

        {blog.blogImages?.length > 0 && (
          <ImageCarousel images={blog.blogImages} />
        )}
      </CardContent>

      <CardFooter>
        <div className="w-full">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <div
                className="flex items-center gap-1"
                onClick={(e) => {
                  e.stopPropagation();
                  updateBlogLike(blog.blogId, loggedInUser.userId);
                }}
              >
                <FontAwesomeIcon
                  icon={faHeart}
                  size="xl"
                  className={
                    blog.blogLikes?.includes(loggedInUser.userId)
                      ? "text-orange-500"
                      : "text-orange-300"
                  }
                />
                <h1>{blog.blogLikes?.length}</h1>
              </div>

              <div onClick={(e) => e.stopPropagation()}>
                <BlogComments blog={blog} />
              </div>

              <FontAwesomeIcon icon={faShare} size="xl" />
            </div>

            <div className="flex items-center gap-1 text-xs">
              <FontAwesomeIcon icon={faEye} />
              <span>
                {blog.blogViews?.length}{" "}
                {blog.blogViews?.length > 1 ? "views" : "view"}
              </span>
            </div>
          </div>

          <h1 className="font-light text-xs mt-2">
            Posted {formatTimeAgo(blog.postedDate)}
          </h1>
        </div>
      </CardFooter>
    </Card>
  );
};

export default BlogCard;
