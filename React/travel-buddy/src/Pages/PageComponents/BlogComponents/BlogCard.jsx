/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
import {
  Card,
  CardHeader,
  CardContent,
  CardFooter,
} from "@/components/ui/card";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { shallowEqual, useSelector } from "react-redux";
import { useEffect, useRef, useState } from "react";
import useUserData from "../../../CustomHooks/useUserData";
import {
  DEFAULT_FEMALE_PIC,
  DEFAULT_MALE_PIC,
} from "../../../Constants/constants";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faCommentDots,
  faEye,
  faHeart,
  faShare,
  faTrash,
} from "@fortawesome/free-solid-svg-icons";
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
import CreateBlogForm from "./CreateBlogForm";
import { ScrollArea, ScrollBar } from "@/components/ui/scroll-area";
import {
  differenceInHours,
  differenceInDays,
  differenceInWeeks,
  differenceInMonths,
} from "date-fns";
import useBlog from "../../../CustomHooks/useBlog";
import BlogComments from "./BlogComments";

export const BlogCard = ({ blog }) => {
  const { getUser } = useUserData();
  const { deleteBlog, updateBlogLike } = useBlog();
  const [showComment, setShowComment] = useState(false);
  const user = useSelector((store) => store.auth, shallowEqual);
  const [author, setAuthor] = useState(null);
  const loggedInUser = user?.user;
  const usersList = user?.usersList;

  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const scrollContainerRef = useRef(null);
  const imageRefs = useRef([]);
  const totalImages = blog?.blogImages?.length || 0;

  const localUser =
    blog?.blogAuthor === loggedInUser?.userId
      ? loggedInUser
      : usersList?.find((user) => user.userId === blog?.blogAuthor);

  useEffect(() => {
    if (localUser) {
      setAuthor(localUser);
    }
  }, [localUser]);

  useEffect(() => {
    const fetchUser = async () => {
      if (!localUser && !author) {
        const fetchedUser = await getUser(blog?.blogAuthor);
        setAuthor(fetchedUser);
      }
    };
    fetchUser();
  }, [localUser, author]);

  // const goToImage = (index) => {
  //   const imageElement = imageRefs.current[index];
  //   if (imageElement && scrollContainerRef.current) {
  //     scrollContainerRef.current.scrollTo({
  //       left: imageElement.offsetLeft,
  //       behavior: "smooth",
  //     });
  //   }
  // };
  const goToImage = (index) => {
    setCurrentImageIndex(index);
  };

  const handleScroll = () => {
    console.log("Handle scroll function");
    if (!scrollContainerRef.current) return;
    const container = scrollContainerRef.current;

    let closestIndex = 0;
    let minDistance = Infinity;

    imageRefs.current.forEach((imgRef, index) => {
      if (!imgRef) return;
      const distance = Math.abs(imgRef.offsetLeft - container.scrollLeft);
      if (distance < minDistance) {
        closestIndex = index;
        minDistance = distance;
      }
    });

    setCurrentImageIndex(closestIndex);
  };

  useEffect(() => {
    const container = scrollContainerRef.current;
    if (container) {
      container.addEventListener("scroll", handleScroll);
      return () => container.removeEventListener("scroll", handleScroll);
    }
  }, []);
  const formatTimeAgo = (date) => {
    const postedDate = new Date(date);
    const now = new Date();

    const hoursDiff = differenceInHours(now, postedDate);
    const daysDiff = differenceInDays(now, postedDate);
    const weeksDiff = differenceInWeeks(now, postedDate);
    const monthsDiff = differenceInMonths(now, postedDate);

    if (hoursDiff < 24 && daysDiff === 0) {
      return `${hoursDiff} ${hoursDiff === 1 ? "hour" : "hours"} ago`;
    } else if (daysDiff < 7) {
      return `${daysDiff} ${daysDiff === 1 ? "day" : "days"} ago`;
    } else if (weeksDiff < 4) {
      return `${weeksDiff} ${weeksDiff === 1 ? "week" : "weeks"} ago`;
    } else {
      return `${monthsDiff} ${monthsDiff === 1 ? "month" : "months"} ago`;
    }
  };
  // console.log("Author is: ",author);
  return (
    <Card className="w-full max-w-3xl mb-4 p-4 mx-auto">
      <CardHeader>
        <div className="blog-header flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Avatar>
              <AvatarImage
                src={
                  author?.profilePic
                    ? author?.profilePic
                    : author?.gender?.toLowerCase() === "male"
                    ? DEFAULT_MALE_PIC
                    : DEFAULT_FEMALE_PIC
                }
              />
              <AvatarFallback>{author?.name?.charAt(0)}</AvatarFallback>
            </Avatar>
            <h1 className="text-lg font-semibold">{author?.name}</h1>
          </div>
          {author?.userId === loggedInUser?.userId && (
            <div className="flex items-center gap-2">
              <CreateBlogForm formType={"update"} blog={blog} />
              <AlertDialog>
                <AlertDialogTrigger className="border border-red-500 py-1 px-2">
                  <FontAwesomeIcon
                    icon={faTrash}
                    className=" text-red-500 text-lg"
                  />
                </AlertDialogTrigger>
                <AlertDialogContent>
                  <AlertDialogHeader>
                    <AlertDialogTitle>
                      Are you absolutely sure?
                    </AlertDialogTitle>
                    <AlertDialogDescription>
                      This action cannot be undone. This will permanently delete
                      your blog and remove your blog data from our servers.
                    </AlertDialogDescription>
                  </AlertDialogHeader>
                  <AlertDialogFooter>
                    <AlertDialogCancel className="border-2 border-orange-600 text-orange-600 hover:text-orange-800 hover:border-orange-400 hover:bg-orange-100">
                      Cancel
                    </AlertDialogCancel>
                    <AlertDialogAction
                      className="bg-orange-600 hover:bg-orange-500"
                      onClick={() => deleteBlog(blog?.blogId)}
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
        <div>
          <div className="p-4">
            <h1 className="font-semibold">
              {blog?.blogTitle}: {blog?.blogCaption}
            </h1>
            <p className="mt-5">{blog?.blogContent}</p>
          </div>

          {totalImages > 0 && (
            <div className="flex flex-col items-center justify-center">
              <ScrollArea className="overflow-x-auto max-w-96 shadow-xl">
                <div
                  ref={scrollContainerRef}
                  onScroll={handleScroll}
                  className="bg-gray-100 flex items-center justify-start flex-nowrap scroll-smooth w-full overflow-x-auto"
                  // style={{ scrollSnapType: "x mandatory" }}
                >
                  {blog?.blogImages?.map((item, index) => (
                    <div
                      key={index}
                      ref={(el) => (imageRefs.current[index] = el)}
                      className="w-96 flex-shrink-0 scroll-snap-align-start"
                    >
                      <img
                        src={item}
                        alt={`preview-${index}`}
                        className="w-full h-full object-cover"
                      />
                    </div>
                  ))}
                  <div className="w-96 overflow-hidden shadow-lg">
                    <img
                      src={blog.blogImages[currentImageIndex]}
                      alt={`blog-image-${currentImageIndex}`}
                      className="w-full h-full object-cover transition duration-700 ease-in-out"
                    />
                  </div>
                </div>
                <ScrollBar orientation="horizontal" />
              </ScrollArea>

              {/* Dots */}
              <div className="flex gap-2 mt-3">
                {blog.blogImages.map((_, index) => (
                  <div
                    key={index}
                    onClick={() => goToImage(index)}
                    className={`w-3 h-3 rounded-full cursor-pointer transition-all ${
                      index === currentImageIndex
                        ? "bg-orange-500 scale-110"
                        : "bg-gray-300"
                    }`}
                  />
                ))}
              </div>
            </div>
          )}
        </div>
      </CardContent>
      <CardFooter>
        <div className="w-full">
          <div className="flex items-center justify-between w-full">
            <div className="flex items-center gap-4">
              <div className="flex items-center gap-1">
                <FontAwesomeIcon
                  className={`${
                    blog?.blogLikes?.includes(loggedInUser?.userId)
                      ? "text-orange-500"
                      : "text-orange-300"
                  } hover:cursor-pointer`}
                  onClick={() =>
                    updateBlogLike(blog?.blogId, loggedInUser?.userId)
                  }
                  icon={faHeart}
                  size="xl"
                />
                <h1>{blog?.blogLikes?.length}</h1>
              </div>
              <div className="flex items-center gap-1">
                <BlogComments blog={blog} />
              </div>

              <FontAwesomeIcon
                className="text-orange-400 hover:cursor-pointer"
                icon={faShare}
                size="xl"
              />
            </div>
            <div className=" gap-2 items-center">
              <div className="flex items-center gap-1">
                <FontAwesomeIcon
                  className="text-orange-400 hover:cursor-pointer text-sm"
                  icon={faEye}
                />
                <h1 className="text-xs">
                  {blog?.blogViews?.length}{" "}
                  {blog?.blogViews?.length > 1 ? "views" : "view"}
                </h1>
              </div>
            </div>
          </div>
          <h1 className="font-light text-xs"> Posted {" "}
            {formatTimeAgo(blog?.postedDate)}
          </h1>
        </div>
      </CardFooter>
    </Card>
  );
};

export default BlogCard;
