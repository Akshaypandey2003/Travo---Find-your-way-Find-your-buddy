/* eslint-disable no-unused-vars */
import { shallowEqual, useSelector } from "react-redux";
import { useForm } from "react-hook-form";
import { Button } from "@/components/ui/button";
import { DialogClose } from "@/components/ui/dialog";

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
import { Switch } from "@/components/ui/switch";
import { faEyeSlash, faXmark } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Textarea } from "@/components/ui/textarea";
import {
  HoverCard,
  HoverCardContent,
  HoverCardTrigger,
} from "@/components/ui/hover-card";
import { useState } from "react";
import useTrip from "../../CustomHooks/useTrip";
import DatePicker from "react-datepicker";

const PlanTripForm = () => {
  const user = useSelector((store) => store.auth.user, shallowEqual);
  const {createTrip} = useTrip();
  // const [open, setOpen] = useState(false);
  //   const { updateUser } = useAuth();
  //   const [preview, setPreview] = useState(null);
  //   const [selectedFile, setSelectedFile] = useState(null);

  const tripTags = [
    "mountains",
    "beaches",
    "adventure",
    "food",
    "sports",
    "music",
  ];
  const form = useForm({
    defaultValues: {
      createdBy: user?.userId,
      tripName: "",
      tripCountry: "",
      tripState: "",
      tripCity: "",
      tripDate: "",
      tripDuration: "",
      tripDescription: "",
      memberSize: "",
      isPrivateTrip: "",
      tripCategory: "",
      tripTags: [],
    },
  });

  const onSubmit = (data) => {
    const finalData = {
      ...data,
      //   profilePic: selectedFile,  // Attach the file here
    };
    console.log("Planned Trip is:", finalData);
    createTrip(data);
  };

  const handleTagsChange = (item) => {
    const currentTags = form.getValues("tripTags") || [];
    const updatedTags = currentTags.includes(item)
      ? currentTags.filter((pref) => pref !== item)
      : [...currentTags, item]; // Spread the array properly

    form.setValue("tripTags", updatedTags);
  };
  const [startDate, setStartDate] = useState(new Date());

  return (
    <div>
      <Form {...form}>
        <form className="space-y-3" onSubmit={form.handleSubmit(onSubmit)}>
          <FormField
            control={form.control}
            name="tripName"
            render={({ field }) => (
              <FormItem>
                <Label>Trip Name</Label>
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
              name="tripCountry"
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
              name="tripState"
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
                        <SelectItem value="Madhya Pradesh">Madhya Pradesh</SelectItem>
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
              name="tripCity"
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
                        <SelectItem value="Bangalore">Bangalore</SelectItem>
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
            name="tripTags"
            render={({ field }) => (
              <FormItem>
                <FormControl>
                  <Select onValueChange={(value) => handleTagsChange(value)}>
                    <SelectTrigger>
                      <SelectValue placeholder="Tags" />
                    </SelectTrigger>
                    <SelectContent>
                      {tripTags.map((pref, index) => (
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
                      onClick={() => handleTagsChange(item)}
                      key={index}
                      className="cursor-pointer flex gap-3 items-center rounded-full border px-2 py-1 "
                    >
                      <span className="text-gray-500">{item}</span>

                      <FontAwesomeIcon
                        icon={faXmark}
                        className="hover:cursor-pointer"
                      />
                    </div>
                  ))}
                </div>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="tripDescription"
            render={({ field }) => (
              <FormItem>
                <Label>Description</Label>
                <FormControl>
                  {/* <Input {...field} type="text" /> */}
                  <Textarea {...field} placeholder="Description" />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <div className="flex gap-2 items-center relative">
            <FormField
              control={form.control}
              name="tripDate"
              render={({ field }) => (
                <FormItem className="relative">
                  {/* <FormLabel>Trip Date</FormLabel> */}
                  <FormControl>
                    <Input
                      type="date"
                      value={field.value}
                      onChange={(e) => field.onChange(e.target.value)}
                      className="w-full"
                    />
                    {/* <DatePicker selected={startDate} onChange={(date) => setStartDate(date)} /> */}
                  </FormControl>
                  {/* <Popover open={open} onOpenChange={setOpen} modal={false}>
                    <PopoverTrigger asChild>
                      <Button
                        variant={"outline"}
                        className={cn(
                          "w-[240px] pl-3 text-left font-normal",
                          !field.value && "text-muted-foreground"
                        )}
                      >
                        {field.value ? (
                          format(field.value, "PPP")
                        ) : (
                          <span>Start Date</span>
                        )}
                        <CalendarIcon className="ml-auto h-4 w-4 opacity-50" />
                      </Button>
                    </PopoverTrigger>

                    <PopoverContent
                      align="start"
                      sideOffset={8}
                      className="w-auto p-0"
                      avoidCollisions={false}
                      onOpenAutoFocus={(e) => e.preventDefault()}
                    >
                      <Calendar
                        mode="single"
                        selected={field.value}
                        onSelect={(date) => {
                          field.onChange(date);
                          setOpen(false);
                        }}
                        disabled={(date) => {
                          const today = new Date();
                          today.setHours(0, 0, 0, 0);
                          return date < today;
                        }}
                        initialFocus
                      />
                    </PopoverContent>
                  </Popover> */}
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="tripDuration"
              render={({ field }) => (
                <FormItem className="w-36">
                  <FormControl>
                    <Select
                      value={field.value}
                      onValueChange={(value) => field.onChange(value)}
                    >
                      <SelectTrigger>
                        <SelectValue placeholder="Duration" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="india">2-3 days</SelectItem>
                        <SelectItem value="america">3-5 Days</SelectItem>
                        <SelectItem value="australia">5-10 Days</SelectItem>
                        <SelectItem value="britain">10-15 days</SelectItem>
                        <SelectItem value="russia">15+ Days</SelectItem>
                      </SelectContent>
                    </Select>
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </div>

          <div className="flex gap-2 items-center">
            <FormField
              control={form.control}
              name="tripCategory"
              render={({ field }) => (
                <FormItem className="">
                  <FormControl>
                    <Select
                      value={field.value}
                      onValueChange={(value) => field.onChange(value)}
                    >
                      <SelectTrigger>
                        <SelectValue placeholder="Select Trip Category" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="Solo">Solo</SelectItem>
                        <SelectItem value="Group">Group</SelectItem>
                        <SelectItem value="Family">Family</SelectItem>
                      </SelectContent>
                    </Select>
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="memberSize"
              render={({ field }) => (
                <FormItem className="">
                  <FormControl>
                    <Select
                      value={field.value}
                      onValueChange={(value) => field.onChange(value)}
                    >
                      <SelectTrigger>
                        <SelectValue placeholder="Member Size" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="2-5 Members">2-5 Members</SelectItem>
                        <SelectItem value="5-10 Members">5-10 Members</SelectItem>
                        <SelectItem value="10-15 Members">10-15 Members</SelectItem>
                        <SelectItem value="15+ Members">15+ Members</SelectItem>
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
            name="isPrivateTrip"
            render={({ field }) => (
              <FormItem>
                <FormControl>
                  <div className="flex items-center space-x-2">
                    <Switch
                      id="trip-mode"
                      checked={field.value}
                      onCheckedChange={field.onChange}
                    />
                    <Label htmlFor="trip-mode">Private Trip</Label>
                  </div>
                </FormControl>
                <HoverCard>
                  <HoverCardTrigger asChild>
                    {/* <Button variant="link" className="border-none p-0 py-0">@nextjs</Button> */}
                    <span className="text-xs text-blue-600  cursor-pointer font-light">
                      Know More
                    </span>
                  </HoverCardTrigger>
                  <HoverCardContent className="p-2">
                    <div className="flex justify-between space-x-4">
                      <div className="space-y-1">
                        <FontAwesomeIcon
                          icon={faEyeSlash}
                          className="hover:cursor-pointer"
                        />
                        <p className="text-sm">
                          It will be a private trip and only the close friends
                          will be informed.
                        </p>
                        <h1 className="text-xs text-blue-600 cursor-pointer font-light">
                          Select Close Friends
                        </h1>
                      </div>
                    </div>
                  </HoverCardContent>
                </HoverCard>
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
                Save
              </Button>
            </div>
          </DialogClose>
        </form>
      </Form>
    </div>
  );
};

export default PlanTripForm;
