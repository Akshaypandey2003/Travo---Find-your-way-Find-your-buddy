/* eslint-disable no-unused-vars */
import { shallowEqual, useSelector } from "react-redux";
import useAuth from "../../CustomHooks/useAuth";
import { useForm } from "react-hook-form";
import { Button } from "@/components/ui/button";
import { DialogClose } from "@/components/ui/dialog";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faEdit } from "@fortawesome/free-solid-svg-icons";

import {
  Select,
  SelectTrigger,
  SelectItem,
  SelectContent,
  SelectValue,
} from "@/components/ui/select";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { CrossIcon } from "lucide-react";
import { useState } from "react";
import {
  DEFAULT_FEMALE_PIC,
  DEFAULT_MALE_PIC,
} from "../../Constants/constants";

const ProfileUpdateForm = () => {
  const user = useSelector((store) => store.auth.user, shallowEqual);
  const { updateUser } = useAuth();
  const [preview, setPreview] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);

  const preferences = [
    "mountains",
    "beaches",
    "adventure",
    "food",
    "sports",
    "music",
  ];
  const form = useForm({
    defaultValues: {
      name: user?.name || "",
      email: user?.email || "",
      phone: user?.phone || "",
      profilePic: user?.profilePic || "",
      country: user?.country || "",
      state: user?.state || "",
      city: user?.city || "",
      bio: user?.bio || "",
      preferences: user.preferences || [
        "Eg.",
        "Mountains",
        "Beaches",
        "Adeventure",
      ],
    },
  });

  const onSubmit = (data) => {
    const finalData = {
      ...data,
      profilePic: selectedFile, // Attach the file here
    };
    console.log("User to be Updated:", finalData);
    updateUser(finalData, user?.userId); // Call the updateUser function with form
  };

  const handlePreferenceChange = (item) => {
    const currentPref = form.getValues("preferences") || [];
    const updatedPref = currentPref.includes(item)
      ? currentPref.filter((pref) => pref !== item)
      : [...currentPref, item]; // Spread the array properly

    form.setValue("preferences", updatedPref);
  };

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
      <div className="flex mt-4">
        <div
          className={
            user?.profilePic
              ? "border-4 border-orange-600 h-[10rem] w-[10rem] rounded-full object-cover overflow-hidden m-auto"
              : "border-4 border-orange-600 bg-orange-200 h-[10rem] w-[10rem] rounded-full object-cover overflow-hidden m-auto"
          }
        >
          <img
            src={
              preview
                ? preview
                : user?.profilePic
                ? user?.profilePic
                : user?.gender?.toLowerCase() === "male"
                ? DEFAULT_MALE_PIC
                : DEFAULT_FEMALE_PIC
            }
            alt="profile"
          />
        </div>
        <div>
          <Button
            variant="outline"
            className="border rounded-full border-orange-500"
          >
            <FontAwesomeIcon icon={faEdit} className="text-orange-600" />
          </Button>
        </div>
      </div>
      <Form {...form}>
        <form className="space-y-3" onSubmit={form.handleSubmit(onSubmit)}>
          <FormField
            control={form.control}
            name="profilePic"
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
          <FormField
            control={form.control}
            name="name"
            render={({ field }) => (
              <FormItem>
                <Label>Name</Label>
                <FormControl>
                  <Input {...field} type="text" />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="email"
            render={({ field }) => (
              <FormItem>
                <Label>Email</Label>
                <FormControl>
                  <Input {...field} type="email" />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="phone"
            render={({ field }) => (
              <FormItem>
                <Label>Phone</Label>
                <FormControl>
                  <Input {...field} type="numeric" />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="bio"
            render={({ field }) => (
              <FormItem>
                <Label>Bio</Label>
                <FormControl>
                  <Input {...field} type="text" />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <div className="flex gap-2 items-center">
            <FormField
              control={form.control}
              name="country"
              render={({ field }) => (
                <FormItem className="w-36">
                  <FormControl>
                    <Select
                      value={field.value}
                      onValueChange={(value) => field.onChange(value)}
                    >
                      <SelectTrigger>
                        <SelectValue placeholder="Country" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="india">India</SelectItem>
                        <SelectItem value="america">America</SelectItem>
                        <SelectItem value="australia">Australia</SelectItem>
                        <SelectItem value="britain">Britain</SelectItem>
                        <SelectItem value="russia">Russia</SelectItem>
                        <SelectItem value="china">China</SelectItem>
                        <SelectItem value="ukrain">Ukrain</SelectItem>
                      </SelectContent>
                    </Select>
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="state"
              render={({ field }) => (
                <FormItem className="w-36">
                  <FormControl>
                    <Select
                      value={field.value}
                      onValueChange={(value) => field.onChange(value)}
                    >
                      <SelectTrigger>
                        <SelectValue placeholder="State" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="india">Madhya Pradesh</SelectItem>
                        <SelectItem value="america">America</SelectItem>
                        <SelectItem value="australia">Australia</SelectItem>
                        <SelectItem value="britain">Britain</SelectItem>
                        <SelectItem value="russia">Russia</SelectItem>
                        <SelectItem value="china">China</SelectItem>
                        <SelectItem value="ukrain">Ukrain</SelectItem>
                      </SelectContent>
                    </Select>
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="city"
              render={({ field }) => (
                <FormItem className="w-36">
                  <FormControl>
                    <Select
                      value={field.value}
                      onValueChange={(value) => field.onChange(value)}
                    >
                      <SelectTrigger>
                        <SelectValue placeholder="city" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="india">Bangalore</SelectItem>
                        <SelectItem value="america">America</SelectItem>
                        <SelectItem value="australia">Australia</SelectItem>
                        <SelectItem value="britain">Britain</SelectItem>
                        <SelectItem value="russia">Russia</SelectItem>
                        <SelectItem value="china">China</SelectItem>
                        <SelectItem value="ukrain">Ukrain</SelectItem>
                      </SelectContent>
                    </Select>
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </div>
          <FormField
            control={form.control}
            name="preferences"
            render={({ field }) => (
              <FormItem>
                <FormControl>
                  <Select
                    onValueChange={(value) => handlePreferenceChange(value)}
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="Preferences" />
                    </SelectTrigger>
                    <SelectContent>
                      {preferences.map((pref, index) => (
                        <SelectItem key={index} value={pref}>
                          {pref.charAt(0).toUpperCase() + pref.substring(1)}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </FormControl>
                <div className="flex flex-wrap gap-2 items-center">
                  {field.value?.map((item, index) => (
                    <div
                      onClick={() => handlePreferenceChange(item)}
                      key={index}
                      className="cursor-pointer flex gap-3 items-center rounded-full border px-2 py-1 "
                    >
                      <span className="text-gray-500">{item}</span>

                      <CrossIcon className="h-3 w-3"></CrossIcon>
                    </div>
                  ))}
                </div>
                <FormMessage />
              </FormItem>
            )}
          />
          <DialogClose className="mt-4 ">
            <div>
              <Button
                type="submit"
                className="w-full bg-orange-700 hover:bg-orange-600 hover:border-none"
              >
                Save Changes
              </Button>
            </div>
          </DialogClose>
        </form>
      </Form>
    </div>
  );
};

export default ProfileUpdateForm;
