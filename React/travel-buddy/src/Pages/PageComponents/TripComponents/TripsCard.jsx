/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
import { Card } from "@/components/ui/card";
import { shallowEqual, useSelector } from "react-redux";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Link, useParams } from "react-router-dom";

import {
  faBell, faCalendar,
  faClock, faEdit, faPenClip, faPlus,
  faTrash,
  faUser, faUserGroup
} from "@fortawesome/free-solid-svg-icons";
import { TripMemberHover } from "./TripMemberHover";
import { ScrollArea } from "@/components/ui/scroll-area";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import PlanTripForm from "../../User/PlanTripForm";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog";
import useTrip from "../../../CustomHooks/useTrip";
import { Badge } from "@/components/ui/badge";
import { useEffect, useState } from "react";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import {
  DEFAULT_FEMALE_PIC,
  DEFAULT_MALE_PIC,
} from "../../../Constants/constants";
import { TripRequestCard } from "./TripRequestCard";
import useUserData from "../../../CustomHooks/useUserData";

export const TripsCard = ({ trip }) => {
  const { getUser } = useUserData();
  const { sendTripRequest, deleteTrip } = useTrip();
  const [tripOwner, setOwner] = useState(null);
  const user = useSelector((store) => store.auth, shallowEqual); // Get the logged-in user
  const currUser = useParams();
  const loggedInUser = user?.user; // Find the user by ID
  const usersList = user?.usersList;

  const localUser = usersList?.find((user) => user.userId == trip?.createdBy); // Get the current user's data

  useEffect(() => {
    if (localUser) {
      setOwner(localUser);
    }
  }, [localUser]);

  useEffect(() => {
    const fetchUser = async () => {
      if (!localUser && !tripOwner) {
        const fetchedUser = await getUser(trip?.createdBy);
        setOwner(fetchedUser);
      }
    };
    fetchUser();
  }, [localUser, tripOwner, trip]);
  return (
    <>
      {trip?.tripStatus !== "COMPLETED" ? (
        <Card className="p-4">
          <div className="flex justify-between">
            <div className=" p-2 rounded-lg">
              <div className="flex items-center gap-4">
                <div className="flex items-center">
                  <FontAwesomeIcon
                    icon={faUser}
                    className="mr-2 text-orange-600"
                  />
                  <h1>Owner: </h1>
                </div>
                <Link
                  to={
                    loggedInUser
                      ? `/user_profile/${tripOwner?.userId}`
                      : `/login`
                  }
                  state={{ redirectTo: `/user_profile/${tripOwner?.userId}` }}
                >
                  <div className="flex items-center gap-2 hover:cursor-pointer">
                    <Avatar>
                      <AvatarImage
                        src={
                          tripOwner?.profilePic
                            ? tripOwner?.profilePic
                            : tripOwner?.gender?.toLowerCase() === "male"
                            ? DEFAULT_MALE_PIC
                            : DEFAULT_FEMALE_PIC
                        }
                      />
                      <AvatarFallback>
                        {tripOwner?.name?.charAt(0)}
                      </AvatarFallback>
                    </Avatar>
                    <h1 className="font-semibold">{tripOwner?.name}</h1>
                  </div>
                </Link>
              </div>
              <div className="flex my-2">
                <FontAwesomeIcon
                  icon={faUserGroup}
                  className="mr-2 text-orange-600"
                />
                <h1>{trip?.memberSize}</h1>
              </div>
              <div className="flex my-2">
                <FontAwesomeIcon
                  icon={faCalendar}
                  className="mr-2 text-orange-600"
                />
                <h1>{trip?.tripDate}</h1>
              </div>
              <div className="flex my-2">
                <FontAwesomeIcon
                  icon={faClock}
                  className="mr-2 text-orange-600"
                />
                <h1>{trip?.tripDuration}</h1>
              </div>
              <div className="flex my-2">
                <FontAwesomeIcon
                  icon={faPenClip}
                  className="mr-2 text-orange-600"
                />
                <h1>{trip?.tripDescription}</h1>
              </div>
              <div className="flex gap-2 my-2">
                {trip?.tripTags?.map((item, index) => (
                  <Badge
                    key={index}
                    variant="outline"
                    className="border-orange-600 h-4 font-thin"
                  >
                    {item}
                  </Badge>
                ))}
              </div>
            </div>
            {/* <div > */}
            <div className=" p-2  rounded-lg">
              <FontAwesomeIcon
                icon={faUserGroup}
                className="mr-2 text-orange-600"
              />
              <span>Trip Members </span>

              <ScrollArea className=" w-80 p-2 h-28  overflow-y-auto ">
                <div className=" flex flex-wrap gap-2">
                  {trip?.tripMembers?.length == 0 && <h1>No members yet !!</h1>}

                  {trip?.tripMembers?.map((item, index) => (
                    <TripMemberHover
                      key={index}
                      memberId={item}
                      tripId={trip?.tripId}
                    />
                  ))}
                </div>
              </ScrollArea>
            </div>
            {/* </div> */}
          </div>
          <div className="p-4 text-right">
            {currUser?.userId === tripOwner?.userId ? (
              <div className="flex gap-2">
                <Dialog modal={true}>
                  <DialogTrigger
                    aschild="true"
                    className="py-1 px-2 border-2 text-orange-700 font-semibold border-orange-700 hover:border-orange-500  hover:text-orange-500"
                  >
                    <FontAwesomeIcon
                      icon={faEdit}
                      className="mr-2 text-orange-600 text-lg"
                    />
                  </DialogTrigger>

                  <DialogContent className="sm:max-w-[550px]">
                    <ScrollArea className="h-96 rounded-md">
                      <DialogHeader>
                        <DialogTitle>
                          <h1>Plan Trip</h1>
                        </DialogTitle>
                        <DialogDescription>
                          <h1>
                            Plan a trip to your dream destinations and let us
                            find a match for your trip.
                          </h1>
                        </DialogDescription>
                      </DialogHeader>
                      <div className="m-4">
                        <PlanTripForm />
                      </div>
                    </ScrollArea>
                  </DialogContent>
                </Dialog>
                <AlertDialog>
                  <AlertDialogTrigger className="border-2 border-orange-600 px-3">
                    <FontAwesomeIcon
                      icon={faTrash}
                      className="mr-2 text-orange-600 text-lg"
                    />
                  </AlertDialogTrigger>
                  <AlertDialogContent>
                    <AlertDialogHeader>
                      <AlertDialogTitle>
                        Are you absolutely sure?
                      </AlertDialogTitle>
                      <AlertDialogDescription>
                        This action cannot be undone. This will permanently
                        delete your trip and remove your trip data from our
                        servers.
                      </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                      <AlertDialogCancel
                        variant="outline"
                        className="border-2 border-orange-600 text-orange-600 hover:text-orange-800 hover:border-orange-400 hover:bg-orange-100"
                      >
                        Cancel
                      </AlertDialogCancel>
                      <AlertDialogAction
                        className="bg-orange-600 hover:bg-orange-500"
                        onClick={() => deleteTrip(trip?.tripId)}
                      >
                        Continue
                      </AlertDialogAction>
                    </AlertDialogFooter>
                  </AlertDialogContent>
                </AlertDialog>
                {trip?.tripRequests?.length > 0 && (
                  <Dialog modal={true}>
                    <DialogTrigger
                      aschild="true"
                      className=" px-3 border-2 text-orange-700 font-semibold border-orange-700 hover:border-orange-500  hover:text-orange-500"
                    >
                      <FontAwesomeIcon
                        icon={faBell}
                        className="mr-2 text-orange-600 text-lg"
                      />
                      {trip?.tripRequests?.length}
                    </DialogTrigger>

                    <DialogContent className="sm:max-w-[550px]">
                      <ScrollArea className="h-96 rounded-md">
                        <DialogHeader>
                          <DialogTitle>
                            <h1>Trip Requests</h1>
                          </DialogTitle>
                          <DialogDescription>
                            <h1>
                              See the people who are interested in join you
                              trips.
                            </h1>
                          </DialogDescription>
                        </DialogHeader>
                        <div className="m-4">
                          {trip?.tripRequests?.map((item, index) => (
                            <TripRequestCard
                              key={index}
                              tripId={trip?.tripId}
                              userId={item}
                            />
                          ))}
                        </div>
                      </ScrollArea>
                    </DialogContent>
                  </Dialog>
                )}
              </div>
            ) : (
              !trip?.tripMembers?.includes(loggedInUser?.userId) && (
                <div>
                  <AlertDialog>
                    <AlertDialogTrigger className="border-2 border-orange-600 px-4 py-1">
                      <FontAwesomeIcon
                        icon={faUser}
                        className=" text-orange-600 text-xs"
                      />
                      <FontAwesomeIcon
                        icon={faPlus}
                        className=" text-orange-600 text-xs"
                      />
                    </AlertDialogTrigger>
                    <AlertDialogContent>
                      <AlertDialogHeader>
                        <AlertDialogTitle>
                          Are you absolutely sure?
                        </AlertDialogTitle>
                        <AlertDialogDescription>
                          This action cannot be undone. This will send a request
                          to join the trip.
                        </AlertDialogDescription>
                      </AlertDialogHeader>
                      <AlertDialogFooter>
                        <AlertDialogCancel
                          variant="outline"
                          className="border-2 border-orange-600 text-orange-600 hover:text-orange-800 hover:border-orange-400 hover:bg-orange-100"
                        >
                          Cancel
                        </AlertDialogCancel>
                        <AlertDialogAction
                          className="bg-orange-600 hover:bg-orange-500"
                          onClick={() =>
                            !trip?.tripRequests?.includes(
                              loggedInUser?.userId
                            ) &&
                            sendTripRequest({
                              tripId: trip?.tripId,
                              requestTo: tripOwner?.userId,
                            })
                          }
                        >
                          Continue
                        </AlertDialogAction>
                      </AlertDialogFooter>
                    </AlertDialogContent>
                  </AlertDialog>
                </div>
              )
            )}
          </div>
        </Card>
      ) : (
        //Different card design for completed trips
        <Card className="p-4">
          <div className="flex justify-between">
            <div className=" p-2 rounded-lg">
              <div className="flex ">
                <FontAwesomeIcon
                  icon={faUser}
                  className="mr-2 text-orange-600"
                />
                <h1>Owner: {tripOwner?.name}</h1>
              </div>
              <div className="flex my-2">
                <FontAwesomeIcon
                  icon={faUserGroup}
                  className="mr-2 text-orange-600"
                />
                <h1>{trip?.memberSize}</h1>
              </div>
              <div className="flex my-2">
                <FontAwesomeIcon
                  icon={faCalendar}
                  className="mr-2 text-orange-600"
                />
                <h1>{trip?.tripDate}</h1>
              </div>
              <div className="flex my-2">
                <FontAwesomeIcon
                  icon={faClock}
                  className="mr-2 text-orange-600"
                />
                <h1>{trip?.tripDuration}</h1>
              </div>
              <div className="flex my-2">
                <FontAwesomeIcon
                  icon={faPenClip}
                  className="mr-2 text-orange-600"
                />
                <h1>{trip?.tripDescription}</h1>
              </div>
              <div className="flex gap-2 my-2">
                {trip?.tripTags?.map((item, index) => (
                  <Badge
                    key={index}
                    variant="outline"
                    className="border-orange-600 h-4 font-thin"
                  >
                    {item}
                  </Badge>
                ))}
              </div>
            </div>
            {/* <div > */}
            <div className=" p-2  rounded-lg">
              <FontAwesomeIcon
                icon={faUserGroup}
                className="mr-2 text-orange-600"
              />
              <span>Trip Members </span>
              <ScrollArea className=" w-80 p-2 h-28  overflow-y-auto ">
                <div className=" flex flex-wrap gap-2">
                  {trip?.tripMembers?.length == 0 && <h1>No members yet !!</h1>}
                  {trip?.tripMembers?.map((item, index) => (
                    <TripMemberHover
                      key={index}
                      tripId={trip?.tripId}
                      memberId={item}
                    />
                  ))}
                </div>
              </ScrollArea>
            </div>
            {/* </div> */}
          </div>
          <div className="p-4 text-right">
            {loggedInUser?.userId === tripOwner?.userId ? (
              <div className="flex gap-2">
                <Dialog modal={true}>
                  <DialogTrigger
                    aschild="true"
                    className="py-1 px-2 border-2 text-orange-700 font-semibold border-orange-700 hover:border-orange-500  hover:text-orange-500"
                  >
                    <FontAwesomeIcon
                      icon={faEdit}
                      className="mr-2 text-orange-600 text-lg"
                    />
                  </DialogTrigger>

                  <DialogContent className="sm:max-w-[550px]">
                    <ScrollArea className="h-96 rounded-md">
                      <DialogHeader>
                        <DialogTitle>
                          <h1>Plan Trip</h1>
                        </DialogTitle>
                        <DialogDescription>
                          <h1>
                            Plan a trip to your dream destinations and let us
                            find a match for your trip.
                          </h1>
                        </DialogDescription>
                      </DialogHeader>
                      <div className="m-4">
                        <PlanTripForm />
                      </div>
                    </ScrollArea>
                  </DialogContent>
                </Dialog>
                <AlertDialog>
                  <AlertDialogTrigger className="border-2 border-orange-600 px-3">
                    <FontAwesomeIcon
                      icon={faTrash}
                      className="mr-2 text-orange-600 text-lg"
                    />
                  </AlertDialogTrigger>
                  <AlertDialogContent>
                    <AlertDialogHeader>
                      <AlertDialogTitle>
                        Are you absolutely sure?
                      </AlertDialogTitle>
                      <AlertDialogDescription>
                        This action cannot be undone. This will permanently
                        delete your trip and remove your trip data from our
                        servers.
                      </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                      <AlertDialogCancel
                        variant="outline"
                        className="border-2 border-orange-600 text-orange-600 hover:text-orange-800 hover:border-orange-400 hover:bg-orange-100"
                      >
                        Cancel
                      </AlertDialogCancel>
                      <AlertDialogAction
                        className="bg-orange-600 hover:bg-orange-500"
                        onClick={() => deleteTrip(trip?.tripId)}
                      >
                        Continue
                      </AlertDialogAction>
                    </AlertDialogFooter>
                  </AlertDialogContent>
                </AlertDialog>
                {trip?.tripRequests?.length > 0 && (
                  <Dialog modal={true}>
                    <DialogTrigger
                      aschild="true"
                      className=" px-3 border-2 text-orange-700 font-semibold border-orange-700 hover:border-orange-500  hover:text-orange-500"
                    >
                      <FontAwesomeIcon
                        icon={faBell}
                        className="mr-2 text-orange-600 text-lg"
                      />
                      {trip?.tripRequests?.length}
                    </DialogTrigger>

                    <DialogContent className="sm:max-w-[550px]">
                      <ScrollArea className="h-96 rounded-md">
                        <DialogHeader>
                          <DialogTitle>
                            <h1>Plan Trip</h1>
                          </DialogTitle>
                          <DialogDescription>
                            <h1>
                              Plan a trip to your dream destinations and let us
                              find a match for your trip.
                            </h1>
                          </DialogDescription>
                        </DialogHeader>
                        <div className="m-4">
                          <PlanTripForm />
                        </div>
                      </ScrollArea>
                    </DialogContent>
                  </Dialog>
                )}
              </div>
            ) : (
              !trip?.tripMembers?.includes(loggedInUser?.userId) && (
                <div>
                  <AlertDialog>
                    <AlertDialogTrigger className="border-2 border-orange-600 px-4 py-1">
                      <FontAwesomeIcon
                        icon={faUser}
                        className=" text-orange-600 text-xs"
                      />
                      <FontAwesomeIcon
                        icon={faPlus}
                        className=" text-orange-600 text-xs"
                      />
                    </AlertDialogTrigger>
                    <AlertDialogContent>
                      <AlertDialogHeader>
                        <AlertDialogTitle>
                          Are you absolutely sure?
                        </AlertDialogTitle>
                        <AlertDialogDescription>
                          This action cannot be undone. This will send a request
                          to join the trip.
                        </AlertDialogDescription>
                      </AlertDialogHeader>
                      <AlertDialogFooter>
                        <AlertDialogCancel
                          variant="outline"
                          className="border-2 border-orange-600 text-orange-600 hover:text-orange-800 hover:border-orange-400 hover:bg-orange-100"
                        >
                          Cancel
                        </AlertDialogCancel>
                        <AlertDialogAction
                          className="bg-orange-600 hover:bg-orange-500"
                          onClick={() =>
                            !trip?.tripRequests?.includes(
                              loggedInUser?.userId
                            ) &&
                            sendTripRequest({
                              tripId: trip?.tripId,
                              requestTo: tripOwner?.userId,
                            })
                          }
                        >
                          Continue
                        </AlertDialogAction>
                      </AlertDialogFooter>
                    </AlertDialogContent>
                  </AlertDialog>
                </div>
              )
            )}
          </div>
        </Card>
      )}
    </>
  );
};
export default TripsCard;
