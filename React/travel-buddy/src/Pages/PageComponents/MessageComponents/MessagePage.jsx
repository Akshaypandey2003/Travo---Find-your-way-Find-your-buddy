/* eslint-disable no-unused-vars */
import {
  ResizableHandle,
  ResizablePanel,
  ResizablePanelGroup,
} from "@/components/ui/resizable";
import Chat from "./Chat";
import SuggestedUserCard from "./SuggestedUserCard";
import { useEffect, useMemo, useRef, useState } from "react";
import useChat from "../../../CustomHooks/useChat";
import { useSelector, useDispatch } from "react-redux";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { ScrollArea } from "@/components/ui/scroll-area";

import {
  Card,
  CardHeader,
  CardContent,
  CardFooter,
} from "@/components/ui/card";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faComment,
  faCommentAlt,
  faMagnifyingGlass,
  faPaperPlane,
} from "@fortawesome/free-solid-svg-icons";
import useUserData from "../../../CustomHooks/useUserData";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";
import {
  DEFAULT_FEMALE_PIC,
  DEFAULT_MALE_PIC,
} from "../../../Constants/constants";
import { setActiveChat } from "../../../Redux/Slices/chatSlice";
import MessageCard from "./MessageCard";
import CreateGroupComponent from "./CreateGroupComponent";
import SideSheet from "../SideSheet";
import DropDown from "./DropDown";

export const MessagePage = () => {
  const { fetchChats, sendMessage, startChat } = useChat();
  const { getGroupParticipantsData } = useUserData();
  const dispatch = useDispatch();
  const chats = useSelector((store) => store.chat.chats);
  const activeChat = useSelector((store) => store.chat.activeChat);
  const messages = useSelector((store) => store.chat.messages);

  const [chatUser, setChatUser] = useState(null);
  let currMessage = activeChat?.chatId ? messages[activeChat?.chatId] : [];
  const allUsers = useSelector((store) => store.auth.usersList);
  const loggedInUser = useSelector((store) => store.auth.user);
  const { getUser } = useUserData();
  const messageRef = useRef();
  const [searchQuery, setSearchQuery] = useState("");
  const [suggestedUsers, setSuggestedUsers] = useState([]);
  const [participants, setParticipants] = useState([]);
  const usersList = useSelector((store) => store.auth.usersList);
  const messagesEndRef = useRef(null);

  // Derive other participant's userId
  // Only extract chatUserId if:
  // 1. activeChat has a chatId (i.e., it's an actual chat)
  // 2. It is NOT a group chat
  const chatUserId = useMemo(() => {
    if (!activeChat?.chatId || activeChat?.groupChat) return null;

    return activeChat?.participants?.find((id) => id !== loggedInUser?.userId);
  }, [activeChat, loggedInUser]);

  // Now only try to fetch the user if chatUserId is available
  const localUser = useMemo(() => {
    return chatUserId ? usersList?.find((user) => user?.userId === chatUserId):null;
  }, [chatUserId, usersList]);

  const handleSendMessage = async () => {
    const message = messageRef?.current?.value;
    const messageData = {
      chatId: activeChat?.chatId,
      senderId: loggedInUser?.userId,
      messageType: "text",
      messageContent: message,
    };

    const chatId = activeChat?.chatId;
    if (chatId) {
      // console.log("Sending message for chat: ", chatId);
      await sendMessage({ message: messageData });
      // console.log("Message sent successfylly for chat: ", chatId);
    } else {
      // console.log("Initiating chat for user: ", activeChat?.userId);
      await startChat({ message: messageData, receiver: activeChat?.userId });
      // console.log("Chat initiated successfully for user: ", activeChat?.userId);
    }
    messageRef.current.value = "";
  };
  console.log("Active chat is: ", activeChat);
  // console.log("Group participants are: ", participants);
  const handleUserSelect = (chat) => {
    dispatch(setActiveChat(chat));
    setSearchQuery("");
    setSuggestedUsers([]);
  };
  console.log("Chat user is : ", chatUser);
  console.log("Chat user ID is : ", chatUserId);

  
  useEffect(() => {
  // If it's a group chat → skip
  if (activeChat?.groupChat) return;

  // If it's an existing chat
  if (chatUserId) {
    if (localUser) {
      setChatUser(localUser);
    } else {
      const fetchUser = async () => {
        const user = await getUser(chatUserId);
        setChatUser(user);
      };
      fetchUser();
    }
  } 
  // If it's a new chat (no chatId), just set the activeChat as chatUser
  else if (!activeChat?.chatId && activeChat?.userId) {
    setChatUser(activeChat);
  }
}, [activeChat, chatUserId, localUser]);

  useEffect(() => {
    if (chats == null || chats.length === 0) fetchChats(loggedInUser?.userId);
  }, [loggedInUser]);

  const handleSearch = async (e) => {
    const query = e.target.value.toLowerCase();
    setSearchQuery(query);

    const mutualUserIds = loggedInUser?.followers?.filter((id) =>
      loggedInUser?.following?.includes(id)
    );
    const matchingUsers = [];
    for (let id of mutualUserIds) {
      let user = allUsers.find((user) => user.userId === id);

      if (!user) {
        user = await getUser(id);
      }

      if (user?.name?.toLowerCase().includes(query)) {
        matchingUsers.push(user);
      }
    }
    setSuggestedUsers(matchingUsers);
  };
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [currMessage]);

  //Fetching group participants data
  useEffect(() => {
    if (!activeChat?.groupChat || !activeChat?.participants?.length) return;

    const fetchParticipants = async () => {
      const data = await getGroupParticipantsData(activeChat.participants);
      setParticipants(data);
    };

    fetchParticipants();
  }, [activeChat]); // ✅ only reruns when chatId changes

  return (
    <div className=" h-[86vh] w-[95%] m-auto">
      <ResizablePanelGroup
        direction="horizontal"
        className="min-h-[200px] rounded-lg md:min-w-[450px]"
      >
        <ResizablePanel minSize={25} maxSize={35} className="border border-r-0">
          <div className="text-right px-4 py-1">
            <CreateGroupComponent />
          </div>
          <div className="flex items-center gap-2 px-2">
            <Input
              placeholder="search friends..."
              onChange={handleSearch}
              value={searchQuery}
            />
            <FontAwesomeIcon icon={faMagnifyingGlass} className="" />
          </div>
          <ScrollArea className="h-[90%]">
            {searchQuery.length > 0 && suggestedUsers.length > 0 ? (
              suggestedUsers.map((user) => (
                <div key={user.userId} onClick={() => handleUserSelect(user)}>
                  <SuggestedUserCard user={user} />
                </div>
              ))
            ) : searchQuery.length > 0 && suggestedUsers.length === 0 ? (
              <div className="p-4 text-center">
                <h1>No friends found with this name !!</h1>
              </div>
            ) : chats && chats.length > 0 ? (
              chats.map((chat, index) => (
                <div
                  key={index}
                  className="p-2 rounded-lg"
                  onClick={() => handleUserSelect(chat)}
                >
                  <Chat chat={chat} />
                </div>
              ))
            ) : (
              <div className="p-4 text-center">
                <h1>No chats yet !!</h1>
              </div>
            )}
          </ScrollArea>
        </ResizablePanel>

        <ResizableHandle withHandle />

        <ResizablePanel defaultSize={75} className="">
          {Object.keys(activeChat).length > 0 ? (
            <Card className="h-full rounded-none p-0">
              <CardHeader className="border-b p-2">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-4">
                    {!activeChat?.groupChat ? (
                      <Avatar>
                        <AvatarImage
                          src={
                            activeChat?.groupImageUrl ||
                            chatUser?.profilePic ||
                            (chatUser?.gender?.toLowerCase() === "male"
                              ? DEFAULT_MALE_PIC
                              : DEFAULT_FEMALE_PIC)
                          }
                        />
                        <AvatarFallback>
                          {activeChat?.groupName?.charAt(0).toUpperCase() ||
                            chatUser?.name?.charAt(0).toUpperCase()}
                        </AvatarFallback>
                      </Avatar>
                    ) : (
                      <div className="*:data-[slot=avatar]:ring-background flex items-center -space-x-5 *:data-[slot=avatar]:ring-2 *:data-[slot=avatar]:grayscale">
                        {participants?.slice(0, 3).map((item, index) => (
                          <Avatar key={index}>
                            <AvatarImage src={item?.profilePic} alt="@shadcn" />
                            <AvatarFallback>
                              {item?.name?.charAt(0).toUpperCase()}
                            </AvatarFallback>
                          </Avatar>
                        ))}
                        {participants?.length > 3 && (
                          <Badge
                            className="z-50 bg-orange-600 h-6 w-6 p-0 rounded-full font-semibold hover:bg-orange-600"
                            size=""
                          >
                            {"+"} {participants.length - 3}
                          </Badge>
                        )}
                      </div>
                    )}

                    <div className="">
                      <h1 className="font-semibold">
                        {activeChat?.groupName || chatUser?.name}
                      </h1>
                      {!activeChat?.groupChat ? (
                        <h1 className="text-[12px] mt-1">
                          Last seen 1 hour ago
                        </h1>
                      ) : (
                        <div className="flex items-center w-[35rem] gap-2 overflow-hidden whitespace-nowrap">
                          {participants?.slice(0, 5).map((item, index) => (
                            <h1
                              key={index}
                              className="text-[12px] mt-1 inline-block"
                            >
                              {item?.name}
                              {index < participants?.length - 1 && ","}
                            </h1>
                          ))}
                          <span className="ml-1">...</span>
                        </div>
                      )}
                    </div>
                  </div>
                  {/* {activeChat?.groupChat && ( */}
                    <div className="p-2">
                      <DropDown chatUser={chatUser} participants={participants} />
                    </div>
                  {/* )} */}
                </div>
              </CardHeader>
              <CardContent className="h-[80%] flex items-center justify-center p-0">
                <ScrollArea className="h-[100%] w-full">
                  <div className="w-full">
                    {currMessage && currMessage.length > 0 ? (
                      <div className="flex flex-col justify-end  gap-2 p-4">
                        {currMessage.map((message, index) => (
                          <MessageCard
                            key={index}
                            message={message}
                            isGroup={activeChat?.groupChat}
                          />
                        ))}
                        <div ref={messagesEndRef} />
                      </div>
                    ) : (
                      <div className="h-[30rem] flex justify-center items-center">
                        <div className="">
                          <Avatar className="h-32 w-32 m-auto">
                            <AvatarImage
                              src={
                                activeChat?.groupImageUrl ||
                                chatUser?.profilePic ||
                                (chatUser?.gender?.toLowerCase() === "male"
                                  ? DEFAULT_MALE_PIC
                                  : DEFAULT_FEMALE_PIC)
                              }
                            />
                            <AvatarFallback>
                              {activeChat?.groupName?.charAt(0).toUpperCase() ||
                                chatUser?.name.charAt(0).toUpperCase()}
                            </AvatarFallback>
                          </Avatar>
                          <h1 className="font-bold">
                            Start a conversation.........
                          </h1>
                        </div>
                      </div>
                    )}
                  </div>
                </ScrollArea>
              </CardContent>
              <CardFooter className="px-4 py-2 flex gap-2">
                <Input
                  placeholder="Type your message here..."
                  className=""
                  ref={messageRef}
                />
                <Badge
                  variant="outline"
                  className="hover:cursor-pointer border-none"
                  onClick={handleSendMessage}
                >
                  <FontAwesomeIcon
                    className="text-orange-400 hover:cursor-pointer"
                    icon={faPaperPlane}
                    size="2xl"
                  />
                </Badge>
              </CardFooter>
            </Card>
          ) : (
            <div className="border h-full flex flex-col items-center justify-center">
              <FontAwesomeIcon
                icon={faComment}
                size="4x"
                className="text-orange-200"
              />
              <div className="w-96 text-center">
                <h1>
                  <strong>
                    {" "}
                    A space to talk, share, and build connections.{" "}
                  </strong>
                  Create group chats, discover new people, and keep every
                  conversation alive in real time
                </h1>
              </div>
            </div>
          )}
        </ResizablePanel>
      </ResizablePanelGroup>
    </div>
  );
};
export default MessagePage;
