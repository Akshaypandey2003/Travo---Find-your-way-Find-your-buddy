/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";
import {
  DEFAULT_FEMALE_PIC,
  DEFAULT_MALE_PIC,
} from "../../../Constants/constants";
import { useSelector } from "react-redux";
import { useEffect, useState, useMemo } from "react";
import useUserData from "../../../CustomHooks/useUserData";
import useChat from "../../../CustomHooks/useChat";

export const Chat = ({ chat }) => {
  const loggedInUser = useSelector((store) => store.auth.user);
  const usersList = useSelector((store) => store.auth.usersList);
  const { getUser } = useUserData();
  const { fetchMessages } = useChat();
  const messages = useSelector((store) => store.chat.messages);

  const currMessage = chat?.chatId ? messages[chat?.chatId] : [];
  const unseenMessages = currMessage?.filter(
    (message) => !message?.read && message?.senderId !== loggedInUser?.userId
  );

  const chatUserId = useMemo(() => {
    return chat?.participants?.find((id) => id !== loggedInUser?.userId);
  }, [chat, loggedInUser]);

  const localUser = useMemo(() => {
    return usersList?.find((user) => user?.userId === chatUserId);
  }, [chatUserId, usersList]);

  const [chatUser, setChatUser] = useState(null);

  useEffect(() => {
    const fetchUser = async () => {
    if (!chatUserId) return;

    if (localUser) {
      setChatUser(localUser);
    } else {
      try {
        const user = await getUser(chatUserId);
        setChatUser(user);
      } catch (err) {
        console.error("Failed to fetch chat user", err);
        setChatUser(null);
      }
    }
  };

  fetchUser();
  }, [chatUserId]);

  useEffect(() => {
    fetchMessages(chat?.chatId);
  }, [chat?.chatId]);

  // 🕓 Get last message sent/received
  const lastMessage = useMemo(() => {
    if (!currMessage || currMessage.length === 0) return null;
    return currMessage[currMessage.length - 1]; // assuming messages are sorted by time
  }, [currMessage]);

  // ⏱️ Convert timestamp to "x min ago" style
  const getTimeAgo = (timestamp) => {
    if (!timestamp) return "";
    const now = new Date();
    const time = new Date(timestamp);
    const diff = Math.floor((now - time) / 1000); // in seconds

    if (diff < 60) return `${diff} sec ago`;
    if (diff < 3600) return `${Math.floor(diff / 60)} min ago`;
    if (diff < 86400) return `${Math.floor(diff / 3600)} hour${Math.floor(diff / 3600) > 1 ? "s" : ""} ago`;
    return `${Math.floor(diff / 86400)} day${Math.floor(diff / 86400) > 1 ? "s" : ""} ago`;
  };

  return (
    <Card className="border-none shadow-none p-2">
      <div className="text-wrap flex gap-2">
        <Avatar>
          <AvatarImage
            src={
              chat?.groupImageUrl || chatUser?.profilePic ||
              (chatUser?.gender?.toLowerCase() === "male"
                ? DEFAULT_MALE_PIC
                : DEFAULT_FEMALE_PIC)
            }
          />
          <AvatarFallback>
            {chat?.groupName?.charAt(0).toUpperCase() || chatUser?.name?.charAt(0).toUpperCase()}
          </AvatarFallback>
        </Avatar>
        <div className="w-full">
          <div className="flex items-center justify-between w-full">
            <h1 className="font-semibold text-sm">
              {chat?.groupName || chatUser?.name}
            </h1>
            {unseenMessages?.length > 0 && (
              <div>
                <Badge className="rounded-xl bg-orange-700 py-0 px-1">
                  <span className="text-xs">{unseenMessages.length}</span>
                </Badge>
              </div>
            )}
          </div>
          <h1 className="text-xs text-gray-500">
            {lastMessage?.createdAt ? getTimeAgo(lastMessage.createdAt) : chat?.groupChat ? chat?.groupDescription : chatUser?.bio || "Start a conversation..."}
          </h1>
        </div>
      </div>
    </Card>
  );
};

export default Chat;
