import {
  differenceInHours,
  differenceInDays,
  differenceInWeeks,
  differenceInMonths,
} from "date-fns";


export const useHelperMethods = ()=>{
    const formatTimeAgo = (date) => {
        const postedDate = new Date(date);
        const now = new Date();
    
        const hoursDiff = differenceInHours(now, postedDate);
        const daysDiff = differenceInDays(now, postedDate);
        const weeksDiff = differenceInWeeks(now, postedDate);
        const monthsDiff = differenceInMonths(now, postedDate);
    
        if (hoursDiff < 24 && daysDiff === 0) {
          return `${hoursDiff} ${hoursDiff === 1 ? "hour" : "hours"} ago`;
        } else if (daysDiff < 7) {
          return `${daysDiff} ${daysDiff === 1 ? "day" : "days"} ago`;
        } else if (weeksDiff < 4) {
          return `${weeksDiff} ${weeksDiff === 1 ? "week" : "weeks"} ago`;
        } else {
          return `${monthsDiff} ${monthsDiff === 1 ? "month" : "months"} ago`;
        }
      };

      return {formatTimeAgo};
}
export default useHelperMethods;