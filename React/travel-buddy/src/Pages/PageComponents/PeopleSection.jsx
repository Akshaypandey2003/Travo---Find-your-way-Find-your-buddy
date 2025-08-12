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
  const usersList = useSelector((store) => store.auth.usersList, shallowEqual);
  const loggedInUser = useSelector((store) => store.auth.user, shallowEqual);
  const [page, setPage] = useState(0);
  const loadMoreRef = useRef(null);
  const scrollRef = useRef(null);
  const [isFetchingMore, setIsFetchingMore] = useState(false);

  const userNextPageToken = useSelector(
    (store) => store.auth.nextPageToken,
    shallowEqual
  );

  const loadMoreUsers = useCallback(async () => {
    if (!userNextPageToken || loading) return; // prevent multiple calls

    setLoading(true);
    setPage((page) => page + 1);
    const newUsers = await getAllUsers(page);
    const pageSize = 10 + Math.pow(page, 2);

    if (newUsers && newUsers.length < pageSize) {
      dispatch(setNextPageToken(false));
    }
    setLoading(false);
  }, [page, loading, getAllUsers]);

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
  return (
    <>
      <div className="mt-4">
        <h1 className="text-3xl font-semibold">People</h1>
        <div className="py-8">
          <ScrollArea
            className=" w-full h-[40rem] rounded-md border-black shadow-none"
            ref={scrollRef}
          >
            <div className="flex gap-4 py-5 flex-wrap">
              {usersList && usersList?.length > 0
                ? usersList.map((item, index) => (
                    <CustomerCard key={index} user={item} />
                  ))
                : [1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((item, index) => (
                    <div key={index} className="flex flex-col space-y-3">
                      <Skeleton className="h-[200px] w-[320px] rounded-xl p-3">
                        <Skeleton className="h-11 w-11 rounded-full" />
                      </Skeleton>
                      <div className="space-y-2">
                        <Skeleton className="h-4 w-[250px]" />
                        <Skeleton className="h-4 w-[200px]" />
                      </div>
                    </div>
                  ))}
              {/*------------------------ Skeleton part on loading ----------------------- */}
              {loading &&
                [1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((item, index) => (
                  <div key={index} className="flex flex-col space-y-3">
                    <Skeleton className="h-[200px] w-[320px] rounded-xl p-3">
                      <Skeleton className="h-11 w-11 rounded-full" />
                    </Skeleton>
                    <div className="space-y-2">
                      <Skeleton className="h-4 w-[250px]" />
                      <Skeleton className="h-4 w-[200px]" />
                    </div>
                  </div>
                ))}
              {/* ------------------------------------------------------------------------- */}
            </div>
            <div ref={loadMoreRef} className="h-10">
              {loading && userNextPageToken
                ? [1, 2, 3, 4].map((item, index) => (
                    <div key={index} className="flex flex-col space-y-3 mb-2">
                      <Skeleton className="h-[200px] w-[320px] rounded-xl p-3">
                        <Skeleton className="h-11 w-11 rounded-full" />
                      </Skeleton>
                      <div className="space-y-2">
                        <Skeleton className="h-4 w-[250px]" />
                        <Skeleton className="h-4 w-[200px]" />
                      </div>
                    </div>
                  ))
                : !userNextPageToken &&
                  usersList?.length > 0 && (
                    <div className="text-center">
                      <h1 className="text-sm text-gray-400">No more users</h1>
                    </div>
                  )}
            </div>
          </ScrollArea>
        </div>
      </div>
    </>
  );
};
