/* eslint-disable react/prop-types */
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

export const LoginForm = ({data}) => {

  const { loginUser, loading } = useAuth();

  const error = useSelector((store)=>store.auth.error,shallowEqual);

  return (
      <Card className={`${data} w-full max-w-md p-6  shadow-lg rounded-2xl`}>
        <CardContent>
          <h1 className="text-2xl font-semibold text-center mb-4">SignIn</h1>

          <Formik
            initialValues={{ email: "", password: "" }}
            validationSchema={loginValidationSchema}
            onSubmit={(values) => {
              loginUser(values);
            }}
          >
            {({ handleChange, handleBlur, values }) => (
              <Form className="space-y-4">
                <div>
                  <Label htmlFor="email">Email</Label>
                  <Input
                    type="email"
                    name="email"
                    onChange={handleChange}
                    onBlur={handleBlur}
                    value={values.email}
                  />
                  <ErrorMessage
                    name="email"
                    component="div"
                    className="text-red-500 text-xs mt-1"
                  />
                </div>

                <div>
                  <Label htmlFor="password">Password</Label>
                  <Input
                    type="password"
                    name="password"
                    onChange={handleChange}
                    onBlur={handleBlur}
                    value={values.password}
                  />
                  <ErrorMessage
                    name="password"
                    component="div"
                    className="text-red-500 text-xs mt-1"
                  />
                </div>

                <Button
                  type="submit"
                  className="w-full bg-orange-700 hover:bg-orange-600"
                >
                  {loading ? "Logging in..." : "Login"}
                </Button>
              </Form>
            )}
          </Formik>

          <div className="mt-4 text-xs">
            <span className="mr-2">Don't have an account?</span>
            <Link
              to="/register"
              className="text-orange-700 hover:underline"
            >
              SignUp
            </Link>
          </div>
        </CardContent>
      </Card>
  );
};

export default LoginForm;
