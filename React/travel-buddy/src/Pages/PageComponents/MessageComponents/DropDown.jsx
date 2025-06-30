/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
"use client";

import * as React from "react";
// import { DropdownMenuCheckboxItemProps } from "@radix-ui/react-dropdown-menu"

import { Button } from "@/components/ui/button";
import { Switch } from "@/components/ui/switch";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  DropdownMenu,
  DropdownMenuRadioGroup,
  DropdownMenuRadioItem,
  DropdownMenuContent,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import ChatTabs from "./ChatTabs";
import {
  faBellSlash,
  faChevronDown,
  faDumpster,
  faEllipsisVertical,
  faGripLinesVertical,
  faGripVertical,
  faSignOutAlt,
  faStar,
  faUserGroup,
} from "@fortawesome/free-solid-svg-icons";
import { useSelector } from "react-redux";
import {
  Card,
  CardHeader,
  CardContent,
  CardFooter,
} from "@/components/ui/card";
import { ScrollArea } from "@/components/ui/scroll-area";
import {
  DEFAULT_FEMALE_PIC,
  DEFAULT_MALE_PIC,
} from "../../../Constants/constants";
import useChat from "../../../CustomHooks/useChat";
// type Checked = DropdownMenuCheckboxItemProps["checked"]

export const DropDown = ({ chatUser, participants }) => {
  const [showStatusBar, setShowStatusBar] = React.useState(true);
  const [showActivityBar, setShowActivityBar] = React.useState(false);
  const [showPanel, setShowPanel] = React.useState(false);
  const activeChat = useSelector((store) => store.chat.activeChat);
  const [disappearingMessages, setDisappearingMessages] = React.useState(false);
  const [duration, setDuration] = React.useState("");
  const loggedInUser = useSelector((store) => store.auth.user);
  const { updateFavorite,updateGroupMembers } = useChat();

  return (
    <DropdownMenu className="">
      <DropdownMenuTrigger asChild className="px-4 hover:cursor-pointer">
        {/* <Button variant="outline" > */}
        <FontAwesomeIcon icon={faEllipsisVertical} />
        {/* </Button> */}
      </DropdownMenuTrigger>
      <DropdownMenuContent className=" mr-10">
        {activeChat?.groupChat ? (
          <ChatTabs chatUser={chatUser} participants={participants} />
        ) : (
          <div>
            <ScrollArea className="h-96">
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
                          chatUser?.profilePic ||
                          (chatUser?.gender?.toLowerCase() === "male"
                            ? DEFAULT_MALE_PIC
                            : DEFAULT_FEMALE_PIC)
                        }
                        alt="profile"
                      />
                    </div>
                  </CardHeader>
                  <CardContent className="flex flex-col gap-4">
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
                    <div className="flex flex-col gap-1">
                      <h1 className="font-semibold">Mute Notifications</h1>
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <div className="flex items-center hover:cursor-pointer">
                            <div className="flex gap-2 border-2 rounded-md p-1 px-4">
                              <FontAwesomeIcon icon={faBellSlash} />
                              <FontAwesomeIcon icon={faChevronDown} />
                            </div>
                          </div>
                          {/* <Button variant="outline">Open</Button> */}
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
                        className={activeChat?.favoriteBy?.includes(loggedInUser?.userId) ? "text-orange-600 hover:cursor-pointer" : "text-orange-300 hover:cursor-pointer"}
                        onClick={()=> updateFavorite(activeChat?.chatId, loggedInUser?.userId)}
                      />
                      {activeChat?.groupChat && (
                        <FontAwesomeIcon
                          icon={faSignOutAlt}
                          size="lg"
                          className="text-orange-600 hover:cursor-pointer"
                          onClick={()=> updateGroupMembers(activeChat?.chatId)}
                        />
                      )}
                    </div>
                  </CardFooter>
                </Card>
              </div>
            </ScrollArea>
          </div>
        )}
      </DropdownMenuContent>
    </DropdownMenu>
  );
};
export default DropDown;
