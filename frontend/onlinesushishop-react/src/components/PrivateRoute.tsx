import {Navigate, Outlet} from "react-router";

const PrivateRoute = () => {
    const token = localStorage.getItem("token");

    return token ? <Outlet/> : <Navigate to="/login" replace/>;
};

export default PrivateRoute;
