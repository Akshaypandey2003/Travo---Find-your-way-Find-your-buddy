/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion";
import NotificationCard from "./NotificationCard";

export const NotificationAccordian = ({ notifications, type }) => {
  return (
    <Accordion type="single" collapsible defaultValue="item-1">
      <AccordionItem value="item-1" >
        <AccordionTrigger className=" text-orange-500 border-none outline-none focus:outline-none focus:ring-0 focus:ring-offset-0">{type}</AccordionTrigger>
        {notifications?.map((item,index) => (
          <AccordionContent key={index}>
            <NotificationCard notification={item} />
          </AccordionContent>
        ))}
      </AccordionItem>
    </Accordion>
  );
};
export default NotificationAccordian;
