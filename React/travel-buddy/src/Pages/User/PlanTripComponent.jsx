/* eslint-disable no-unused-vars */
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { ScrollArea } from "@/components/ui/scroll-area";
import { shallowEqual, useSelector } from "react-redux";
import PlanTripForm from "./PlanTripForm";

export const PlanTripComponent = () => {
  const profileStatus = useSelector(
    (store) => store.auth.profileStatus,
    shallowEqual
  );
  return (
    <>
    <Dialog modal={true}>
      <DialogTrigger
        aschild="true"
        className="py-1 px-2 border-2 text-orange-700 font-semibold border-orange-700 hover:border-orange-500  hover:text-orange-500"
      >
        Plan a trip
      </DialogTrigger>

      <DialogContent className="sm:max-w-[550px]">
        <ScrollArea className="h-96 rounded-md">
          <DialogHeader>
            <DialogTitle>
              <h1>Plan Trip</h1>
            </DialogTitle>
            <DialogDescription>
              <h1>Plan a trip to your dream destinations and let us find a match for your trip.</h1>
            </DialogDescription>
          </DialogHeader>
          <div className="m-4">
            <PlanTripForm/>
          </div>
        </ScrollArea>
      </DialogContent>
    </Dialog>
    </>
  );
};
export default PlanTripComponent;
