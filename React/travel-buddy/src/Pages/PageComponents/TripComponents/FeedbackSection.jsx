/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
import { useState } from "react";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { ScrollArea } from "@/components/ui/scroll-area";
import { ChevronDown, ChevronUp, Star } from "lucide-react";
import TimeAgo from "../BlogComponents/FormatCommentTime";
import { useSelector } from "react-redux";

const FeedbackSection = ({ feedbacks = [] }) => {
  const [showMore, setShowMore] = useState(false);
  const theme = useSelector((state) => state.auth.theme);
//   const visibleFeedbacks = showMore ? feedbacks : feedbacks.slice(0, 3);

 return (
    <div className="mt-6">
      <h2 className={`text-lg font-semibold mb-3 ${theme==="dark" ? "text-orange-400" : "text-orange-700"}`}>
        Trip Feedback
      </h2>

      {feedbacks?.length > 0 ? (
        <ScrollArea className="h-96 pr-2">
          <div className="space-y-4">
            {feedbacks.map((fb, index) => (
              <div
                key={index}
                className={`rounded-xl p-4 border transition shadow-md hover:shadow-lg ${
                  theme==="dark"
                    ? "bg-zinc-900 border-zinc-700 text-gray-200"
                    : "bg-white border-orange-100 text-gray-800"
                }`}
              >
                <div className="flex justify-between items-center mb-2">
                  <div className="flex items-center gap-3">
                    <Avatar className="h-8 w-8">
                      <AvatarImage src={fb?.author?.profilePic || ""} />
                      <AvatarFallback>
                        {fb?.author?.name?.charAt(0)}
                      </AvatarFallback>
                    </Avatar>
                    <span className={`font-medium ${theme==="dark" ? "text-gray-200" : "text-gray-700"}`}>
                      {fb?.author?.name}
                    </span>
                  </div>
                  <TimeAgo timestamp={fb?.createdAt} />
                </div>

                <p className={`text-sm ${theme==="dark" ? "text-gray-300" : "text-gray-800"}`}>
                  {fb?.comment}
                </p>

                {fb?.rating > 0 && (
                  <div className="mt-2 flex items-center gap-1 text-yellow-500 text-sm">
                    {Array.from({ length: fb.rating }).map((_, i) => (
                      <Star
                        key={i}
                        className="h-4 w-4 fill-yellow-400 stroke-yellow-400"
                      />
                    ))}
                  </div>
                )}

                {fb?.tags?.length > 0 && (
                  <div className="mt-3 flex flex-wrap gap-2">
                    {fb.tags.map((tag, i) => (
                      <Badge
                        key={i}
                        className={`text-xs border ${
                         theme==="dark"
                            ? "bg-zinc-800 text-orange-400 border-orange-400"
                            : "bg-orange-100 text-orange-600 border-orange-300"
                        }`}
                      >
                        #{tag}
                      </Badge>
                    ))}
                  </div>
                )}
              </div>
            ))}
          </div>
        </ScrollArea>
      ) : (
        <p className={`text-sm ${theme==="dark" ? "text-gray-400" : "text-gray-500"}`}>
          No feedback yet.
        </p>
      )}

      {/* Uncomment this section if you want to toggle show more/less */}
      {/* {feedbacks.length > 3 && (
        <div className="mt-3 text-right">
          <button
            onClick={() => setShowMore(!showMore)}
            className={`text-sm font-medium inline-flex items-center hover:underline ${
              isDark ? "text-orange-400" : "text-orange-600"
            }`}
          >
            {showMore ? (
              <>
                View Less <ChevronUp className="h-4 w-4 ml-1" />
              </>
            ) : (
              <>
                View More <ChevronDown className="h-4 w-4 ml-1" />
              </>
            )}
          </button>
        </div>
      )} */}
    </div>
  );
};
export default FeedbackSection;
