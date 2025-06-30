/* eslint-disable react/prop-types */
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { DEFAULT_FEMALE_PIC, DEFAULT_MALE_PIC } from "../../../Constants/constants";

export const UserCard = ({user}) => {
    // console.log("user in user card: ", user);
    // console.log("user name in user card: ", user?.name);
  return (
    <div>
      <div className="flex items-center gap-2">
        <Avatar>
          <AvatarImage
            src={
                 user?.profilePic
                ? user?.profilePic
                : user?.gender?.toLowerCase() === "male"
                ? DEFAULT_MALE_PIC
                : DEFAULT_FEMALE_PIC
            }
          />
          <AvatarFallback>{user?.name?.charAt(0).toUpperCase()}</AvatarFallback>
        </Avatar>
        <h1>{user?.name}</h1>
      </div>
    </div>
  );
};
export default UserCard;
