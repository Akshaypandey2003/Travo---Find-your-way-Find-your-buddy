/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
import React, { useEffect, useState } from "react";
import {
  differenceInSeconds,
  differenceInMinutes,
  differenceInHours,
  differenceInDays,
  differenceInWeeks,
  differenceInMonths,
} from "date-fns";

const formatTimeAgo = (date) => {
  const postedDate = new Date(date);
  const now = new Date();

  const secondsDiff = differenceInSeconds(now, postedDate);
  const minutesDiff = differenceInMinutes(now, postedDate);
  const hoursDiff = differenceInHours(now, postedDate);
  const daysDiff = differenceInDays(now, postedDate);
  const weeksDiff = differenceInWeeks(now, postedDate);
  const monthsDiff = differenceInMonths(now, postedDate);

  if (secondsDiff < 60) {
    return `${secondsDiff} ${secondsDiff === 1 ? "second" : "seconds"} ago`;
  } else if (minutesDiff < 60) {
    return `${minutesDiff} ${minutesDiff === 1 ? "minute" : "minutes"} ago`;
  } else if (hoursDiff < 24) {
    return `${hoursDiff} ${hoursDiff === 1 ? "hour" : "hours"} ago`;
  } else if (daysDiff < 7) {
    return `${daysDiff} ${daysDiff === 1 ? "day" : "days"} ago`;
  } else if (weeksDiff < 4) {
    return `${weeksDiff} ${weeksDiff === 1 ? "week" : "weeks"} ago`;
  } else {
    return `${monthsDiff} ${monthsDiff === 1 ? "month" : "months"} ago`;
  }
};

const TimeAgo = ({ timestamp }) => {
  const [timeAgo, setTimeAgo] = useState(formatTimeAgo(timestamp));

  useEffect(() => {
    const interval = setInterval(() => {
      setTimeAgo(formatTimeAgo(timestamp));
    }, 10000); // Update every 10 seconds

    return () => clearInterval(interval); // Cleanup on unmount
  }, [timestamp]);

  return <span className="text-xs">{timeAgo}</span>;
};

export default TimeAgo;
