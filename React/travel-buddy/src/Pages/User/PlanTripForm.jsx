/* eslint-disable react/prop-types */
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
import { Country, State, City } from "country-state-city";

const PlanTripForm = (
  { trip } // Accept trip prop for editing existing trip
) => {
  const user = useSelector((store) => store.auth.user, shallowEqual);
  const { createTrip, updateTrip } = useTrip();
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
      tripName: trip?.tripName || "",
      tripCountry: trip?.tripCountry || "",
      tripState: trip?.tripState || "",
      tripCity: trip?.tripCity || "",
      tripStartDate: trip?.tripStartDate || "",
      tripDuration: trip?.tripDuration || "",
      tripDescription: trip?.tripDescription || "",
      memberSize: trip?.memberSize || "",
      isPrivateTrip: trip?.isPrivateTrip || "",
      tripCategory: trip?.tripCategory || "",
      tripTags: trip?.tripTags || [],
    },
  });

  const onSubmit = (data) => {
    const tripData = {
      ...data,
      ...(trip?.tripId && { tripId: trip.tripId }), // Add tripId only if updating
    };
    console.log("Planned Trip is:", tripData);
    trip ? updateTrip(tripData) : createTrip(tripData);
  };

  const handleTagsChange = (item) => {
    const currentTags = form.getValues("tripTags") || [];
    const updatedTags = currentTags.includes(item)
      ? currentTags.filter((pref) => pref !== item)
      : [...currentTags, item]; // Spread the array properly

    form.setValue("tripTags", updatedTags);
  };
  const states = State.getStatesOfCountry(form.watch("tripCountry"));
  // Get cities of the selected state
  const cities = form.watch("tripState")
    ? City.getCitiesOfState(
        form.getValues("tripCountry"),
        form.getValues("tripState")
      )
    : [];
  const tripCat = form.watch("tripCategory");
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
                        <SelectValue placeholder="Select Country" />
                      </SelectTrigger>
                      <SelectContent>
                        {Country.getAllCountries().map((country) => (
                          <SelectItem
                            key={country.isoCode}
                            value={country.isoCode}
                          >
                            {country.name}
                          </SelectItem>
                        ))}
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
                        {states.map((state) => (
                          <SelectItem key={state.isoCode} value={state.isoCode}>
                            {state.name}
                          </SelectItem>
                        ))}
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
                        {cities.map((city) => (
                          <SelectItem key={city.name} value={city.name}>
                            {city.name}
                          </SelectItem>
                        ))}
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
              name="tripStartDate"
              render={({ field }) => {
                // Calculate tomorrow's date in yyyy-MM-dd format
                const today = new Date();
                const tomorrow = new Date(today);
                tomorrow.setDate(today.getDate() + 1);
                const minDate = tomorrow.toISOString().split("T")[0];

                return (
                  <FormItem className="relative">
                    <FormControl>
                      <Input
                        type="date"
                        value={field.value}
                        onChange={(e) => field.onChange(e.target.value)}
                        min={minDate} // disables past & current dates
                        className="w-full"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                );
              }}
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
                        <SelectItem value="2-3 days">2-3 days</SelectItem>
                        <SelectItem value="3-5 Days">3-5 Days</SelectItem>
                        <SelectItem value="5-10 Days">5-10 Days</SelectItem>
                        <SelectItem value="10-15 days">10-15 days</SelectItem>
                        <SelectItem value="15+ Days">15+ Days</SelectItem>
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
            {tripCat === "Group" && (
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
                          <SelectItem value="2-5 Members">
                            2-5 Members
                          </SelectItem>
                          <SelectItem value="5-10 Members">
                            5-10 Members
                          </SelectItem>
                          <SelectItem value="10-15 Members">
                            10-15 Members
                          </SelectItem>
                          <SelectItem value="15+ Members">
                            15+ Members
                          </SelectItem>
                        </SelectContent>
                      </Select>
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            )}
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
                    <div className="flex space-x-4">
                      <div className="space-y-1">
                        <FontAwesomeIcon
                          icon={faEyeSlash}
                          className="hover:cursor-pointer"
                        />
                      </div>
                      <h1 className="text-sm font-semibold">Privacy</h1>
                    </div>
                    <p className="text-sm">
                      It will be a private trip and only the close friends will
                      be notified.
                    </p>
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
