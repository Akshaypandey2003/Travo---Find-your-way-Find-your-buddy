/* eslint-disable react/prop-types */

import React, { useEffect,useRef, useState } from "react";
import { shallowEqual, useSelector } from "react-redux";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";

import {
  BookOpen,
  Camera,
  Tag,
  Type,
  X,
  ArrowRight,
  RotateCcw,
  ImagePlus,
  ChevronLeft,
  ChevronRight,
} from "lucide-react";

import useBlog from "../CustomHooks/useBlog";

const CreateBlog = ({ formType, blog }) => {
  const navigate = useNavigate();

  const user = useSelector((store) => store.auth.user, shallowEqual);

  const { postBlog, updateBlog } = useBlog();

  const [selectedImages, setSelectedImages] = useState([]);

  const categories = [
    "mountains",
    "beaches",
    "adventure",
    "food",
    "sports",
    "music",
  ];

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    watch,
    formState: { errors, isSubmitting },
  } = useForm({
    defaultValues: {
      authorId: blog?.authorId || user?.userId || "",
      authorName: blog?.authorName || user?.name || "",
      authorProfilePic:
        blog?.authorProfilePic || user?.profilePic || "",

      title: blog?.title || "",
      content: blog?.content || "",
      caption: blog?.caption || "",
      category: blog?.category || "",
      imageUrls: blog?.imageUrls || [],

      cloudinaryPublicIds: blog?.cloudinaryPublicIds || [],
    },
  });

  const selectedCategory = watch("category");

  /*
   * When opening component in UPDATE mode,
   * populate existing blog data.
   */
  useEffect(() => {
    if (formType === "update" && blog) {
      reset({
       authorId: blog?.authorId || user?.userId || "",
      authorName: blog?.authorName || user?.name || "",
      authorProfilePic:
        blog?.authorProfilePic || user?.profilePic || "",

      title: blog?.title || "",
      content: blog?.content || "",
      caption: blog?.caption || "",
      category: blog?.category || "",
      imageUrls: blog?.imageUrls || [],

      cloudinaryPublicIds: blog?.cloudinaryPublicIds || [],
      });

      setSelectedImages(blog?.imageUrls || []);
    }
  }, [blog, formType, reset, user]);

  /*
   * If the component is used for CREATE,
   * make sure currently logged-in user information
   * gets populated after Redux user is available.
   */
  useEffect(() => {
    if (formType === "create" && user) {
      setValue("authorId", user?.userId || "");
      setValue("authorName", user?.name || "");
      setValue("authorProfilePic", user?.profilePic || "");
    }
  }, [user, formType, setValue]);

  /*
   * Image selection
   */
  const handleImageChange = (event) => {
    const files = Array.from(event.target.files || []);

    if (files.length === 0) {
      return;
    }

    setSelectedImages((previousImages) => {
      const updatedImages = [...previousImages, ...files];

      setValue("imageUrls", updatedImages);

      return updatedImages;
    });

    /*
     * Allows selecting the same file again after removing it.
     */
    event.target.value = "";
  };

  /*
   * Remove image from preview
   */
  const handleRemoveImage = (indexToRemove) => {
    setSelectedImages((previousImages) => {
      const updatedImages = previousImages.filter(
        (_, index) => index !== indexToRemove,
      );

      setValue("imageUrls", updatedImages);

      return updatedImages;
    });
  };

  /*
   * Reset form
   */
  const resetForm = () => {
    if (formType === "update" && blog) {
      reset({
        authorId: blog?.authorId || user?.userId || "",

        authorName: blog?.authorName || user?.name || "",

        authorProfilePic:
          blog?.authorProfilePic || user?.profilePic || "",

        title: blog?.title || "",
        content: blog?.content || "",
        caption: blog?.caption || "",
        category: blog?.category || "",

        imageUrls: blog?.imageUrls || [],

        cloudinaryPublicIds: blog?.cloudinaryPublicIds || [],
      });

      setSelectedImages(blog?.imageUrls || []);
    } else {
      reset({
        authorId: user?.userId || "",
        authorName: user?.name || "",
        authorProfilePic: user?.profilePic || "",
        title: "",
        content: "",
        caption: "",
        category: "",
        imageUrls: [],
        cloudinaryPublicIds: [],
      });

      setSelectedImages([]);
    }
  };

  const imageScrollRef = useRef(null);

  const scrollImages = (direction) => {
    if (imageScrollRef.current) {
      imageScrollRef.current.scrollBy({
        left: direction === "next" ? 300 : -300,
        behavior: "smooth",
      });
    }
  };

  /*
   * Submit Blog
   */
  const onSubmit = async (data) => {
    const finalData = {
      ...data,
      imageUrls: selectedImages,
    };

    console.log(
      formType === "create" ? "Created blog is:" : "Updated blog is:",
      finalData,
    );
    try {
      if (formType === "create") {
        await postBlog(finalData);
      } else {
        await updateBlog(finalData, blog?.blogId);
      }

      /*
       * Clear form after successful CREATE.
       * For update we normally keep the values.
       */
      if (formType === "create") {
        resetForm();
      }

    } catch (error) {
      console.error(
        `Failed to ${formType === "create" ? "create" : "update"} blog:`,
        error,
      );
    }
  };

  return (
    <div className="max-w-3xl mx-auto py-8 relative">
      {/* Main Card */}
      <div className="bg-white dark:bg-surface-darker border dark:border-slate-800 rounded-3xl shadow-2xl overflow-hidden relative z-10">
        {/* Header */}
        <header className="px-8 pt-8 pb-6 border-b dark:border-slate-800">
          <h1 className="text-3xl font-extrabold tracking-tight mb-2">
            {formType === "create"
              ? "Share your adventure"
              : "Update your adventure"}
          </h1>

          <p className="text-slate-500 text-sm">
            {formType === "create"
              ? "Tell the Travo community about your journey and experiences."
              : "Update your blog and keep your story fresh."}
          </p>
        </header>

        <form className="p-8 space-y-12" onSubmit={handleSubmit(onSubmit)}>
          {/* Hidden Author Information */}
          <input type="hidden" {...register("authorId")} />

          <input type="hidden" {...register("authorName")} />

          <input type="hidden" {...register("authorProfilePic")} />

          {/* ================================================= */}
          {/* SECTION 1 - BLOG BASICS */}
          {/* ================================================= */}

          <section className="space-y-6">
            <div className="flex items-center gap-2 mb-4">
              <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center text-primary">
                <BookOpen size={16} />
              </div>

              <h2 className="text-lg font-bold">Blog Basics</h2>
            </div>

            <div className="space-y-6">
              {/* Blog Title */}
              <div className="space-y-1.5">
                <label className="text-[10px] font-bold text-slate-500 uppercase tracking-widest ml-1">
                  Blog Title
                </label>

                <div className="relative">
                  <Type
                    className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400"
                    size={18}
                  />

                  <input
                    type="text"
                    placeholder="Give your adventure a title..."
                    {...register("title", {
                      required: "Blog title is required",
                    })}
                    className={`w-full bg-slate-50 dark:bg-surface-dark border rounded-xl py-3.5 pl-12 pr-4 text-sm focus:ring-2 focus:ring-primary transition-all outline-none ${
                      errors.title
                        ? "border-red-500"
                        : "dark:border-slate-800"
                    }`}
                  />
                </div>

                {errors.title && (
                  <p className="text-xs text-red-500 ml-1">
                    {errors.title.message}
                  </p>
                )}
              </div>

              {/* Caption */}
              <div className="space-y-1.5">
                <label className="text-[10px] font-bold text-slate-500 uppercase tracking-widest ml-1">
                  Caption
                </label>

                <input
                  type="text"
                  placeholder="A short caption for your story..."
                  {...register("caption")}
                  className="w-full bg-slate-50 dark:bg-surface-dark border dark:border-slate-800 rounded-xl py-3.5 px-4 text-sm focus:ring-2 focus:ring-primary transition-all outline-none"
                />
              </div>
            </div>
          </section>

          <hr className="dark:border-slate-800" />

          {/* ================================================= */}
          {/* SECTION 2 - CATEGORY */}
          {/* ================================================= */}

          <section className="space-y-6">
            <div className="flex items-center gap-2 mb-4">
              <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center text-primary">
                <Tag size={16} />
              </div>

              <h2 className="text-lg font-bold">Category</h2>
            </div>

            <div className="space-y-3">
              <label className="text-[10px] font-bold text-slate-500 uppercase tracking-widest ml-1">
                What kind of adventure is this?
              </label>

              <div className="flex flex-wrap gap-3">
                {categories.map((category) => {
                  const isSelected = selectedCategory === category;

                  return (
                    <button
                      key={category}
                      type="button"
                      onClick={() =>
                        setValue("category", category, {
                          shouldValidate: true,
                          shouldDirty: true,
                        })
                      }
                      className={`px-5 py-2 rounded-full border text-xs font-bold transition-all capitalize ${
                        isSelected
                          ? "bg-primary border-primary text-white shadow-lg shadow-primary/20"
                          : "bg-white dark:bg-surface-dark border-slate-200 dark:border-slate-800 text-slate-500 hover:border-slate-400 dark:hover:border-slate-600"
                      }`}
                    >
                      {category}
                    </button>
                  );
                })}
              </div>

              <input
                type="hidden"
                {...register("category", {
                  required: "Please select a category",
                })}
              />

              {errors.category && (
                <p className="text-xs text-red-500 ml-1">
                  {errors.category.message}
                </p>
              )}
            </div>
          </section>

          <hr className="dark:border-slate-800" />

          {/* ================================================= */}
          {/* SECTION 3 - CONTENT */}
          {/* ================================================= */}

          <section className="space-y-6">
            <div className="flex items-center gap-2 mb-4">
              <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center text-primary">
                <BookOpen size={16} />
              </div>

              <h2 className="text-lg font-bold">Your Story</h2>
            </div>

            <div className="space-y-1.5">
              <label className="text-[10px] font-bold text-slate-500 uppercase tracking-widest ml-1">
                Content
              </label>

              <textarea
                placeholder="Share your story... Tell people about the places you visited, experiences you had, food you tried, and moments worth remembering."
                {...register("content", {
                  required: "Blog content is required",
                })}
                className={`w-full bg-slate-50 dark:bg-surface-dark border rounded-2xl p-6 text-sm focus:ring-2 focus:ring-primary outline-none resize-none h-56 leading-relaxed ${
                  errors.content
                    ? "border-red-500"
                    : "dark:border-slate-800"
                }`}
              />

              {errors.content && (
                <p className="text-xs text-red-500 ml-1">
                  {errors.content.message}
                </p>
              )}
            </div>
          </section>

          <hr className="dark:border-slate-800" />

          {/* ================================================= */}
          {/* SECTION 4 - IMAGES */}
          {/* ================================================= */}

          <section className="space-y-6">
            <div className="flex items-center gap-2 mb-4">
              <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center text-primary">
                <Camera size={16} />
              </div>

              <h2 className="text-lg font-bold">Photos</h2>
            </div>

            <div className="space-y-4">
              <label className="text-[10px] font-bold text-slate-500 uppercase tracking-widest ml-1">
                Add Images
              </label>

              {/* Upload Area */}
              <label
                htmlFor="imageUrls"
                className="flex flex-col items-center justify-center gap-3 w-full min-h-40 bg-slate-50 dark:bg-surface-dark border-2 border-dashed border-slate-200 dark:border-slate-800 rounded-2xl cursor-pointer hover:border-primary dark:hover:border-primary transition-all group"
              >
                <div className="w-12 h-12 rounded-2xl bg-primary/10 flex items-center justify-center text-primary group-hover:scale-110 transition-transform">
                  <ImagePlus size={22} />
                </div>

                <div className="text-center">
                  <p className="text-sm font-bold">Add photos to your blog</p>

                  <p className="text-[11px] text-slate-500 mt-1">
                    Click to browse images from your device
                  </p>
                </div>

                <input
                  id="imageUrls"
                  type="file"
                  multiple
                  accept="image/*"
                  onChange={handleImageChange}
                  className="hidden"
                />
              </label>

              {/* Image Preview */}

        
              {selectedImages.length > 0 && (
                <div className="relative group/slider ">
                  {/* Previous Arrow */}
                  <button
                    type="button"
                    onClick={() => scrollImages("prev")}
                    className="absolute left-2 top-1/2 -translate-y-1/2 z-20 w-9 h-9 rounded-full bg-white/90 dark:bg-slate-900/90 shadow-lg flex items-center justify-center text-slate-700 dark:text-slate-200 hover:bg-primary hover:text-white transition-all"
                    aria-label="Previous images"
                  >
                    <ChevronLeft size={18} />
                  </button>

                  {/* Horizontal Scroll Container */}
                  <div
                    ref={imageScrollRef}
                    className="flex gap-4 overflow-x-auto scroll-smooth  pb-3 px-1 image-scrollbar"
                  >
                    {selectedImages.map((image, index) => {
                      const imageUrl =
                        typeof image === "string"
                          ? image
                          : URL.createObjectURL(image);

                      return (
                        <div
                          key={`${index}-${imageUrl}`}
                          className="relative group aspect-square w-32 h-32 min-w-32 shrink-0 rounded-2xl overflow-hidden border dark:border-slate-800 bg-slate-100 dark:bg-slate-900"
                        >
                          <img
                            src={imageUrl}
                            alt={`Blog preview ${index + 1}`}
                            className="w-full h-full object-cover transition-transform duration-300 group-hover:scale-105"
                          />

                          {/* Overlay */}
                          <div className="absolute inset-0 bg-black/0 group-hover:bg-black/20 transition-all" />

                          {/* Remove Image */}
                          <button
                            type="button"
                            onClick={() => handleRemoveImage(index)}
                            className="absolute top-2 right-2 w-8 h-8 rounded-full bg-white/90 dark:bg-slate-900/90 flex items-center justify-center shadow-lg opacity-0 group-hover:opacity-100 hover:bg-red-500 hover:text-white transition-all"
                            aria-label="Remove image"
                          >
                            <X size={15} />
                          </button>

                          {/* Number */}
                          <span className="absolute bottom-2 left-2 px-2 py-1 rounded-lg bg-black/60 text-white text-[10px] font-bold">
                            {index + 1}
                          </span>
                        </div>
                      );
                    })}
                  </div>

                  {/* Next Arrow */}
                  <button
                    type="button"
                    onClick={() => scrollImages("next")}
                    className="absolute right-2 top-1/2 -translate-y-1/2 z-20 w-9 h-9 rounded-full bg-white/90 dark:bg-slate-900/90 shadow-lg flex items-center justify-center text-slate-700 dark:text-slate-200 hover:bg-primary hover:text-white transition-all"
                    aria-label="Next images"
                  >
                    <ChevronRight size={18} />
                  </button>
                </div>
              )}

              {selectedImages.length > 0 && (
                <p className="text-[10px] text-slate-500 font-semibold">
                  {selectedImages.length}{" "}
                  {selectedImages.length === 1 ? "image" : "images"} selected
                </p>
              )}
            </div>
          </section>

          {/* ================================================= */}
          {/* ACTION BUTTONS */}
          {/* ================================================= */}

          <div className="pt-6 space-y-4">
            {/* Create / Update */}
            <button
              type="submit"
              disabled={isSubmitting}
              className="w-full bg-primary hover:bg-primary-hover disabled:opacity-60 disabled:cursor-not-allowed text-white py-4 rounded-2xl font-bold shadow-xl shadow-primary/30 transition-all flex items-center justify-center gap-2 group"
            >
              {isSubmitting
                ? formType === "create"
                  ? "Publishing..."
                  : "Updating..."
                : formType === "create"
                  ? "Publish Blog"
                  : "Update Blog"}

              {!isSubmitting && (
                <ArrowRight
                  size={20}
                  className="group-hover:translate-x-1 transition-transform"
                />
              )}
            </button>

            {/* Reset */}
            <button
              type="button"
              onClick={resetForm}
              disabled={isSubmitting}
              className="w-full bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 text-slate-700 dark:text-slate-200 py-3.5 rounded-2xl font-bold transition-all flex items-center justify-center gap-2"
            >
              <RotateCcw size={17} />

              {formType === "create" ? "Reset Form" : "Reset Changes"}
            </button>

            <p className="text-center text-[10px] text-slate-500 mt-6 uppercase tracking-widest font-bold">
              By publishing this blog, you agree to Travo&apos;s{" "}
              <a href="#" className="underline text-primary">
                Community Guidelines
              </a>
            </p>
          </div>
        </form>
      </div>

      {/* Decorative Blur - same concept as CreateTrip */}
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-full h-[600px] bg-primary/10 blur-[150px] -z-10 rounded-full" />
    </div>
  );
};

export default CreateBlog;
