import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useSelector } from "react-redux";

const ProtectedRoute = () => {
  const loggedInUser = useSelector((store) => store.auth.user);
  const location = useLocation();

  if (!loggedInUser) {
    return (
      <Navigate
        to="/login"
        replace
        state={{ redirectTo: location.pathname }}
      />
    );
  }

  return <Outlet />;
};

export default ProtectedRoute;