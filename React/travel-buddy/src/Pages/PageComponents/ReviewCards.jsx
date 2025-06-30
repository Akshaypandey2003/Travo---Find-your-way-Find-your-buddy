/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import * as React from "react";
import { Badge } from "@/components/ui/badge";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faStar } from "@fortawesome/free-solid-svg-icons";

export const ReviewCards = ({ item }) => {
  
  const [imageUrl, setImageUrl] = React.useState("");
  // console.log("Profile url: ",item?.profile_photo_url,typeof item?.profile_photo_url)
    const formatTimestamp = (timestamp) => {
        const date = new Date(timestamp * 1000); // Convert seconds to milliseconds
        return date.toLocaleString(); // Returns a formatted date-time string
    };

    React.useEffect(() => {
      if (item?.profile_photo_url) {
        setImageUrl(item?.profile_photo_url);
      }
    }, [item]);

  return (
    <div className="my-4">
      <div className="flex items-center gap-4">
        <div className="w-[5rem] h-[5rem] rounded-full border object-cover overflow-hidden">
        {/* <img src={imageUrl} alt="profile image" crossOrigin="anonymous" /> */}
        <img src="/AkshayImg.JPG" alt="profile image" />

        </div>
        <h1 className="text-lg font-semibold">
          {item?.author_name[0].toUpperCase() + item?.author_name.substring(1)}
        </h1>
      </div>
      <p>{item?.text}</p>
      <div className=" w-full flex items-center gap-4 py-2">
        <div>
          <Badge variant="outline" className="border border-orange-500">
            <FontAwesomeIcon icon={faStar} className="text-orange-400" />
            <span className="mx-2">{item?.rating}</span>
          </Badge>
        </div>
        <span className="text-gray-500 text-xs">{item?.relative_time_description[0].toUpperCase() + item?.relative_time_description.substring(1)}</span>
        <span className="text-gray-500 text-xs">{formatTimestamp(item?.time)}</span>
      </div>
    </div>
  );
};
export default ReviewCards;
