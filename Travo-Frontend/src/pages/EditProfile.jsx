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
    shallowEqual,
  );
  return (
    <Dialog>
      <DialogTrigger
        aschild="true"
        className="flex-1 lg:flex-none bg-primary hover:bg-primary-hover text-white px-8 py-3 rounded-xl font-bold shadow-xl shadow-primary/10 transition-all flex items-center justify-center gap-2"
      >
        {profileStatus < 100 ? "Complete Profile" : "Edit Profile"}
      </DialogTrigger>

      <DialogContent className="sm:max-w-[550px] bg-black/60 backdrop-blur-xl border border-white/10 rounded-3xl shadow-2xl shadow-black/40">
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
