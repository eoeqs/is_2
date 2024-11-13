import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Home from './components/Home';
import Login from './components/Login';
import Register from './components/Register';
import CityForm from './components/CityForm';
import CityActionSelector from './components/CityActionSelector';
import CityUpdate from './components/CityUpdate';
import CitySelectForUpdate from './components/CitySelectForUpdate';
import CityDelete from './components/CityDelete';
import CityInfo from "./components/CityInfo";
import RoleChangeRequest from "./components/RoleChangeRequest";
import RoleRequestManager from "./components/RoleRequestManager";  // Импортируем новый компонент

const App = () => {
    const [user, setUser] = useState({ roles: [] });

    useEffect(() => {
        const loggedInUser = JSON.parse(localStorage.getItem('user'));
        if (loggedInUser) {
            setUser(loggedInUser);
        }
    }, []);

    const handleLogout = () => {
        setUser(null);
        localStorage.removeItem('user');
    };

    return (
        <Router>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/city-actions" element={<CityActionSelector />} />
                <Route path="/cities/create" element={<CityForm />} />
                <Route path="/cities/update/:id" element={<CityUpdate />} />
                <Route path="/cities/update" element={<CitySelectForUpdate />} />
                <Route path="/cities/delete" element={<CityDelete />} />
                <Route path="/cities/info/:id" element={<CityInfo/>} />
                <Route path="/request-role-change" element={<RoleChangeRequest />} />
                <Route path="/role-requests" element={<RoleRequestManager />} />
            </Routes>
        </Router>
    );
};

export default App;
