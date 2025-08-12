/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
import { useState } from "react";
import { useSelector } from "react-redux";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import { toast } from "sonner";
import { Star } from "lucide-react";

export const TripFeedbackForm = ({ tripId, onSubmit }) => {
  const { user: loggedInUser } = useSelector((store) => store.auth);
  const [comment, setComment] = useState("");
  const [rating, setRating] = useState(0);
  const [tags, setTags] = useState([]);
  const [tagInput, setTagInput] = useState("");
  const theme = useSelector((state) => state.auth.theme);

  const handleTagAdd = (e) => {
    e.preventDefault();
    if (tagInput.trim() && !tags.includes(tagInput.trim())) {
      setTags([...tags, tagInput.trim()]);
    }
    setTagInput("");
  };

  const handleSubmit = () => {
    if (!comment || rating <= 0) {
      toast.error("Please provide a comment and rating.");
      return;
    }

    const payload = {
      tripId: tripId,
      authorId: loggedInUser?.userId,
      author:{
        name: loggedInUser?.name,
        userId: loggedInUser?.userId,
        profilePic: loggedInUser?.profilePic
      },
      comment,
      rating,
      tags,
    };
    onSubmit(payload);
    console.log("Submitted feedback is: ",payload);
  };

   return (
    <div
      className={`border p-4 rounded-xl space-y-4 ${
        theme==="dark"
          ? "bg-[#1f1f1f] text-gray-200 border-gray-700"
          : "bg-orange-50 text-gray-800 border-orange-200"
      }`}
    >
      <h3
        className={`text-lg font-semibold ${
          theme==="dark" ? "text-orange-400" : "text-orange-800"
        }`}
      >
        Submit Your Feedback
      </h3>

      <div>
        <Label className="text-sm">Your Rating</Label>
        <div className="flex gap-1 mt-1">
          {[1, 2, 3, 4, 5].map((star) => (
            <Star
              key={star}
              className={`h-6 w-6 cursor-pointer ${
                star <= rating
                  ? "text-yellow-400"
                  : theme==="dark"
                  ? "text-gray-600"
                  : "text-gray-300"
              }`}
              onClick={() => setRating(star)}
            />
          ))}
        </div>
      </div>

      <div>
        <Label className="text-sm">Feedback Comment</Label>
        <Textarea
          placeholder="Share your thoughts about the trip..."
          value={comment}
          onChange={(e) => setComment(e.target.value)}
          rows={4}
          className={`resize-none mt-1 ${
            theme==="dark" ? "bg-[#2c2c2c] text-gray-100 border-gray-700" : ""
          }`}
        />
      </div>

      <div>
        <Label className="text-sm">Tags</Label>
        <form onSubmit={handleTagAdd} className="flex gap-2 mt-1">
          <Input
            type="text"
            placeholder="Add a tag (e.g., fun, well-organized)"
            value={tagInput}
            onChange={(e) => setTagInput(e.target.value)}
            className={`w-1/2 ${
              theme==="dark" ? "bg-[#2c2c2c] text-gray-100 border-gray-700" : ""
            }`}
          />
          <Button type="submit" variant="outline" size="sm">
            Add Tag
          </Button>
        </form>
        <div className="flex gap-2 flex-wrap mt-2">
          {tags.map((tag, index) => (
            <Badge
              key={index}
              className={`border text-xs ${
                theme==="dark"
                  ? "bg-orange-900 border-orange-600 text-orange-300"
                  : "bg-orange-100 border-orange-400 text-orange-700"
              }`}
            >
              #{tag}
            </Badge>
          ))}
        </div>
      </div>

      <div className="text-right">
        <Button
          onClick={handleSubmit}
          className="bg-orange-600 hover:bg-orange-700 text-white"
        >
          Submit Feedback
        </Button>
      </div>
    </div>
  );
};

export default TripFeedbackForm;
