/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";
import { DEFAULT_MALE_PIC } from "../../../Constants/constants";

export const SuggestedUserCard = ({user}) => {
    console.log("User presernt in chat card: ",user);
  return (
    <Card className="border-none shadow-none p-2">
      <div className="text-wrap flex  gap-2">
        <Avatar>
          <AvatarImage src={DEFAULT_MALE_PIC} />
          <AvatarFallback>AK</AvatarFallback>
        </Avatar>
        <div className="w-full">
          <div className="flex items-center justify-between w-full">
            <h1 className="font-semibold text-sm">{user.name}</h1>
            <div>
              <Badge className="rounded-xl bg-orange-700 py-0 px-1">
                <span className="text-xs">2</span>
              </Badge>
            </div>
          </div>
          <h1 className="text-xs">Sent 54 min ago</h1>
        </div>
      </div>
    </Card>
  );
};
export default SuggestedUserCard;
