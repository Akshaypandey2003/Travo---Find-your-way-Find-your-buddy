/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCommentDots, faPaperPlane } from "@fortawesome/free-solid-svg-icons";
import { ScrollArea } from "@/components/ui/scroll-area";
import CommentsCard from "./CommentsCard";
import { useEffect, useRef, useState, useCallback } from "react";
import { useSelector, shallowEqual, useDispatch } from "react-redux";
import useComments from "../../../CustomHooks/useComments";
import store from "../../../Redux/Store";
import { Skeleton } from "@/components/ui/skeleton";
import CommentsCardSkeleton from "./CommentsCardSkeleton";

export const BlogComments = ({ blog }) => {

  const comments = useSelector((store) => store.comment);
  const scrollRef = useRef(null);
  const commentRef = useRef(null);
  const loadMoreRef = useRef(null);
  const dispatch = useDispatch();
  const [fetchComments, setFetchComments] = useState(false);
  const [page, setPage] = useState(0);
  const [isFetchingMore, setIsFetchingMore] = useState(false);

  const { getComments, postComment } = useComments();
  const loggedInUser = useSelector((store) => store.auth.user);

  const allComments = useSelector(
    (store) => store.comment.comments,
    shallowEqual
  );
 
  
  const postComments = allComments?.filter(
    (item) => item?.parentCommentId === null && item?.blogId === blog?.blogId
  );

  const commentData = {
    blogId: blog?.blogId,
    authorId: loggedInUser?.userId,
    authorName: loggedInUser?.name,
    authorProfilePic: loggedInUser?.profilePic,
    authorGender: loggedInUser?.gender,
  };

  const handleSubmit = () => {
    const comment = commentRef.current?.value;
    const finalData = { ...commentData, content: comment };
    postComment(finalData,blog?.blogAuthor);
    commentRef.current.value = "";
  };
  const nextPageToken = useSelector((store) => store.comment.nextPageTokens[blog?.blogId]);
  
  const loadMoreComments = useCallback(async () => {
    if (isFetchingMore || nextPageToken) return;
    setIsFetchingMore(true);
    await getComments(blog?.blogId, page + 1);

    // Assuming your `getComments` supports pagination
    setPage((prev) => prev + 1);
    setIsFetchingMore(false);
  }, [page, blog?.blogId, getComments, isFetchingMore]);

  useEffect(() => {
    if (fetchComments) {
      setPage(0);
      getComments(blog?.blogId, 0); // Initial load
      setFetchComments(false);
    }
  }, [fetchComments, blog?.blogId, getComments]);

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          loadMoreComments();
        }
      },
      {
        root: scrollRef.current?.querySelector(
          "[data-radix-scroll-area-viewport]"
        ),
        threshold: 1.0,
      }
    );
    const loadMoreDiv = loadMoreRef.current;
    if (loadMoreDiv) observer.observe(loadMoreDiv);

    return () => {
      if (loadMoreDiv) observer.unobserve(loadMoreDiv);
    };
  }, [loadMoreComments, postComments]);
  return (
    <div className="">
      <Dialog modal={true}>
        <DialogTrigger
          asChild
          className="py-1 px-2 font-semibold hover:text-orange-500"
        >
          <div className="flex items-center gap-1">
            <FontAwesomeIcon
              className="text-orange-400 hover:cursor-pointer"
              icon={faCommentDots}
              size="xl"
              onClick={() => setFetchComments(true)}
            />
            {postComments?.length > 0 && (
              <div>
                <h1 className="font-normal">{postComments?.length}</h1>
              </div>
            )}
          </div>
        </DialogTrigger>

        <DialogContent className="sm:max-w-[550px]">
          <div className="px-5">
            <ScrollArea
              className="max-h-96 overflow-y-auto scrollbar-hide rounded-md " 
              ref={scrollRef}
            >
              <DialogHeader className="">
                <DialogTitle>Comments</DialogTitle>
                <DialogDescription>
                  See how people are interacting with your blog.
                </DialogDescription>
              </DialogHeader>

              <div className="py-4">
                {!postComments &&
                  [1, 2, 3, 4, 5].map((item, index) => (
                    <div key={index} className="mb-2">
                      <CommentsCardSkeleton />
                    </div>
                  ))}
                {postComments?.length === 0 ? (
                  <h1 className="text-center text-sm text-gray-500">
                    No comments yet.
                  </h1>
                ) : (
                  postComments.map((item, index) => (
                    <div key={item.commentId || index} className="mb-2">
                      <CommentsCard comment={item} blog={blog} />
                    </div>
                  ))
                )}
                <div ref={loadMoreRef} className="h-10">
                  {isFetchingMore && !nextPageToken
                    ? [1, 2, 3, 4].map((item, index) => (
                        <div key={index} className="mb-2">
                          <CommentsCardSkeleton />
                        </div>
                      ))
                    : nextPageToken &&
                      postComments?.length > 0 && (
                        <div className="text-center">
                          <h1 className="text-sm text-gray-400">
                            No more comments
                          </h1>
                        </div>
                      )}
                </div>
              </div>
            </ScrollArea>
          </div>

          <div className="flex items-center justify-center px-8 gap-2">
            <Input
              type="text"
              placeholder="drop your comment here..."
              className="border-orange-300"
              ref={commentRef}
            />
            <Badge
              variant="outline"
              className="hover:cursor-pointer border-none"
              onClick={handleSubmit}
            >
              <FontAwesomeIcon
                className="text-orange-400 hover:cursor-pointer"
                icon={faPaperPlane}
                size="2xl"
              />
            </Badge>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default BlogComments;
