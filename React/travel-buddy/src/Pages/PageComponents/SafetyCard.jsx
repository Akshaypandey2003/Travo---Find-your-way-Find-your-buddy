/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import React from "react";
import { Card, CardContent } from "@/components/ui/card";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faCommentAlt,
  faCommentDollar,
  faComments,
  faExclamationTriangle,
  faIdCard,
  faShield,
  faShieldAlt,
} from "@fortawesome/free-solid-svg-icons";
import { useSelector } from "react-redux";

export const SafetyCard = ({item}) => {

  const theme = useSelector(store=>store.auth.theme);
  return (
    <Card className={`border-none shadow-xl p-5 w-[35rem] ${theme==="dark" ?"bg-gray-950 bg-opacity-80" :"bg-orange-50 bg-opacity-80"}`}>
      <div className=" text-center">
        <FontAwesomeIcon icon={item.icon} className={item.className} />
        <h1 className="text-4xl font-semibold">
          {item.title}
        </h1>
        <p className="text-gray-500">
         {item.desc}
        </p>
      </div>
    </Card>
  );
};
