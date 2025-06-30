/* eslint-disable react/prop-types */

import { useSelector } from "react-redux";
import { Navigate } from "react-router-dom";

/* eslint-disable no-unused-vars */
const ProtectedRoute = ({ children }) => {

    const loggedInUser = useSelector((store)=> store.auth.user);
    if (!loggedInUser) {
      return <Navigate to="/login" replace />;
    }
    return children;
  };
  export default ProtectedRoute;
  