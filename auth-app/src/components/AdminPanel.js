import React from 'react';
import {useAuth} from "../AuthProvider";


const AdminPanel = () => {
    const { userRole } = useAuth();

    if (userRole !== 'ADMIN') {
        return <p>You do not have access to the admin panel.</p>;
    }

    return <div>Welcome to the admin panel!</div>;
};

export default AdminPanel;
