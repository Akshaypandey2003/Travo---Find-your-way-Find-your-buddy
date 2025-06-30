import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import ProfileUpdateForm from "./ProfileUpdateForm";
import { ScrollArea } from "@/components/ui/scroll-area";
import { shallowEqual, useSelector } from "react-redux";

export const EditProfile = () => {
  const profileStatus = useSelector(
    (store) => store.auth.profileStatus,
    shallowEqual
  );
  return (
    <Dialog>
      <DialogTrigger
        aschild="true"
        className="py-1 px-2 bg-orange-700 hover:bg-orange-600 border-none font-semibold text-white hover:text-white"
      >
        {profileStatus < 100 ? "Complete Profile" : "Edit Profile"}
      </DialogTrigger>

      <DialogContent className="sm:max-w-[550px]">
        <ScrollArea className="h-96 rounded-md">
          <DialogHeader>
            <DialogTitle>
              {profileStatus < 100 ? "Complete Profile" : "Edit Profile"}
            </DialogTitle>
            <DialogDescription>
              Make changes to your profile here. Click save when you are done.
            </DialogDescription>
          </DialogHeader>

          <div className="m-4">
            <ProfileUpdateForm />
          </div>
        </ScrollArea>
      </DialogContent>
    </Dialog>
  );
};
export default EditProfile;
