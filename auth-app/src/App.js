import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Home from './components/Home';
import Login from './components/Login';
import Register from './components/Register';
import CityForm from './components/CityForm';
import AdminPanel from "./components/AdminPanel";
import Navigation from "./components/Navigation";
import UpdateObject from "./components/UpdateObject";
import ViewObject from "./components/ViewObject";
import DeleteObject from "./components/DeleteObject";


const App = () => {
    const [user, setUser] = useState(null);

    useEffect(() => {
        const storedUser = JSON.parse(localStorage.getItem('user'));
        if (storedUser) {
            setUser(storedUser);
        }
    }, []);

    const handleLogout = () => {
        setUser(null);
        localStorage.removeItem('user');
    };

    return (
        <Router>
            <Navigation user={user} onLogout={handleLogout} />
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/login" element={<Login setUser={setUser} />} />
                <Route path="/register" element={<Register />} />
                <Route path="/city-form" element={<CityForm />} />
                <Route
                    path="/admin"
                    element={user && user.roles.includes('ADMIN') ? <AdminPanel /> : <Navigate to="/" />}
                />
                <Route path="/view-object/:id" element={<ViewObject />} />
                <Route path="/update-object/:id" element={<UpdateObject />} />
                <Route path="/delete-object/:id" element={<DeleteObject />} />
            </Routes>
        </Router>
    );
};

export default App;
