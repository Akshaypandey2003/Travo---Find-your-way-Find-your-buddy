/* eslint-disable react/prop-types */
// components/FollowingUsersList.jsx
import { useState, useMemo } from "react";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Input } from "@/components/ui/input";
import CustomerCard from "../PageComponents/CustomerCard";

const FollowingUsers = ({ users }) => {
  const [searchTerm, setSearchTerm] = useState("");

  const filteredUsers = useMemo(() => {
    if (!searchTerm.trim()) return users;
    return users.filter((user) =>
      user.name.toLowerCase().includes(searchTerm.toLowerCase())
    );
  }, [searchTerm, users]);

  return (
    <div className="w-[80rem] py-5 px-5">
      <Input
        placeholder="Search by name..."
        className="mb-4 w-full max-w-sm"
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
      />

      <ScrollArea className="h-96 overflow-y-auto border rounded-md p-4">
        {filteredUsers?.length > 0 ? (
          <div className="flex items-center flex-wrap gap-5">
            {filteredUsers.map((user, index) => (
              <CustomerCard key={index} user={user} />
            ))}
          </div>
        ) : (
          <p className="text-muted-foreground text-sm">No matching users found.</p>
        )}
      </ScrollArea>
    </div>
  );
};

export default FollowingUsers;
