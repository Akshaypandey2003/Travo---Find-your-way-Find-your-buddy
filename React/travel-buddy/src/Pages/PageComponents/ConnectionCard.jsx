/* eslint-disable react/prop-types */
import { useEffect, useState } from "react";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";
import {
  DEFAULT_FEMALE_PIC,
  DEFAULT_MALE_PIC,
} from "../../Constants/constants";
import useUserData from "../../CustomHooks/useUserData";
import { shallowEqual, useSelector } from "react-redux";
import { Badge } from "@/components/ui/badge";

// eslint-disable-next-line no-unused-vars
export const ConnectionCard = ({ userId }) => {
  const [currentUser, setCurrentUser] = useState(null);
  const { getUser, addCloseFriend, removeCloseFriend } = useUserData();

  const user = useSelector((store) => store.auth, shallowEqual); 
  const usersList = user?.usersList;
  const loggedInUser = user?.user;

  const localUser =
    userId === loggedInUser?.userId
      ? loggedInUser
      : usersList?.find((user) => user.userId === userId);

  useEffect(() => {
    if (localUser) {
      setCurrentUser(localUser);
    }
  }, [localUser]);

  useEffect(() => {
    const fetchUser = async () => {
      if (!localUser && !currentUser) {
        const fetchedUser = await getUser(userId);
        setCurrentUser(fetchedUser);
      }
    };
    fetchUser();
  }, [localUser, currentUser, userId]);

  return (
    <div className="notification-card flex items-center justify-between bg-orange-100 my-2 p-2 rounded-lg shadow-md gap-2">
      <div className="flex items-center gap-2">
        <Avatar className="border border-black">
          <AvatarImage
            src={
              currentUser?.profilePic
                ? currentUser?.profilePic
                : currentUser?.gender?.toLowerCase() === "male"
                ? DEFAULT_MALE_PIC
                : DEFAULT_FEMALE_PIC
            }
            alt="sender"
          />
          <AvatarFallback>{currentUser?.name?.charAt(0)}</AvatarFallback>
        </Avatar>
        <h1 className="font-semibold">{currentUser?.name}</h1>
      </div>
      {loggedInUser?.closeFriends?.includes(currentUser?.userId) ? (
        <Badge
          variant="outline"
          className="border-orange-400 hover:cursor-pointer hover:bg-orange-300 hover:border hover:border-orange-600"
          onClick={() => removeCloseFriend(userId)}
        >
          Remove
        </Badge>
      ) : (
        <Badge
          variant="outline"
          className="bg-orange-400 hover:cursor-pointer hover:bg-orange-300 hover:border hover:border-orange-600"
          onClick={() => addCloseFriend(userId)}
        >
          Add
        </Badge>
      )}
    </div>
  );
};

export default ConnectionCard;
