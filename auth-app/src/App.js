import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Home from './components/Home';
import Login from './components/Login';
import Register from './components/Register';
import CityForm from './components/CityForm';
import AdminPanel from "./components/AdminPanel";
import CityActionSelector from "./components/CityActionSelector";
import CityUpdate from "./components/CityUpdate";
import CitySelectForUpdate from "./components/CitySelectForUpdate";
import CityInfo from './components/CityInfo';  // Новый импорт

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
                <Route path="/cities/info/:id" element={<CityInfo />} />
                <Route
                    path="/admin"
                    element={user && user.roles.includes('ADMIN') ? <AdminPanel /> : <Navigate to="/" />}
                />
            </Routes>
        </Router>
    );
};

export default App;
