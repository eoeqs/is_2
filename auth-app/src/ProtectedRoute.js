import { Navigate } from 'react-router-dom';
import { useAuth } from './AuthProvider';
import {useEffect} from "react";

const ProtectedRoute = ({ children }) => {
    const { token } = useAuth();

    useEffect(() => {
        console.log("Updated token in ProtectedRoute: ", token);
    }, [token]);

    if (!token) {
        return <Navigate to="/" replace />;
    }

    return children;
};

export default ProtectedRoute;
