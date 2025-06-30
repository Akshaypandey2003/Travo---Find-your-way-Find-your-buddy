/* eslint-disable no-unused-vars */
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import ReactCardSlider from "react-card-slider-component";
import { shallowEqual, useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import CustomerCard from "../PageComponents/CustomerCard";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { ScrollArea } from "@/components/ui/scroll-area";
import useUserData from "../../CustomHooks/useUserData";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faWarning } from "@fortawesome/free-solid-svg-icons";
import { useEffect, useState } from "react";
import TripAccordian from "../PageComponents/TripComponents/TripAccordian";
import CreateBlogForm from "../PageComponents/BlogComponents/CreateBlogForm";
import BlogCard from "../PageComponents/BlogComponents/BlogCard";

export const UserTabs = () => {
  const slides = [
    {
      image: "https://picsum.photos/200/300",
      title: "This is a title",
      description: "This is a description",
    },
    {
      image: "https://picsum.photos/600/500",
      title: "This is a second title",
      description: "This is a second description",
    },
    {
      image: "https://picsum.photos/700/600",
      title: "This is a third title",
      description: "This is a third description",
    },
    {
      image: "https://picsum.photos/500/400",
      title: "This is a fourth title",
      description: "This is a fourth description",
    },
    {
      image: "https://picsum.photos/200/300",
      title: "This is a fifth title",
      description: "This is a fifth description",
    },
    {
      image: "https://picsum.photos/800/700",
      title: "This is a sixth title",
      description: "This is a sixth description",
    },
    {
      image: "https://picsum.photos/300/400",
      title: "This is a seventh title",
      description: "This is a seventh description",
    },
  ];
  const suggestedVisitTime = [
    {
      interval: "Jan - March",
      desc: "Some description here for specific time interval to visit a particular locations.",
    },
    {
      interval: "April - June",
      desc: "Some description here for specific time interval to visit a particular locations.",
    },
    {
      interval: "July - Sept",
      desc: "Some description here for specific time interval to visit a particular locations.",
    },
    {
      interval: "Oct - Dec",
      desc: "Some description here for specific time interval to visit a particular locations.",
    },
  ];
  const specialEvents = [
    {
      event: "Maha Shivaratri",
      desc: "This is one of the best event to experience at this location.",
    },
    {
      event: "Ganesh Chaturthi",
      desc: "This is one of the best event to experience at this location.",
    },
    {
      event: "Holi",
      desc: "This is one of the best event to experience at this location.",
    },
    {
      event: "Diwali",
      desc: "This is one of the best event to experience at this location.",
    },
    {
      event: "Ramnavmi",
      desc: "This is one of the best event to experience at this location.",
    },
  ];
  const blog = useSelector((store) => store.blog, shallowEqual);
  const { getUser } = useUserData();
  const [currentUser, setCurrentUser] = useState(null);

  const { userId } = useParams(); // The id from URL like /user_profile/:userId
  const user = useSelector((store) => store.auth, shallowEqual); // Get the logged-in user
  const usersList = user?.usersList;
  const loggedInUser = user?.user;
  let localUser =
    userId === loggedInUser?.userId
      ? loggedInUser
      : usersList?.find((user) => user.userId == userId); // Find the user by ID
  const currentUserBlogs = blog?.blogs.filter(
    (blog) => blog?.blogAuthor == currentUser?.userId
  );
  // console.log("Blogs inside userTAbs: ",currentUserBlogs);
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

  const following =
    currentUser?.following?.length > 0
      ? usersList?.filter((item) =>
          currentUser?.following?.includes(item?.userId)
        )
      : [];
  const followers =
    currentUser?.followers?.length > 0
      ? usersList?.filter((item) =>
          currentUser?.followers?.includes(item?.userId)
        )
      : [];
  // console.log("Current user is: ", currentUser);

  let upcomingTrips = currentUser?.trips?.filter(
    (trip) => trip.tripStatus === "UPCOMING"
  );
  let ongoingTrips = currentUser?.trips?.filter(
    (trip) => trip.tripStatus === "ONGOING"
  );
  let completedTrips = currentUser?.trips?.filter(
    (trip) => trip.tripStatus === "COMPLETED"
  );
  // console.log("Upcoming trips before filtering", upcomingTrips);
  if (
    currentUser?.userId != loggedInUser?.userId &&
    !currentUser?.closeFriends?.includes(loggedInUser?.userId)
  ) {
    upcomingTrips = upcomingTrips?.filter(
      (trip) => trip.isPrivateTrip.toLowerCase() === "false"
    );
    ongoingTrips = ongoingTrips?.filter(
      (trip) => trip.isPrivateTrip.toLowerCase() === "false"
    );
    completedTrips = completedTrips?.filter(
      (trip) => trip.isPrivateTrip.toLowerCase() === "false"
    );
  }
  const tripSuggestions = usersList?.flatMap((user) =>
    user?.trips?.filter(
      (trip) =>
        trip?.isPrivateTrip === "false" ||
        (trip?.isPrivateTrip === "true" &&
          user?.closeFriends?.includes(currentUser?.userId))
    )
  );

  // console.log("Upcoming trips after filtering", upcomingTrips);
  //  console.log("On going trips after filtering", ongoingTrips);
  //  console.log("completed trips after filtering", completedTrips);

  return (
    <div className=" flex justify-center p-5 ">
      <Tabs defaultValue="blogs">
        <TabsList className=" gap-2">
          <TabsTrigger value="blogs" className="border-none text-black">
            Blogs
          </TabsTrigger>
          <TabsTrigger value="trips" className="border-none text-black">
            Trips
          </TabsTrigger>
          {following?.length > 0 && (
            <TabsTrigger value="following" className="border-none text-black">
              Following
            </TabsTrigger>
          )}
          {following?.length > 0 && (
            <TabsTrigger value="followers" className="border-none text-black">
              Followers
            </TabsTrigger>
          )}

          {/* {(following?.length == 0 && followers?.length == 0) && ( */}
          {currentUser?.userId == loggedInUser?.userId && (
            <TabsTrigger value="people" className="border-none text-black">
              Discover People
            </TabsTrigger>
          )}

          {/* )} */}
          {userId == user?.user?.userId && (
            <TabsTrigger value="requests" className="border-none text-black">
              Requests
            </TabsTrigger>
          )}
          <TabsTrigger value="feedback" className="border-none text-black">
            Feedbacks
          </TabsTrigger>
        </TabsList>
        <TabsContent value="blogs">
          <div className="w-[80rem] py-5  flex">
            <ScrollArea className="w-[80%] h-[100vh] overflow-y-auto py-5 px-10">
              {!currentUserBlogs || currentUserBlogs.length === 0 ? (
                <div className="flex justify-between items-center py-1 px-1">
                  <div className="flex items-center gap-2  rounded-2xl w-auto">
                    <FontAwesomeIcon
                      size="xl"
                      icon={faWarning}
                      className="text-orange-400"
                    />
                    <Badge
                      variant="outline"
                      className="text-lg border-none text-orange-400"
                    >
                      Haven't Posted anything currentlly !!
                    </Badge>
                  </div>
                  {currentUser?.userId === loggedInUser?.userId && (
                    <div>
                      <CreateBlogForm formType={"create"} />
                    </div>
                  )}
                </div>
              ) : (
                <div className="">
                  {currentUserBlogs?.map((item, index) => (
                    <BlogCard key={index} blog={item} />
                  ))}
                </div>
              )}
            </ScrollArea>
          </div>
        </TabsContent>
        <TabsContent value="trips">
          <div className="w-[80rem] py-5  flex ">
            <ScrollArea className="w-[80%] h-96 overflow-y-auto py-5 px-10">
              {upcomingTrips?.length == 0 &&
              ongoingTrips?.length == 0 &&
              completedTrips?.length == 0 ? (
                currentUser?.userId == loggedInUser?.userId ? (
                  <>
                    <div className="flex items-center gap-2  rounded-2xl w-auto">
                      <FontAwesomeIcon
                        size="xl"
                        icon={faWarning}
                        className="text-orange-400"
                      />
                      <Badge
                        variant="outline"
                        className="text-lg border-none text-orange-400"
                      >
                        You did not plan any trip yet !! please plan some trip
                        or join some trips. .
                      </Badge>
                    </div>
                    <div className="gap-4  items-center justify-start flex-nowrap mt-4">
                      <h1 className=" text-xl text-orange-500 font-bold">
                        Upcoming Trips
                      </h1>
                      {tripSuggestions?.map((item, index) => (
                        <TripAccordian key={index} trip={item} />
                      ))}
                    </div>
                  </>
                ) : (
                  currentUser?.userId != loggedInUser?.userId && (
                    <div className="flex items-center gap-2  rounded-2xl w-auto">
                      <FontAwesomeIcon
                        size="xl"
                        icon={faWarning}
                        className="text-orange-400"
                      />
                      <Badge
                        variant="outline"
                        className="text-lg border-none text-orange-400"
                      >
                        Don't have any trip currentlly !!
                      </Badge>
                    </div>
                  )
                )
              ) : (
                <div className=" gap-4  items-center justify-start flex-nowrap">
                  {upcomingTrips?.length > 0 && (
                    <h1 className=" text-xl text-orange-500 font-bold">
                      Upcoming Trips{" "}
                    </h1>
                  )}
                  {upcomingTrips?.map((item, index) => (
                    <TripAccordian key={index} trip={item} />
                  ))}

                  {ongoingTrips?.length > 0 && <h1>On Going Trips </h1>}
                  {ongoingTrips?.map((item, index) => {
                    <TripAccordian key={index} trip={item} />;
                  })}

                  {completedTrips?.length > 0 && <h1>Completed Trips </h1>}
                  {completedTrips?.map((item, index) => {
                    <TripAccordian key={index} trip={item} />;
                  })}

                  {/* {currentUser?.trips?.map((item, index) => (
                  <TripsCard key={index} trip={item} />
                ))} */}
                </div>
              )}
            </ScrollArea>
          </div>
        </TabsContent>
        <TabsContent value="following">
          <div className="w-[80rem] py-5 ">
            <ScrollArea className=" h-96 overflow-y-auto py-5 px-10 ">
              <div className="flex items-center flex-wrap gap-5">
                {following?.map((item, index) => (
                  <CustomerCard key={index} user={item} />
                ))}
              </div>
            </ScrollArea>
          </div>
        </TabsContent>
        <TabsContent value="people">
          <div className="w-[80rem] py-5 ">
            <ScrollArea className=" h-96 overflow-y-auto py-5 px-10 ">
              <div className="flex items-center flex-wrap gap-5">
                {usersList?.map((item, index) => (
                  <CustomerCard key={index} user={item} />
                ))}
              </div>
            </ScrollArea>
          </div>
        </TabsContent>
        <TabsContent value="followers">
          <div className="w-[80rem] py-5 ">
            <ScrollArea className=" h-96 overflow-y-auto py-5 px-10 ">
              <div className="flex gap-4 py-10 flex-wrap">
                {followers?.map((item, index) => (
                  <CustomerCard key={index} user={item} />
                ))}
              </div>
            </ScrollArea>
            {/* <div className=" flex px-5 justify-around ">
              <div className="  w-[50rem] px-2">
                <h1 className="font-bold text-2xl">Best Time To Visit</h1>
                <div className="flex flex-wrap  gap-4 p-2 justify-center">
                  {suggestedVisitTime.map((item) => (
                    <Card key={item} className="px-4 py-2 w-[15rem]">
                      <h1 className="font-semibold text-xl">{item.interval}</h1>
                      <p>{item.desc}</p>
                    </Card>
                  ))}
                </div>
              </div>
              <div className=" w-[30rem] px-2">
                <h1 className="font-bold text-2xl">Special Events</h1>
                <div className="flex flex-wrap gap-4 p-2">
                  {specialEvents.map((item) => (
                    <HoverCard key={item}>
                      <HoverCardTrigger className="cursor-pointer px-4 border border-orange-500 rounded-full hover:text-black">
                        {item.event}
                      </HoverCardTrigger>
                      <HoverCardContent>{item.desc}</HoverCardContent>
                    </HoverCard>
                  ))}
                </div>
              </div>
              <div></div>
            </div> */}
          </div>
        </TabsContent>
        <TabsContent value="requests">
          <div className="w-[80rem] py-5 ">
            <ReactCardSlider slides={slides} />
          </div>
        </TabsContent>
        <TabsContent value="feedback">
          <div className=" w-[80rem] p-10 flex items-center gap-2  rounded-2xl">
            <FontAwesomeIcon
              size="xl"
              icon={faWarning}
              className="text-orange-400"
            />
            <Badge
              variant="outline"
              className="text-lg border-none text-orange-400"
            >
              Didn't get any feedback currentlly !!
            </Badge>
          </div>
        </TabsContent>
      </Tabs>
    </div>
  );
};
export default UserTabs;
