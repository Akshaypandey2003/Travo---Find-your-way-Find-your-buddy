/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import { shallowEqual, useSelector } from "react-redux";
import { useForm } from "react-hook-form";
import { Button } from "@/components/ui/button";
import { DialogClose } from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { useState } from "react";
import { ScrollArea } from "@/components/ui/scroll-area";
import UserCard from "./UserCard";
import useChat from "../../../CustomHooks/useChat";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUserGroup } from "@fortawesome/free-solid-svg-icons";

const CreateGroupForm = ({setOpen}) => {
  const user = useSelector((store) => store.auth.user, shallowEqual);
  const usersList = useSelector((store) => store.auth.usersList, shallowEqual);
  const { createGroup } = useChat();
  const [selectedFile, setSelectedFile] = useState(null);

  const form = useForm({
    defaultValues: {
      groupAdmin: [user?.userId],
      groupName: "",
      groupChat: true,
      groupDescription: "",
      groupImageUrl: "",
      participants: "",
    },
  });
  const [formErrors, setFormErrors] = useState({
    groupName: "",
    participants: "",
  });

  const [preview, setPreview] = useState(null);
  const [selectedParticipants, setSelectedParticipants] = useState([
    user?.userId,
  ]);
  const onSubmit = (data) => {
    let hasError = false;
    const errors = {
      groupName: "",
      participants: "",
    };

    if (!data.groupName.trim()) {
      errors.groupName = "Group name is required !";
      hasError = true;
    }

    if (selectedParticipants.length < 3) {
      // 1 for current user + 2 friends = 3
      errors.participants = "Please select at least 2 friends !";
      hasError = true;
    }

    setFormErrors(errors);

    if (hasError) return;

    const finalData = {
      ...data,
      participants: selectedParticipants,
      groupImageUrl: selectedFile,
    };

    console.log("Group details are as follows:", finalData);
    createGroup(finalData);
    setOpen(false);
  };

  // const [selectedFile, setSelectedFile] = useState(null);
  const handleProfilePicChange = (e) => {
    const file = e.target.files[0];

    if (file) {
      // const reader = new FileReader();
      // reader.onloadend = () => {
      //   setPreview(reader.result);
      // };
      // reader.readAsDataURL(file);
      setSelectedFile(file);
      setPreview(URL.createObjectURL(file));
    }
  };
  return (
    <div>
      {preview ? (
        <div
          className={
            "border-4 border-orange-600 bg-orange-200 h-[7rem] w-[7rem] rounded-full object-cover overflow-hidden m-auto mb-2"
          }
        >
          <img
            src={preview}
            // alt="profile"
          />
        </div>
      ) : (
        <div className="mb-2  text-center">
          <FontAwesomeIcon
            icon={faUserGroup}
            className="text-orange-300 h-14"
            size=""
          />
        </div>
      )}

      <Form {...form}>
        <form className="space-y-3" onSubmit={form.handleSubmit(onSubmit)}>
          <FormField
            control={form.control}
            // rules={{ required: "Group name is require !" }}
            name="groupName"
            render={({ field }) => (
              <FormItem>
                <FormControl>
                  <Input {...field} type="text" placeholder="Group name..." />
                </FormControl>
                <FormMessage />
                {formErrors.groupName && (
                  <p className="text-xs text-red-600">{formErrors.groupName}</p>
                )}
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="groupDescription"
            render={({ field }) => (
              <FormItem>
                <FormControl>
                  <Input
                    {...field}
                    type="text"
                    placeholder="Group description..."
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="groupImageUrl"
            render={({ field }) => (
              <FormItem>
                <Label>Profile Picture</Label>
                <FormControl>
                  <Input
                    type="file"
                    accept="image/*"
                    onChange={(e) => {
                      handleProfilePicChange(e);
                      field.onChange(e);
                    }}
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <div className="px-2">
            <Label>Select friends</Label>
            {formErrors.participants && (
              <p className="text-xs text-red-600">
                {formErrors.participants}
              </p>
            )}
          </div>

          <ScrollArea className="h-96 rounded-md">
            <div>
              {usersList?.map((user, indx) => {
                const isChecked = selectedParticipants.includes(user.userId);
                return (
                  <div
                    key={indx}
                    className="m-2 flex justify-between items-center px-2"
                  >
                    <UserCard user={user} />
                    <FormField
                      control={form.control}
                      name=""
                      render={({ field }) => (
                        <FormItem>
                          <FormControl>
                            <Input
                              {...field}
                              type="checkbox"
                              checked={isChecked}
                              onChange={() => {
                                if (isChecked) {
                                  setSelectedParticipants((prev) =>
                                    prev.filter((id) => id !== user.userId)
                                  );
                                } else {
                                  setSelectedParticipants((prev) => [
                                    ...prev,
                                    user.userId,
                                  ]);
                                }
                              }}
                            />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                  </div>
                );
              })}
            </div>
          </ScrollArea>
          {/* <DialogClose className="mt-4 "> */}
            <div>
              <Button
                type="submit"
                className="w-full bg-orange-700 hover:bg-orange-600 hover:border-none"
              >
                Save
              </Button>
            </div>
          {/* </DialogClose> */}
        </form>
      </Form>
    </div>
  );
};

export default CreateGroupForm;
