import {Navigate, Outlet} from "react-router";
import {useAuth} from "../context/AuthContext.tsx";

const PrivateRoute = ({requiredRole}: { requiredRole: string }) => {
    const {isAuthenticated, role} = useAuth();

    if (!isAuthenticated) return <Navigate to="/login" replace/>;
    if (requiredRole && role !== requiredRole) return <Navigate to="/unauthorized" replace/>;

    return <Outlet/>
};

export default PrivateRoute;
