/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faLocationDot,
  faUser,
  faHeart,
  faUserGroup,
  faBell,
  faEdit,
  faDeleteLeft,
  faTrash,
} from "@fortawesome/free-solid-svg-icons";
import TripsCard from "./TripsCard";
export const TripAccordian = ({ trip }) => {
  // console.log("Received trip details: ", trip);
  return (
    <Accordion type="single" collapsible defaultValue="item-1">
      <AccordionItem value="item-1">
        <AccordionTrigger className="  border-none outline-none focus:outline-none focus:ring-0 focus:ring-offset-0">
          <div className="flex items-center justify-between w-full">
            <div className="flex items-center">
              <FontAwesomeIcon
                icon={faLocationDot}
                className="mr-2 text-orange-600"
              />
              <div className="flex gap-1 items-center">
                <h1 className="font-semibold text-lg">{trip?.tripName} </h1>
                <h1 className="font-light">
                  {" "}
                  {trip?.tripCity}
                  {","}
                </h1>
                <h1 className="font-light"> {trip?.tripState}</h1>
                <h1 className="font-light"> {trip?.tripCountry}</h1>
              </div>
            </div>
           
            
          </div>
        </AccordionTrigger>
        <AccordionContent>
          <TripsCard trip={trip} />
        </AccordionContent>
      </AccordionItem>
    </Accordion>
  );
};
export default TripAccordian;
