/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Card } from "@/components/ui/card";
import {
  faArrowUp,
  faPaperPlane,
  faThumbsUp,
} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useState } from "react";
import {
  DEFAULT_FEMALE_PIC,
  DEFAULT_MALE_PIC,
} from "../../../Constants/constants";
import { Input } from "@/components/ui/input";
import { useRef } from "react";
import { shallowEqual, useSelector } from "react-redux";
import useComments from "../../../CustomHooks/useComments";
import TimeAgo from "./FormatCommentTime";

export const CommentsCard = ({ comment,blog}) => {
  console.log("Blog in comment card",blog);
  const [showReplies, setShowReplies] = useState(false);
  const [activeReply, setActiveReply] = useState(false);
  const replyRef = useRef(null);

  const { likeComment } = useComments();

  const replies = comment?.replies;
  const loggedInUser = useSelector((store) => store.auth.user, shallowEqual);
  
  const commentData = {
    blogId: comment?.blogId,
    authorId: loggedInUser?.userId,
    authorName: loggedInUser?.name,
    authorProfilePic: loggedInUser?.profilePic,
    authorGender: loggedInUser?.gender,
    parentCommentId: comment?.commentId,
    repliedToUserId: comment?.authorId,
  };
  const { postComment } = useComments();
  const handleSubmit = () => {
    const reply = replyRef.current.value;
    const finalData = { ...commentData, content: reply };
    postComment(finalData,blog?.blogAuthor);
    setActiveReply(!activeReply);
  };
  console.log("Current comment: ", comment);
  console.log("Replies: ", replies);
  return (
    <>
      <Card className="border-none shadow-none">
        <div className="flex items-center justify-between p-2 ">
          <div className="w-[80%] text-wrap flex  gap-2">
            <Avatar>
              <AvatarImage
                src={
                  comment?.authorProfilePic
                    ? comment?.authorProfilePic
                    : comment?.authorGender?.toLowerCase() === "male"
                    ? DEFAULT_MALE_PIC
                    : DEFAULT_FEMALE_PIC
                }
              />
              <AvatarFallback>{comment?.authorName?.charAt(0)}</AvatarFallback>
            </Avatar>
            <div>
              <div className="flex gap-4 items-center">
                <h1 className="font-semibold text-sm">{comment?.authorName}</h1>
                <TimeAgo timestamp={comment?.createdAt} />
              </div>
              <h1 className="text-sm">{comment?.content}</h1>
            </div>
          </div>

          <div className="flex items-center gap-2">
            {/* <div className=" border"> */}
              <Badge
                variant="outline"
                className="border-none hover:cursor-pointer"
                onClick={() => setActiveReply(!activeReply)}
              >
                Reply
              </Badge>
            {/* </div> */}
            <div className="flex items-center gap-2">
              {comment?.commentLikes?.length > 0 && (
                <h1 className="text-xs">{comment?.commentLikes?.length}</h1>
              )}

              <FontAwesomeIcon
                className={`${
                  comment?.commentLikes?.includes(loggedInUser?.userId)
                    ? "text-orange-600"
                    : "text-orange-300"
                } hover:cursor-pointer`}
                icon={faThumbsUp}
                size="sm"
                onClick={() =>
                  likeComment(comment?.commentId, loggedInUser?.userId)
                }
              />
            </div>
          </div>
        </div>
        {replies?.length > 0 && (
          <>
            <div className=" flex items-center justify-center">
              <div className="border border-orange-100 w-[20%]"></div>
              {!showReplies ? (
                <Badge
                  variant="outline"
                  className="border-none hover:cursor-pointer"
                  onClick={() => setShowReplies(!showReplies)}
                >
                  <h1 className="text-orange-300 text-xs font-semibold cursor-pointer">
                    {replies?.length}{" "}
                    {replies?.length > 1 ? "Replies" : "Reply"}
                  </h1>
                </Badge>
              ) : (
                <div className="px-4">
                  <FontAwesomeIcon
                    onClick={() => setShowReplies(!showReplies)}
                    className=" text-orange-300 hover:cursor-pointer"
                    icon={faArrowUp}
                    size="xs"
                  />
                </div>
              )}
              <div className="border border-orange-100 w-[20%]"></div>
            </div>
            {showReplies && (
              <div className="mx-8 border-dashed border-l border-orange-300">
                {replies?.map((item, index) => (
                  <div key={index} className="mb-2">
                    <CommentsCard comment={item} key={index} />
                  </div>
                ))}
              </div>
            )}
          </>
        )}
      </Card>
      <div>
        {activeReply && (
          <div className="flex items-center justify-center px-8 gap-2">
            <Input
              type="text"
              placeholder="drop your comment here..."
              className="border-orange-300"
              ref={replyRef}
            />

            <Badge
              variant="outline"
              className=" hover:cursor-pointer border-none"
              onClick={handleSubmit}
            >
              <FontAwesomeIcon
                className="text-orange-400 hover:cursor-pointer"
                icon={faPaperPlane}
                size="2xl"
              />
            </Badge>
          </div>
        )}
      </div>
    </>
  );
};
export default CommentsCard;
