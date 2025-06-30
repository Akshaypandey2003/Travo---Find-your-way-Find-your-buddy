/* eslint-disable no-unused-vars */
import React, { useState } from "react";
import Login from "./Login";
import { Button } from "@/components/ui/button";
import Register from "./Register";
// import "./Auth.css";

export const Auth = () => {
  const [active, setActive] = useState(false);
  return (
    <div className="flex justify-center items-center border border-red-700">
      <div className="p-2">
        {active ? <Login /> : <Register />}
        <div>
          <span className="font-sans text-gray-400">
            {!active ? "Already have an account ?" : "Don't have account ?"}
          </span>
          <span
            className="border-b-2 border-blue-900 text-blue-900 mx-2 hover:cursor-pointer font-sans "
            onClick={() => setActive(!active)}
          >
            {!active ? "Signin" : "Signup"}
          </span>
        </div>
      </div>
    </div>
  );
};

export default Auth;
