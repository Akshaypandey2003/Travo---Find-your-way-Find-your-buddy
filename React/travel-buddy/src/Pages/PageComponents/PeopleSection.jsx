/* eslint-disable react-hooks/exhaustive-deps */
/* eslint-disable no-unused-vars */
import { useCallback, useEffect, useRef, useState } from "react";
import { shallowEqual, useDispatch, useSelector } from "react-redux";
import useUserData from "../../CustomHooks/useUserData";
import { setNextPageToken } from "../../Redux/Slices/authSlice";
import CustomerCard from "./CustomerCard";
import { Skeleton } from "@/components/ui/skeleton";
import { Button } from "@/components/ui/button";
import { ScrollArea, ScrollBar } from "@/components/ui/scroll-area";

export const PeopleSection = () => {
  const { getAllUsers } = useUserData();
  const dispatch = useDispatch();
  const [loading, setLoading] = useState(false);
  const [showSkeleton, setShowSkeleton] = useState(true); // control skeleton visibility
  const usersList = useSelector((store) => store.auth.usersList, shallowEqual);
  const loggedInUser = useSelector((store) => store.auth.user, shallowEqual);
  const [page, setPage] = useState(0);
  const loadMoreRef = useRef(null);
  const scrollRef = useRef(null);

  const userNextPageToken = useSelector(
    (store) => store.auth.nextPageToken,
    shallowEqual
  );

  const loadMoreUsers = useCallback(async () => {
    if (!userNextPageToken || loading) return;

    setLoading(true);
    setPage((page) => page + 1);
    const newUsers = await getAllUsers(page);
    const pageSize = 10 + Math.pow(page, 2);

    if (newUsers && newUsers.length < pageSize) {
      dispatch(setNextPageToken(false));
    }
    setLoading(false);
  }, [page, loading]);

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          loadMoreUsers();
        }
      },
      {
        root: scrollRef.current?.querySelector(
          "[data-radix-scroll-area-viewport]"
        ),
        threshold: 1.0,
      }
    );
    const loadMoreDiv = loadMoreRef.current;
    if (loadMoreDiv) observer.observe(loadMoreDiv);

    return () => {
      if (loadMoreDiv) observer.unobserve(loadMoreDiv);
    };
  }, [loadMoreUsers, usersList]);

  useEffect(() => {
    if (!usersList || usersList?.length === 0) {
      getAllUsers(page);
    }
  }, [loggedInUser]);

  // timeout for skeleton → 8 seconds
  useEffect(() => {
    const timer = setTimeout(() => {
      setShowSkeleton(false);
    }, 8000);

    return () => clearTimeout(timer);
  }, []);

  return (
    <div className="mt-4">
      <h1 className="text-3xl font-semibold">People</h1>
      <div className="py-8">
        <ScrollArea
          className="w-full min-h-[10rem] max-h-[40rem] rounded-md border-black shadow-none"
          ref={scrollRef}
        >
          <div className="flex gap-4 flex-wrap p-5">
            {usersList && usersList?.length > 0 ? (
              usersList.map((item, index) => (
                <CustomerCard key={index} user={item} />
              ))
            ) : showSkeleton ? (
              // skeleton while waiting
              [1, 2, 3, 4, 5, 6, 7, 8].map((_, index) => (
                <div key={index} className="flex flex-col space-y-3">
                  <Skeleton className="h-[200px] w-[320px] rounded-xl p-3">
                    <Skeleton className="h-11 w-11 rounded-full" />
                  </Skeleton>
                  <div className="space-y-2">
                    <Skeleton className="h-4 w-[250px]" />
                    <Skeleton className="h-4 w-[200px]" />
                  </div>
                </div>
              ))
            ) : (
              // fallback if still empty after timeout
              <div className="flex flex-col items-center justify-center w-full py-10 text-center text-gray-500">
                <span className="text-5xl mb-3">🧑‍🤝‍🧑</span>
                <h2 className="text-lg font-medium">No travel buddies yet</h2>
                <p className="text-sm text-gray-400 mt-1">
                  Be the first one to explore and make connections!
                </p>
              </div>
            )}
          </div>
          <div ref={loadMoreRef} className="h-10">
            {/* {loading &&
              userNextPageToken &&
              [1, 2, 3, 4].map((_, index) => (
                <div key={index} className="flex flex-col space-y-3 mb-2">
                  <Skeleton className="h-[200px] w-[320px] rounded-xl p-3">
                    <Skeleton className="h-11 w-11 rounded-full" />
                  </Skeleton>
                  <div className="space-y-2">
                    <Skeleton className="h-4 w-[250px]" />
                    <Skeleton className="h-4 w-[200px]" />
                  </div>
                </div>
              ))} */}
            {!userNextPageToken && usersList?.length > 0 && (
              <div className="text-center">
                <h1 className="text-sm text-gray-400">No more users</h1>
              </div>
            )}
          </div>
        </ScrollArea>
      </div>
    </div>
  );
};
