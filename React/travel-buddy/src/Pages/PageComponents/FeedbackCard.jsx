/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
import React from "react";
import { Card } from "@/components/ui/card";
import { DEFAULT_FEMALE_PIC, DEFAULT_MALE_PIC } from "../../Constants/constants";
import { useSelector } from "react-redux";

const FeedbackCard = ({ feedback }) => {
  const theme = useSelector((store) => store.auth.theme);

  const profilePic =
    feedback?.userImg ||
    (feedback?.gender?.toLowerCase() === "male"
      ? DEFAULT_MALE_PIC
      : DEFAULT_FEMALE_PIC);

  const isDark = theme === "dark";

  return (
    <Card
      className={`w-[22rem] min-h-[14rem] p-6 rounded-2xl shadow-md transition duration-300 ease-in-out
        border ${isDark ? "bg-black border-gray-900 hover:shadow-xl" : "bg-white border-orange-100 hover:shadow-lg"}`}
    >
      {/* Header */}
      <div className="flex items-center gap-4 mb-4">
        <div className="w-14 h-14 rounded-full overflow-hidden border-2 border-orange-400 shadow-sm">
          <img
            src={profilePic}
            alt={feedback?.name}
            className="object-cover w-full h-full"
          />
        </div>
        <div className="min-w-0">
          <h3
            className={`font-semibold text-lg truncate ${
              isDark ? "text-orange-400" : "text-orange-700"
            }`}
          >
            {feedback?.name}
          </h3>
          {feedback?.createdAt && (
            <p
              className={`text-xs ${
                isDark ? "text-gray-500" : "text-gray-400"
              }`}
            >
              {new Date(feedback.createdAt).toLocaleDateString()}
            </p>
          )}
        </div>
      </div>

      {/* Feedback */}
      <p
        className={`text-base italic leading-relaxed break-words whitespace-normal ${
          isDark ? "text-gray-300" : "text-gray-600"
        }`}
      >
        “{feedback?.comment || "No feedback provided."}”
      </p>

      {/* Rating (optional) */}
      {feedback?.rating && (
        <div className="mt-3 flex gap-1">
          {[...Array(5)].map((_, i) => (
            <span
              key={i}
              className={`text-lg ${
                i < feedback.rating
                  ? "text-yellow-400"
                  : isDark
                  ? "text-gray-600"
                  : "text-gray-300"
              }`}
            >
              ★
            </span>
          ))}
        </div>
      )}
    </Card>
  );
};

export default FeedbackCard;
