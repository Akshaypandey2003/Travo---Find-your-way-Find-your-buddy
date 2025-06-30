/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { DEFAULT_FEMALE_PIC, DEFAULT_MALE_PIC } from "../../../Constants/constants";
import useUserData from "../../../CustomHooks/useUserData";
import { shallowEqual, useSelector } from "react-redux";
import { useEffect, useState } from "react";
import useTrip from "../../../CustomHooks/useTrip";

export const TripRequestCard = ({ tripId,userId }) => {
  const user = useSelector((store) => store.user, shallowEqual);
  const { getUser } = useUserData();
  const {acceptTripRequest,deleteTripRequest} = useTrip();
  const [requestedUser, setUser] = useState(null);
  const localUser = user?.usersList?.find(
    (user, index) => user?.userId == userId
  );

  useEffect(() => {
    if (!requestedUser && localUser) {
      setUser(localUser);
    }
  }, [requestedUser, localUser]);

  useEffect(() => {
    const fetchUser = async () => {
      if (!requestedUser && !localUser) {
        const fetchedUser = await getUser(userId);
        setUser(fetchedUser);
      }
    };
    fetchUser();
  }, [requestedUser, localUser, userId]);

  return (
    <>
      <div className="notification-card flex items-center justify-between bg-orange-100 my-2 p-2 rounded-lg shadow-md gap-2 ">
        <div className="flex items-center gap-2">
          <Avatar className="border border-black">
            <AvatarImage
              src={requestedUser?.profilePic || requestedUser?.gender?.toLowerCase()=="male" ? DEFAULT_MALE_PIC : DEFAULT_FEMALE_PIC}
              alt="sender"
            />
            <AvatarFallback></AvatarFallback>
          </Avatar>
          <h1 className="font-semibold">
            {requestedUser?.name}{" Wants to join your trip."}
          </h1>
        </div>
          <div>
            <Badge
              variant="outline"
              className="bg-orange-400  hover:cursor-pointer hover:bg-orange-300 hover:border hover:border-orange-600"
              onClick={() =>
                   acceptTripRequest(
                      null,
                      tripId,
                      userId
                    )
              }
            >
              Accept
            </Badge>

            <Badge
              variant="outline"
              className=" border-orange-400  hover:cursor-pointer hover:border-orange-800 hover:bg-orange-200"
              onClick={() => deleteTripRequest(tripId,userId)}
            >
              Delete
            </Badge>
          </div>
      </div>
    </>
  );
};
