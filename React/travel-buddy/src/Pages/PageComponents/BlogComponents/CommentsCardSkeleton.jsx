/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Card } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";

export const CommentsCardSkeleton = () => {
  return (
    <>
      <Card className="border-none shadow-none rounded-xl">
        <div className="flex items-center justify-between p-2 ">
          <div className="w-[80%] text-wrap flex  gap-2">
          
            <div>
                <Skeleton className="h-10 w-10 rounded-full" />
            </div>
            <div className="">
              <div className="flex gap-4 items-center mb-2">
                <Skeleton className="h-4 w-24" />
                <Skeleton className="h-2 w-10" />
              </div>
                <Skeleton className="h-2 w-96 my-1" />
                <Skeleton className="h-2 w-72" />
            </div>
          </div>
        </div>
      </Card>
      <div></div>
    </>
  );
};
export default CommentsCardSkeleton;
