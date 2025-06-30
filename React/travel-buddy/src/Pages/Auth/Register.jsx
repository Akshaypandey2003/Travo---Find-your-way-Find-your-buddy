/* eslint-disable no-unused-vars */
import { Formik, Form, Field, ErrorMessage } from "formik";
import * as Yup from "yup";
import { Link } from "react-router-dom";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectTrigger,
  SelectItem,
  SelectContent,
  SelectValue,
} from "@/components/ui/select";

import useAuth from "../../CustomHooks/useAuth";
import { shallowEqual, useDispatch, useSelector } from "react-redux";
import { useEffect, useState } from "react";
import { authFailure } from "../../Redux/Slices/authSlice";

const registerValidationSchema = Yup.object({
  name: Yup.string()
    .min(4, "Name must be at least 4 characters long")
    .matches(/^[a-zA-Z ]+$/, "Enter valid name (Eg. John Wick)")
    .required("Name is required"),
  email: Yup.string()
    .matches(
      /^[a-zA-Z0-9._%+-]+@[a-zA-Z.-]+\.[a-zA-Z]{2,}$/,
      "Invalid email address"
    )
    .required("Email is required"),
  gender: Yup.string().required("Gender is required"),
  password: Yup.string()
    .min(8, "Password must be at least 8 characters long")
    .matches(/[A-Z]/, "Must have uppercase letter")
    .matches(/[a-z]/, "Must have lowercase letter")
    .matches(/\d/, "Must have a number")
    .matches(/[@$!%*?&]/, "Must have special character")
    .required("Password is required"),
});

export const Register = () => {
  const { registerUser, loading } = useAuth();

  const error = useSelector((store)=>store.auth.error,shallowEqual);

  const dispatch = useDispatch();
  const [showAlert, setShowAlert] = useState(false);

  useEffect(() => {
        setShowAlert(true);
        const timer = setTimeout(() => {
          setShowAlert(false);
          dispatch(authFailure(""))
        }, 2000);
        return () => clearTimeout(timer); // cleanup
     
    }, [error]);

  return (
    <div className="flex justify-center items-center w-screen h-screen">
      
      <Card className="w-full max-w-md p-6 shadow-lg rounded-2xl">
        <CardContent>
         
          <h1 className="text-2xl font-semibold text-center mb-4">Register</h1>
          {showAlert && error && <p className="text-red-500 text-xs">{error}</p>}
          <Formik
            initialValues={{ name: "", email: "", gender: "", password: "" }}
            validationSchema={registerValidationSchema}
            validateOnBlur={true} // triggers validation when you leave input
            validateOnChange={false} // doesn't validate on every keystroke
            onSubmit={(values) => {
              console.log("Registered User:", values);
              registerUser(values);
            }}
          >
            {({
              handleChange,
              handleBlur,
              handleSubmit,
              values,
              setFieldValue,
              errors,
              touched,
            }) => (
              <Form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <Label>Name</Label>
                  <Input
                    name="name"
                    onBlur={handleBlur}
                    onChange={handleChange}
                    value={values.name}
                  />
                  {touched.name && errors.name && (
                    <p className="text-red-500 text-xs">{errors.name}</p>
                  )}
                </div>

                <div>
                  <Label>Email</Label>
                  <Input
                    name="email"
                    onChange={handleChange}
                    onBlur={handleBlur}
                    value={values.email}
                  />
                  {touched.email && errors.email && (
                    <p className="text-red-500 text-xs">{errors.email}</p>
                  )}
                </div>

                <div>
                  <Label>Gender</Label>
                  <Select
                    onBlur={handleBlur} onValueChange={(value) => setFieldValue("gender", value)}
                  >
                    <SelectTrigger className="w-[200px]">
                      <SelectValue placeholder="Select Gender" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="male">Male</SelectItem>
                      <SelectItem value="female">Female</SelectItem>
                      <SelectItem value="other">Other</SelectItem>
                    </SelectContent>
                  </Select>
                  {touched.gender && errors.gender && (
                    <p className="text-red-500 text-xs">{errors.gender}</p>
                  )}
                </div>

                <div>
                  <Label>Password</Label>
                  <Input
                    name="password"
                    type="password"
                    onChange={handleChange}
                    onBlur={handleBlur}
                    value={values.password}
                  />
                  {touched.password && errors.password && (
                    <p className="text-red-500 text-xs">{errors.password}</p>
                  )}
                </div>

                <Button
                  type="submit"
                  className="w-full bg-orange-700 hover:bg-orange-600"
                >
                  {loading ? "Registering..." : "Register"}
                </Button>
              </Form>
            )}
          </Formik>

          <div className="mt-4 text-xs">
            <span className="mr-2">Already have an account ?</span>
            <Link to="/login" className="text-orange-700 hover:underline">
              SignIn
            </Link>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};
export default Register;
