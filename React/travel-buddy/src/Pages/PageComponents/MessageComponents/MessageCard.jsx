/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
import { useSelector } from "react-redux";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCheckDouble } from "@fortawesome/free-solid-svg-icons";
import { useEffect, useState } from "react";
import useChat from "../../../CustomHooks/useChat";
import useUserData from "../../../CustomHooks/useUserData";

export const MessageCard = ({ message, isGroup }) => {
  // console.log("MessageCard rendered with message: ", message);

  const { updateReadStatus } = useChat();
  const { getUser } = useUserData();
  const loggedInUser = useSelector((store) => store.auth.user);
  const usersList = useSelector((store) => store.auth.usersList);

  const [sender, setSender] = useState(null);

  // Update read status
  useEffect(() => {
    if (message?.senderId !== loggedInUser.userId && !message?.read) {
      updateReadStatus({
        messageId: message?.messageId,
        chatId: message?.chatId,
      });
    }
  }, [message?.messageId]);

  // Fetch sender info if needed
  useEffect(() => {
    if (!message?.senderId) return;

    const localUser = usersList.find(
      (user) => user.userId === message.senderId
    );

    if (localUser) {
      setSender(localUser);
    } else {
      const user = getUser(message.senderId); // expected to return from cache or fetch
      if (user && typeof user.then === "function") {
        // it's a Promise
        user.then(setSender);
      } else {
        setSender(user);
      }
    }
  }, [message?.senderId]);

  return (
    <div
      className={`px-2 py-1 rounded-lg max-w-[75%] ${
        message.senderId === loggedInUser.userId
          ? "bg-green-400 self-end"
          : "bg-gray-400 self-start"
      }`}
    >
      {isGroup && message?.senderId !== loggedInUser.userId && (
        <h1 className=" font-semibold text-orange-800">
          {sender?.name}
        </h1>
      )}

      <p className="break-words">{message.messageContent}</p>

      <div className="flex items-center justify-end gap-2">
        <span className="text-xs text-gray-100">
          {new Date(message.createdAt).toLocaleTimeString([], {
            hour: "2-digit",
            minute: "2-digit",
            hour12: true,
          })}
        </span>
        {message?.senderId === loggedInUser.userId && (
          <FontAwesomeIcon
            className={`${
              message?.read ? "text-orange-500" : "text-gray-200"
            }`}
            icon={faCheckDouble}
            size="xs"
          />
        )}
      </div>
    </div>
  );
};

export default MessageCard;
