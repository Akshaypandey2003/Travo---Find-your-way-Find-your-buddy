/* eslint-disable no-unused-vars */
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faMagnifyingGlass,
  faPaperPlane,
  faPeopleGroup,
  faUserGroup,
} from "@fortawesome/free-solid-svg-icons";
import { ScrollArea } from "@/components/ui/scroll-area";
import CreateGroupForm from "./CreateGroupForm";
import { useState } from "react";

export const CreateGroupComponent = () => {
  const [open, setOpen] = useState(false);

  return (
    <>
      <Dialog open={open} onOpenChange={setOpen}>
        <DialogTrigger
          asChild="true"
          className=" px-2 text-orange-700 font-semibold   hover:text-orange-500 border-none"
        >
          <FontAwesomeIcon icon={faUserGroup} className="" />
        </DialogTrigger>
        <DialogContent className="sm:max-w-[550px]">
          <ScrollArea className="h-96 rounded-md">
            <DialogHeader>
              <DialogTitle>
                <h1>Create New Group</h1>
              </DialogTitle>
              <DialogDescription>
                <h1>
                  Create a group to chat with your friends together
                </h1>
              </DialogDescription>
            </DialogHeader>
            <div className="m-4">
                <CreateGroupForm setOpen={setOpen} />
            </div>
          </ScrollArea>
        </DialogContent>
      </Dialog>
    </>
  );
};
export default CreateGroupComponent;
