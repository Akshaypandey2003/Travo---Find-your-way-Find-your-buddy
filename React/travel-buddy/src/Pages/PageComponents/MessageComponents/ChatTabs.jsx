/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
} from "@/components/ui/card";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuRadioGroup,
  DropdownMenuRadioItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Input } from "@/components/ui/input";
import { Checkbox } from "@/components/ui/checkbox";
import { ScrollArea, ScrollBar } from "@/components/ui/scroll-area";
import { Switch } from "@/components/ui/switch";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";

import {
  faBellSlash,
  faChevronDown,
  faCircleInfo,
  faEdit,
  faMagnifyingGlass,
  faSignOutAlt,
  faStar,
  faUserGroup,
  faUserMinus,
  faUserPlus,
} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useEffect, useMemo, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  DEFAULT_FEMALE_PIC,
  DEFAULT_MALE_PIC,
} from "../../../Constants/constants";
import useChat from "../../../CustomHooks/useChat";
import { setActiveChat } from "../../../Redux/Slices/chatSlice";
import UserCard from "./UserCard";
import useUserData from "../../../CustomHooks/useUserData";

export const ChatTabs = ({ chatUser, participants }) => {
  const dispatch = useDispatch();
  const activeChat = useSelector((store) => store.chat.activeChat);
  const loggedInUser = useSelector((store) => store.auth.user);
  const usersList = useSelector((store) => store.auth.usersList);
  const [searchQuery, setSearchQuery] = useState("");
  const [suggestedUsers, setSuggestedUsers] = useState([]);
  const [allFriends, setAllFriends] = useState([]);
  const [newMembers, setNewMembers] = useState([]);
  const [removingMembers, setRemovingMembers] = useState([]);
  const [disappearingMessages, setDisappearingMessages] = useState(false);
  const { extractAllFriends } = useUserData();

  console.log("All friends in ChatTabs: ", allFriends);
  const remainingFriends = allFriends?.filter(
    (friend) =>
      friend?.userId !== loggedInUser?.userId &&
      !participants?.some((user) => user?.userId === friend?.userId)
  );
  // console.log("Remaining friends in ChatTabs: ", remainingFriends);

  const handleFriendsSearch = async (e) => {
    const query = e.target.value.toLowerCase();
    setSearchQuery(query);
    const matchingUsers = [];

    remainingFriends.forEach((user) => {
      // Check if the user name includes the search query
      if (user?.name?.toLowerCase().includes(query)) {
        matchingUsers.push(user);
      }
    });
    setSuggestedUsers(matchingUsers);
  };

  // console.log("chatUser in ChatTabs: ", chatUser);
  const handleSearch = async (e) => {
    const query = e.target.value.toLowerCase();
    setSearchQuery(query);
    const matchingUsers = [];

    participants.forEach((user) => {
      // Check if the user name includes the search query
      if (user?.name?.toLowerCase().includes(query)) {
        matchingUsers.push(user);
      }
    });
    setSuggestedUsers(matchingUsers);
  };
  const handleUserSelect = (chat) => {
    dispatch(setActiveChat(chat));
    setSearchQuery("");
    setSuggestedUsers([]);
  };
  const [duration, setDuration] = useState("");
  const { updateFavorite, updateGroupMembers } = useChat();

  useEffect(() => {
    const fetchFriends = async () => {
      const friends = await extractAllFriends();
      setAllFriends(friends);
    };

    fetchFriends();
  }, []);

  return (
    <div className="flex">
      <Tabs
        defaultValue="overview"
        className="flex gap-4 min-h-96 w-full items-start self-start h-full border"
      >
        {/* Left-side vertical tab list */}
        <TabsList className="flex flex-col h-full gap-2 w-48 self-start items-start bg-white ">
          <TabsTrigger
            value="overview"
            className="text-left text-black gap-2  flex items-center "
          >
            <FontAwesomeIcon icon={faCircleInfo} />
            Overview
          </TabsTrigger>
          {activeChat?.groupChat && (
            <TabsTrigger
              value="members"
              className="text-left text-black flex gap-2 items-center"
            >
              <FontAwesomeIcon icon={faUserGroup} />
              Members
            </TabsTrigger>
          )}
          {activeChat?.groupChat &&
            activeChat?.groupAdmin?.includes(loggedInUser?.userId) && (
              <TabsTrigger
                value="add-members"
                className="text-left text-black flex gap-2 items-center"
              >
                <FontAwesomeIcon icon={faUserPlus} />
                Add Members
              </TabsTrigger>
            )}
        </TabsList>
        {/* Right-side content */}
        <div className="flex-1 border">
          <TabsContent value="overview" className="">
            <ScrollArea className="h-96 scrollbar-hide">
              <div className=" flex flex-col ">
                <Card className="p-0 min-h-96 shadow-none px-5 border-none">
                  <CardHeader className="items-center">
                    <div
                      className={
                        "border-4 border-orange-600 bg-orange-200 h-[7rem] w-[7rem] rounded-full object-cover overflow-hidden m-auto mb-2"
                      }
                    >
                      <img
                        src={
                          activeChat?.groupImageUrl ||
                          chatUser?.profilePic ||
                          (chatUser?.gender?.toLowerCase() === "male"
                            ? DEFAULT_MALE_PIC
                            : DEFAULT_FEMALE_PIC)
                        }
                        alt="profile"
                      />
                    </div>
                    <div className="flex gap-2">
                      {activeChat?.groupAdmin?.includes(
                        loggedInUser?.userId
                      ) && (
                        <Input
                          type="text"
                          className="border-0 border-b-2 border-b-orange-400 focus:outline-none focus:ring-0"
                          placeholder={
                            activeChat?.groupDescription ||
                            "Description here..."
                          }
                        />
                      )}
                      {activeChat?.groupAdmin?.includes(
                        loggedInUser?.userId
                      ) && (
                        <Badge className="bg-white hover:bg-white hover:cursor-pointer border-none ">
                          <FontAwesomeIcon
                            icon={faEdit}
                            size="lg"
                            className="text-orange-400"
                          />
                        </Badge>
                      )}
                    </div>
                  </CardHeader>
                  <CardContent className="flex flex-col gap-4">
                    {!activeChat?.groupChat && (
                      <>
                        <div className="">
                          <h1 className="font-semibold">About</h1>
                          <h1>
                            {
                              <h1 className="text-sm">
                                {chatUser?.bio || "No bio available"}
                              </h1>
                            }
                          </h1>
                        </div>
                        <div className="">
                          <h1 className="font-semibold">Email</h1>
                          <h1>
                            {
                              <h1 className="text-sm">
                                {chatUser?.email || "No bio available"}
                              </h1>
                            }
                          </h1>
                        </div>
                      </>
                    )}
                    {activeChat?.groupChat && (
                      <div className="">
                        <h1 className="font-semibold">Created</h1>
                        <h1>
                          {
                            <h1 className="text-sm">
                              {new Date(activeChat?.createdAt).toLocaleString()}
                            </h1>
                          }
                        </h1>
                      </div>
                    )}
                    {!activeChat?.groupAdmin?.includes(loggedInUser?.userId) &&
                      activeChat?.groupDescription && (
                        <div>
                          <h1 className="font-semibold">Description</h1>
                          <h1>
                            {
                              <h1 className="text-sm">
                                {activeChat?.groupDescription}
                              </h1>
                            }
                          </h1>
                        </div>
                      )}
                    <div>
                      <h1 className="font-semibold">Disappearing messages</h1>
                      <Switch
                        className="mt-1"
                        id="trip-mode"
                        checked={disappearingMessages}
                        onCheckedChange={() => {
                          setDisappearingMessages(!disappearingMessages);
                        }}
                      />
                    </div>
                    <div>
                      <h1 className="font-semibold">Mute Notifications</h1>
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <div className="flex items-center hover:cursor-pointer">
                            <div className="flex gap-2 border-2 rounded-md p-1 px-4">
                              <FontAwesomeIcon icon={faBellSlash} />
                              <FontAwesomeIcon icon={faChevronDown} />
                            </div>
                          </div>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent className="w-56">
                          <DropdownMenuSeparator />
                          <DropdownMenuRadioGroup
                            value={duration}
                            onValueChange={setDuration}
                          >
                            <DropdownMenuRadioItem value="1 day">
                              1 day
                            </DropdownMenuRadioItem>
                            <DropdownMenuRadioItem value="1 week">
                              1 week
                            </DropdownMenuRadioItem>
                            <DropdownMenuRadioItem value="always">
                              always
                            </DropdownMenuRadioItem>
                          </DropdownMenuRadioGroup>
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </div>
                  </CardContent>
                  <CardFooter className="my-5">
                    <div className="flex gap-4">
                      <FontAwesomeIcon
                        icon={faStar}
                        size="lg"
                        className={
                          activeChat?.favoriteBy?.includes(loggedInUser?.userId)
                            ? "text-orange-600 hover:cursor-pointer"
                            : "text-orange-300 hover:cursor-pointer"
                        }
                        onClick={() =>
                          updateFavorite(
                            activeChat?.chatId,
                            loggedInUser?.userId
                          )
                        }
                      />
                      {activeChat?.groupChat && (
                        <FontAwesomeIcon
                          icon={faSignOutAlt}
                          size="lg"
                          className="text-orange-600 hover:cursor-pointer"
                          onClick={() => updateGroupMembers(activeChat?.chatId)}
                        />
                      )}
                    </div>
                  </CardFooter>
                </Card>
              </div>
            </ScrollArea>
          </TabsContent>
          <TabsContent value="members">
            <div className=" flex flex-col">
              <Card className="p-0 border-none min-h-96 shadow-none px-5">
                <CardHeader className="">
                  <h1 className="font-semibold">
                    Members {"("} {activeChat?.participants?.length} {")"}
                  </h1>
                  <div className="flex items-center justify-between">
                    <h1 className="text-sm text-gray-500">
                      {removingMembers?.length} member
                      {removingMembers?.length !== 1 && "s"} selected
                    </h1>
                    <Badge
                      className="bg-orange-600 text-sm shadow-md shadow-orange-300 hover:bg-bg-orange-600 hover:cursor-pointer hover:shadow-sm hover:scale-95"
                      onClick={() => {
                        updateGroupMembers(activeChat?.chatId, removingMembers);
                        setRemovingMembers([]);
                      }}
                    >
                      Remove
                      {/* <FontAwesomeIcon icon={faUserMinus} className="ml-2" /> */}
                    </Badge>
                  </div>
                </CardHeader>
                <CardContent>
                  <div className="flex gap-2">
                    <Input
                      type="text"
                      className="border-0 border-b-2 border-b-orange-400 focus:outline-none focus:ring-0"
                      placeholder="search members...."
                      onChange={handleSearch}
                      value={searchQuery}
                    />

                    <Badge className="bg-white hover:bg-white text-orange-400 hover:cursor-pointer">
                      <FontAwesomeIcon icon={faMagnifyingGlass} size="lg" />
                    </Badge>
                  </div>
                  {/* <div className="mt-4"> */}
                  <ScrollArea className="h-80 mt-4 scrollbar-hide">
                    {suggestedUsers?.length > 0 &&
                      suggestedUsers.map((user, index) => {
                        const isChecked = removingMembers.some(
                          (member) => member.userId === user.userId
                        );
                        const handleCheckboxChange = (checked) => {
                          if (checked) {
                            setRemovingMembers((prev) => [...prev, user]);
                          } else {
                            setRemovingMembers((prev) =>
                              prev.filter(
                                (member) => member.userId !== user.userId
                              )
                            );
                          }
                        };
                        return (
                          <div key={index} className="flex items-center gap-2">
                            <Checkbox
                              checked={isChecked}
                              onCheckedChange={handleCheckboxChange}
                            />
                            <div
                              className="my-2 px-1 py-1 hover:bg-orange-100 flex items-center justify-between"
                              onClick={() => handleUserSelect(user)}
                            >
                              <UserCard key={index} user={user} />
                              {activeChat?.groupAdmin?.includes(
                                user?.userId
                              ) && (
                                <Badge className="px-1 py-0 bg-orange-500">
                                  <h1 className="text-xs">Admin</h1>
                                </Badge>
                              )}
                            </div>
                          </div>
                        );
                      })}
                    {suggestedUsers?.length == 0 &&
                      participants?.map((user, index) => {
                        const isChecked = removingMembers.some(
                          (member) => member.userId === user.userId
                        );
                        const handleCheckboxChange = (checked) => {
                          if (checked) {
                            setRemovingMembers((prev) => [...prev, user]);
                          } else {
                            setRemovingMembers((prev) =>
                              prev.filter(
                                (member) => member.userId !== user.userId
                              )
                            );
                          }
                        };
                        return (
                          <div key={index} className="flex items-center gap-2">
                            <Checkbox
                              checked={isChecked}
                              onCheckedChange={handleCheckboxChange}
                            />
                            <div
                              className="my-2 px-1 py-1 hover:bg-orange-100 flex items-center justify-between"
                              onClick={() => handleUserSelect(user)}
                            >
                              <UserCard key={index} user={user} />
                              {activeChat?.groupAdmin?.includes(
                                user?.userId
                              ) && (
                                <Badge className="px-1 py-0 bg-orange-500">
                                  <h1 className="text-xs">Admin</h1>
                                </Badge>
                              )}
                            </div>
                          </div>
                        );
                      })}
                  </ScrollArea>
                  {/* </div> */}
                </CardContent>
              </Card>
            </div>
          </TabsContent>
          <TabsContent value="add-members">
            <div className=" flex flex-col">
              <Card className="p-0 border-none min-h-96 shadow-none px-5">
                <CardHeader className="">
                  <h1 className="font-semibold">Add Members</h1>
                  <div className="flex items-center justify-between">
                    <h1 className="text-sm text-gray-500">
                      {newMembers?.length} member
                      {newMembers?.length !== 1 && "s"} selected
                    </h1>
                    <Badge
                      className="bg-orange-600 text-sm shadow-md shadow-orange-300 hover:bg-bg-orange-600 hover:cursor-pointer hover:shadow-sm hover:scale-95"
                      onClick={() => {
                        updateGroupMembers(activeChat?.chatId, newMembers);
                        setNewMembers([]);
                      }}
                    >
                      Add
                      <FontAwesomeIcon icon={faUserPlus} className="ml-2" />
                    </Badge>
                  </div>
                </CardHeader>
                <CardContent>
                  <ScrollArea className="w-full overflow-x-auto whitespace-nowrap scrollbar-hide">
                    {newMembers?.length > 0 && (
                      <div className="inline-flex items-center gap-1 py-2">
                        {newMembers.map((user, index) => (
                          <Avatar key={index} className="min-w-[40px]">
                            <AvatarImage
                              src={
                                user?.profilePic
                                  ? user?.profilePic
                                  : user?.gender?.toLowerCase() === "male"
                                  ? DEFAULT_MALE_PIC
                                  : DEFAULT_FEMALE_PIC
                              }
                            />
                            <AvatarFallback>
                              {user?.name?.charAt(0).toUpperCase()}
                            </AvatarFallback>
                          </Avatar>
                        ))}
                      </div>
                    )}
                    <ScrollBar orientation="horizontal" />
                  </ScrollArea>

                  <div className="flex gap-2">
                    <Input
                      type="text"
                      className="border-0 border-b-2 border-b-orange-400 focus:outline-none focus:ring-0"
                      placeholder="search friends...."
                      onChange={handleFriendsSearch}
                      value={searchQuery}
                    />

                    <Badge className="bg-white hover:bg-white text-orange-400 hover:cursor-pointer">
                      <FontAwesomeIcon icon={faMagnifyingGlass} size="lg" />
                    </Badge>
                  </div>
                  {/* <div className="mt-4"> */}
                  <ScrollArea className="h-80 mt-4 ">
                    {suggestedUsers?.length > 0 &&
                      suggestedUsers.map((user, index) => {
                        const isChecked = newMembers.some(
                          (member) => member.userId === user.userId
                        );
                        const handleCheckboxChange = (checked) => {
                          if (checked) {
                            setNewMembers((prev) => [...prev, user]);
                          } else {
                            setNewMembers((prev) =>
                              prev.filter(
                                (member) => member.userId !== user.userId
                              )
                            );
                          }
                        };
                        return (
                          <div key={index} className="flex items-center gap-2">
                            <Checkbox
                              id={`user-${user.userId}`}
                              checked={isChecked}
                              onCheckedChange={handleCheckboxChange}
                            />
                            <div
                              className="my-2 px-1 py-1 hover:bg-orange-100 flex items-center gap-1"
                              onClick={() => handleUserSelect(user)}
                            >
                              <UserCard key={index} user={user} />
                            </div>
                          </div>
                        );
                      })}
                    {suggestedUsers?.length === 0 &&
                      remainingFriends?.map((user, index) => {
                        const isChecked = newMembers.some(
                          (member) => member.userId === user.userId
                        );

                        const handleCheckboxChange = (checked) => {
                          if (checked) {
                            setNewMembers((prev) => [...prev, user]);
                          } else {
                            setNewMembers((prev) =>
                              prev.filter(
                                (member) => member.userId !== user.userId
                              )
                            );
                          }
                        };

                        return (
                          <div
                            key={user.userId}
                            className="flex items-center gap-2"
                          >
                            <Checkbox
                              id={`user-${user.userId}`}
                              checked={isChecked}
                              onCheckedChange={handleCheckboxChange}
                            />
                            <div
                              className="my-2 px-2 py-1 hover:bg-orange-100 cursor-pointer"
                              onClick={() => handleUserSelect(user)}
                            >
                              <UserCard user={user} />
                            </div>
                          </div>
                        );
                      })}
                  </ScrollArea>
                  {/* </div> */}
                </CardContent>
              </Card>
            </div>
          </TabsContent>
        </div>
      </Tabs>
    </div>
  );
};
export default ChatTabs;
