/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import {
  HoverCard,
  HoverCardContent,
  HoverCardTrigger,
} from "@/components/ui/hover-card";
import { Badge } from "@/components/ui/badge";
import { shallowEqual, useSelector } from "react-redux";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faLocationDot
} from "@fortawesome/free-solid-svg-icons";
import { Link } from "react-router-dom";
import useUserData from "../../../CustomHooks/useUserData";
import {
  DEFAULT_FEMALE_PIC,
  DEFAULT_MALE_PIC,
} from "../../../Constants/constants";
import { useEffect, useState } from "react";
import useTrip from "../../../CustomHooks/useTrip";

export const TripMemberHover = ({memberId , tripId}) => {

  // console.log("Trip id inside hover is : ",tripId);
  const [member, setMember] = useState(null);
  const { getUser } = useUserData();
  const {removeTripMember} = useTrip();
  const user = useSelector((store) => store.auth, shallowEqual);
  const usersList = user?.usersList;
  const tripMember = usersList?.find((user) => user.userId === memberId);
//  console.log("Member is: ", member?.userId);
  useEffect(() => {
    if (!member && tripMember) {
      setMember(tripMember);
    }
  }, [tripMember, member]);

  useEffect(() => {
    const fetchUser = async () => {
      if (!member && !tripMember) {
        const fetchedUser = await getUser(memberId);
        setMember(fetchedUser);
      }
    };
    fetchUser();
  }, [member, tripMember, memberId]);

  return (
    <HoverCard>
      <HoverCardTrigger asChild>
        <Avatar>
          <AvatarImage
            src={
              member?.profilePic
                ? member?.profilePic
                : member?.gender?.toLowerCase() === "male"
                ? DEFAULT_MALE_PIC
                : DEFAULT_FEMALE_PIC
            }
          />
          <AvatarFallback>{member?.name?.charAt(0)}</AvatarFallback>
        </Avatar>
      </HoverCardTrigger>
      <HoverCardContent className="p-1">
       
          <div className="flex justify-between items-center">
             <Link
          to={member ? `/user_profile/${memberId}` : `/login`}
          state={{ redirectTo: `/user_profile/${memberId}` }}
        >
            <div className="flex gap-1 items-center">
              <Avatar>
                <AvatarImage
                  src={
                    member?.profilePic
                      ? member?.profilePic
                      : member?.gender?.toLowerCase() === "male"
                      ? DEFAULT_MALE_PIC
                      : DEFAULT_FEMALE_PIC
                  }
                />
                <AvatarFallback>{member?.name?.charAt(0)}</AvatarFallback>
              </Avatar>
              <h4 className="text-sm font-semibold">{member?.name}</h4>
              {member?.country && (
                <>
                  <FontAwesomeIcon
                    icon={faLocationDot}
                    className="text-orange-600"
                  />
                  <h4 className="text-sm font-light">{member?.country}</h4>
                </>
              )}
            </div>
            </Link>
            <div>
              <Badge variant="outline" className="border border-orange-500 text-orange-500 hover:text-white hover:bg-orange-500 hover:cursor-pointer" onClick={()=>removeTripMember(tripId,member?.userId)}>Remove</Badge>
            </div>
          </div>
        
      </HoverCardContent>
    </HoverCard>
  );
};
