import { Card } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";

const TripDescCardSkeleton = () => {
  return (
    <div className="relative group">
      <div className="absolute inset-0 rounded-2xl p-[2px] bg-gradient-to-r from-orange-400 via-red-400 to-yellow-400 opacity-0 group-hover:opacity-100 transition-opacity duration-300 blur-md"></div>
      <Card className="relative z-10 p-4 bg-white shadow-lg rounded-2xl border border-gray-200 group-hover:shadow-md group-hover:shadow-orange-200 transition duration-300 ease-in-out transform group-hover:scale-105">
        <div className="space-y-3">
          {/* Trip Name */}
          <Skeleton className="h-6 w-2/3 rounded" />

          {/* Location */}
          <Skeleton className="h-4 w-1/2 rounded" />

          {/* Info line icons: date, members, budget, type */}
          <div className="flex flex-wrap gap-4 mt-2">
            <Skeleton className="h-4 w-24 rounded" />
            <Skeleton className="h-4 w-24 rounded" />
            <Skeleton className="h-4 w-20 rounded" />
            <Skeleton className="h-4 w-24 rounded" />
          </div>

          {/* Description */}
          <Skeleton className="h-4 w-full rounded" />
          <Skeleton className="h-4 w-11/12 rounded" />
          <Skeleton className="h-4 w-2/3 rounded" />

          {/* Tags */}
          <div className="flex flex-wrap gap-2 mt-3">
            <Skeleton className="h-5 w-16 rounded-full" />
            <Skeleton className="h-5 w-20 rounded-full" />
            <Skeleton className="h-5 w-12 rounded-full" />
          </div>
        </div>
      </Card>
    </div>
  );
};

export default TripDescCardSkeleton;
