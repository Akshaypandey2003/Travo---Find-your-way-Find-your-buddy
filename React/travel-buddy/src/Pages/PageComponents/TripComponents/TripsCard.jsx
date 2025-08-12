/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */

import { Card } from "@/components/ui/card";
import { useSelector, shallowEqual } from "react-redux";
import { Link, useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import {
  faUser,
  faCalendarAlt,
  faClock,
  faUserGroup,
  faQuoteLeft,
  faTags,
  faUserPlus,
  faTrash,
  faEdit,
} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";
import { ScrollArea, ScrollBar } from "@/components/ui/scroll-area";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { TripMemberHover } from "./TripMemberHover";

import FeedbackSection from "./FeedbackSection";
import useUserData from "../../../CustomHooks/useUserData";
import {
  DEFAULT_FEMALE_PIC,
  DEFAULT_MALE_PIC,
} from "../../../Constants/constants";
import useTrip from "../../../CustomHooks/useTrip";
import TripFeedbackForm from "./TripFeedbackForm";
import { toast } from "sonner";

const UserAvatar = ({ user }) => (
  <Avatar>
    <AvatarImage
      src={
        user?.profilePic
          ? user.profilePic
          : user?.gender?.toLowerCase() === "male"
          ? DEFAULT_MALE_PIC
          : DEFAULT_FEMALE_PIC
      }
    />
    <AvatarFallback>{user?.name?.charAt(0)}</AvatarFallback>
  </Avatar>
);

const InfoItem = ({ icon, label, value }) => (
  <div className="flex items-start gap-3">
    <div className="text-orange-600">
      <FontAwesomeIcon icon={icon} className="h-4 w-4 mt-1" />
    </div>
    <div>
      <p className="text-sm text-muted-foreground">{label}</p>
      <p className="font-medium text-gray-800 leading-tight">{value}</p>
    </div>
  </div>
);

export const TripsCard = ({ trip }) => {

  // console.log("Current Trip is: ",trip);
  const { getUser } = useUserData();
  const [tripOwner, setOwner] = useState(null);
  const [showMore, setShowMore] = useState(false);

  const { fetchTripFeedbacks,postTripFeedback } = useTrip();

const tripFeedbacks = useSelector((store) => {
  const feedbackMap = store.trip?.tripFeedbacks || {};
  return feedbackMap[trip?.tripId] || [];
});

console.log("Trip feedbacks are: ", tripFeedbacks);

  const { user: loggedInUser, usersList } = useSelector(
    (store) => store.auth,
    shallowEqual
  );
  const { userId: currUserId } = useParams();
  const localUser = usersList?.find((u) => u.userId === trip?.createdBy);

  useEffect(() => {
    if (localUser) setOwner(localUser);
    else if (!tripOwner) getUser(trip?.createdBy).then(setOwner);
  }, [localUser, tripOwner, trip]);

  useEffect(() => {
    fetchTripFeedbacks(trip?.tripId);
  }, [trip?.tripId]);

  const mockFeedbacks = [
    {
      authorName: "Ananya Sharma",
      authorPic: "https://randomuser.me/api/portraits/women/68.jpg",
      comment:
        "Absolutely loved the trip! The planning was top-notch and the locations were breathtaking. Looking forward to more adventures!",
      rating: 5,
      tags: ["well-organized", "memorable", "photogenic"],
      createdAt: "02 July 2025",
    },
    {
      authorName: "Ravi Verma",
      authorPic: "https://randomuser.me/api/portraits/men/12.jpg",
      comment: "Good overall, but I wish we had more time at the waterfalls.",
      rating: 4,
      tags: ["scenic", "need-more-time"],
      createdAt: "01 July 2025",
    },
    {
      authorName: "Sneha Iyer",
      authorPic: "", // No profile picture
      comment:
        "Amazing group and great coordination. Food arrangements could have been better though.",
      rating: 4,
      tags: ["fun-group", "food-could-improve"],
      createdAt: "30 June 2025",
    },
    {
      authorName: "Pranav Desai",
      authorPic: "https://randomuser.me/api/portraits/men/44.jpg",
      comment:
        "Loved the adventure part. Would suggest a little more downtime next time.",
      rating: 3,
      tags: ["adventure", "tight-schedule"],
      createdAt: "29 June 2025",
    },
    {
      authorName: "Ritika Jain",
      authorPic: "https://randomuser.me/api/portraits/women/45.jpg",
      comment: "One of the best trips ever! Made so many new friends.",
      rating: 5,
      tags: ["friendship", "perfect"],
      createdAt: "28 June 2025",
    },
    {
      authorName: "Nikhil Joshi",
      authorPic: "", // fallback avatar
      comment: "Great itinerary and fun experience overall!",
      rating: 4,
      tags: ["great-itinerary", "fun"],
      createdAt: "27 June 2025",
    },
  ];

  return (
    <Card className="p-6 space-y-6 border-orange-200 shadow-sm hover:shadow-md transition rounded-2xl">
      <div className="grid md:grid-cols-2 gap-8">
        <div className="space-y-4">
          <div className="flex items-center gap-3">
            <UserAvatar user={tripOwner} />
            <div>
              <p className="text-xs text-muted-foreground">Trip Organizer</p>
              <Link
                to={
                  loggedInUser ? `/user_profile/${tripOwner?.userId}` : "/login"
                }
                className="font-semibold text-orange-700 hover:underline"
              >
                {tripOwner?.name || "Loading..."}
              </Link>
            </div>
          </div>

          <InfoItem
            icon={faUserGroup}
            label="Members"
            value={trip?.memberSize}
          />
          <InfoItem
            icon={faCalendarAlt}
            label="Trip Date"
            value={trip?.tripDate}
          />
          <InfoItem
            icon={faClock}
            label="Duration"
            value={trip?.tripDuration}
          />
        </div>

        <div className="space-y-4">
          <InfoItem
            icon={faQuoteLeft}
            label="Description"
            value={trip?.tripDescription}
          />
          {trip?.tripTags?.length > 0 && (
            <div className="flex flex-wrap gap-2">
              {trip.tripTags.map((tag, i) => (
                <Badge
                  key={i}
                  variant="outline"
                  className="border-orange-400 text-orange-600 text-xs"
                >
                  #{tag}
                </Badge>
              ))}
            </div>
          )}

          <div>
            <p className="text-sm text-muted-foreground mb-1">Members</p>
            <ScrollArea className="w-80 h-14 border rounded-md border-orange-100 p-1">
              <div className="flex gap-2">
                {trip?.tripMembers?.length > 0 ? (
                  trip.tripMembers.map((id, i) => (
                    <>
                      <TripMemberHover
                        key={i}
                        memberId={id}
                        tripId={trip?.tripId}
                      />
                    </>
                  ))
                ) : (
                  <p className="text-sm text-gray-500">No members yet</p>
                )}
              </div>
              <ScrollBar orientation="horizontal" />
            </ScrollArea>
          </div>
        </div>
      </div>

      <div className="text-right flex justify-between">
        {currUserId === tripOwner?.userId ? (
          <div className="flex justify-end gap-4 items-center text-orange-600">
            <button
              onClick={() => {
                // Edit modal logic
                console.log("Edit Trip Clicked");
              }}
              className="hover:text-orange-800 transition"
              title="Edit Trip"
            >
              <FontAwesomeIcon icon={faEdit} className="h-4 w-4" />
            </button>
            <button
              onClick={() => {
                // Delete confirmation logic
                console.log("Delete Trip Clicked");
              }}
              className="hover:text-red-600 transition"
              title="Delete Trip"
            >
              <FontAwesomeIcon icon={faTrash} className="h-4 w-4" />
            </button>
          </div>
        ) : !trip?.tripMembers?.includes(loggedInUser?.userId) ? (
          <div className="flex justify-end text-orange-600">
            <button
              onClick={() => {
                // Send join request logic
                console.log("Send Trip Join Request");
              }}
              className="hover:text-orange-800 transition flex items-center gap-1"
              title="Request to Join"
            >
              <FontAwesomeIcon icon={faUserPlus} className="h-5 w-5" />
              <span className="text-sm">Join</span>
            </button>
          </div>
        ) : (
          <div></div>
        )}

        <Button
          variant="ghost"
          size="sm"
          onClick={() => setShowMore(!showMore)}
          className="text-orange-600 hover:underline"
        >
          {showMore ? "Hide Feedback" : "View Feedback"}
        </Button>
      </div>

      {showMore && <FeedbackSection feedbacks={tripFeedbacks} />}

      {(trip?.tripMembers?.includes(loggedInUser?.userId) || trip?.createdBy===loggedInUser?.userId) &&
        !tripFeedbacks.some((fb) => fb.authorId === loggedInUser?.userId) && (
          <TripFeedbackForm
            tripId={trip?.tripId}
            onSubmit={(feedback) => {
              // Call your API or Redux dispatch to save feedback
               postTripFeedback(feedback);
              console.log("Submitting feedback:", feedback);
              // Optionally dispatch(updateTripFeedbacks({ tripId: trip.tripId, feedbacks: [feedback], append: true }))
              toast.success("Feedback submitted successfully!");
            }}
          />
        )}
    </Card>
  );
};

export default TripsCard;
