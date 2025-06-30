/* eslint-disable no-unused-vars */
import { Link } from "react-router-dom";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Formik, Form, Field, ErrorMessage } from "formik";
import useAuth from "../../CustomHooks/useAuth";
import * as Yup from "yup";
import { shallowEqual, useSelector } from "react-redux";
import LoginForm from "./LoginForm";

const loginValidationSchema = Yup.object().shape({
  email: Yup.string()
    .matches(
      /^[a-zA-Z0-9._%+-]+@[a-zA-Z.-]+\.[a-zA-Z]{2,}$/,
      "Invalid email address"
    )
    .required("Email is required"),
  password: Yup.string()
    .min(8, "Password must be at least 8 characters long")
    .matches(/[A-Z]/, "Must contain at least one uppercase letter")
    .matches(/[a-z]/, "Must contain at least one lowercase letter")
    .matches(/\d/, "Must contain at least one digit")
    .matches(
      /[@$!%*?&#]/,
      "Must contain at least one special character"
    )
    .required("Password is required"),
});

export const Login = () => {

  const { loginUser, loading } = useAuth();

  const error = useSelector((store)=>store.auth.error,shallowEqual);

  return (
    <div className="flex justify-center items-center w-screen h-screen">
     <LoginForm/>
    </div>
  );
};

export default Login;
