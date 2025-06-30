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
import { Textarea } from "@/components/ui/textarea";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { ScrollArea } from "@/components/ui/scroll-area";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faEdit, faXmark } from "@fortawesome/free-solid-svg-icons";
import { useEffect, useState } from "react";
import useBlog from "../../../CustomHooks/useBlog";

export const CreateBlogForm = ({ formType, blog }) => {
  const user = useSelector((store) => store.auth.user, shallowEqual);
  const [selectedImages, setSelectedImages] = useState([]);
  const { postBlog, updateBlog } = useBlog();
  const category = [
    "mountains",
    "beaches",
    "adventure",
    "food",
    "sports",
    "music",
  ];

  const form = useForm({
    defaultValues: {
      blogAuthor: blog?.blogAuthor || user?.userId,
      blogTitle: blog?.blogTitle || "",
      blogContent: blog?.blogContent || "",
      blogCaption: blog?.blogCaption || "",
      blogImages: blog?.blogImages || [],
      blogCategory: blog?.blogCategory || "",
      cloudinaryImagePublicIds: blog?.cloudinaryImagePublicIds||[],
    },
  });

  const onSubmit = (data) => {
    const finalData = {
      ...data,
      blogImages: selectedImages, // You will likely upload these via FormData
    };
    console.log("Created blog is:", finalData);
    form.reset();
    setSelectedImages([]);
    formType === "create" ? postBlog(finalData) : updateBlog(finalData,blog?.blogId);
  };

  const resetForm = () => {
    form.reset();
    setSelectedImages([]);
  };

  const handleImageChange = (e) => {
    const files = Array.from(e.target.files);
    setSelectedImages((prev) => [...prev, ...files]);
    form.setValue("blogImages", [...selectedImages, ...files]);
  };

  const handleRemoveImage = (indexToRemove) => {
    const updated = selectedImages.filter(
      (_, index) => index !== indexToRemove
    );
    setSelectedImages(updated);
    form.setValue("blogImages", updated);
  };
  useEffect(() => {
    if (formType === "update" && blog?.blogImages) {
      setSelectedImages(blog.blogImages);
    }
  }, [formType, blog]);

  return (
    <Dialog modal={true}>
      <DialogTrigger
        asChild
        className="py-1 px-2 border-2 text-orange-700 font-semibold border-orange-700 hover:border-orange-500  hover:text-orange-500"
      >
        <Button variant="ghost">
          {formType === "create" ? (
            "Create Blog"
          ) : (
            <FontAwesomeIcon icon={faEdit} className="text-red-500" />
          )}
        </Button>
      </DialogTrigger>

      <DialogContent className="sm:max-w-[550px]">
        <ScrollArea className="h-96 rounded-md">
          <DialogHeader>
            <DialogTitle>
              {formType === "create" ? "Create a new blog" : "Update Blog"}
            </DialogTitle>
            <DialogDescription>
              Post a blog and let people know about your adventures.
            </DialogDescription>
          </DialogHeader>

          <div className="m-4">
            <Form {...form}>
              <form
                className="space-y-3"
                onSubmit={form.handleSubmit(onSubmit)}
              >
                <FormField
                  control={form.control}
                  name="blogTitle"
                  render={({ field }) => (
                    <FormItem>
                      <Label>Title</Label>
                      <FormControl>
                        <Input {...field} type="text" placeholder="Title" />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="blogContent"
                  render={({ field }) => (
                    <FormItem>
                      <Label>Content</Label>
                      <FormControl>
                        <Textarea
                          {...field}
                          placeholder="Share your story..."
                          rows={10}
                          required
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="blogCategory"
                  render={({ field }) => (
                    <FormItem>
                      <Label>Category</Label>
                      <FormControl>
                        <Select
                          onValueChange={field.onChange}
                          value={field.value}
                        >
                          <SelectTrigger>
                            <SelectValue placeholder="Category" />
                          </SelectTrigger>
                          <SelectContent>
                            {category.map((cat, index) => (
                              <SelectItem key={index} value={cat}>
                                {cat.charAt(0).toUpperCase() + cat.slice(1)}
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
                  name="blogCaption"
                  render={({ field }) => (
                    <FormItem>
                      <Label>Caption</Label>
                      <FormControl>
                        <Input {...field} type="text" placeholder="Caption" />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                {/* Image Upload + Preview */}
                <FormItem>
                  {/* {formType === "create" && ( */}
                    <>
                      <Label htmlFor="blogImages">Images</Label>
                      <FormControl>
                        <Input
                          type="file"
                          id="blogImages"
                          multiple
                          accept="image/*"
                          onChange={handleImageChange}
                        />
                      </FormControl>
                    </>
                  {/* )} */}

                  {selectedImages.length > 0 && (
                    <div className="flex flex-wrap gap-3 mt-2">
                      {selectedImages.map((img, index) => (
                        <div
                          key={index}
                          className="relative w-20 h-20 rounded overflow-hidden border"
                        >
                          <img
                            src={
                              typeof img === "string"
                                ? img
                                : URL.createObjectURL(img)
                            }
                            alt={`preview-${index}`}
                            className="w-full h-full object-cover"
                          />
                          {/* {typeof img !== "string" && ( */}
                            <button
                              type="button"
                              className="absolute -top-1 -right-1 bg-white rounded-full p-1"
                              onClick={() => handleRemoveImage(index)}
                            >
                              <FontAwesomeIcon
                                icon={faXmark}
                                className="text-red-500"
                              />
                            </button>
                          {/* )} */}
                        </div>
                      ))}
                    </div>
                  )}
                </FormItem>

                {/* Buttons */}
                <div className="flex items-center gap-4">
                  <DialogClose>
                    <Button
                      type="submit"
                      className="w-full bg-orange-700 hover:bg-orange-500"
                    >
                      Save
                    </Button>
                  </DialogClose>
                  <Button
                    type="reset"
                    variant="outline"
                    className=" border border-orange-700 text-orange-700 hover:bg-orange-700 hover:text-white"
                    onClick={resetForm}
                  >
                    Reset
                  </Button>
                </div>
              </form>
            </Form>
          </div>
        </ScrollArea>
      </DialogContent>
    </Dialog>
  );
};

export default CreateBlogForm;
