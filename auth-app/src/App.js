import React, {useEffect, useState} from 'react';
import {BrowserRouter as Router, Routes, Route, Navigate} from 'react-router-dom';

import Register from './components/Register';
import CityForm from './components/CityForm';
import CityActionSelector from './components/CityActionSelector';
import CityUpdate from './components/CityUpdate';
import CitySelectForUpdate from './components/CitySelectForUpdate';
import CityDelete from './components/CityDelete';
import CityInfo from "./components/CityInfo";
import RoleChangeRequest from "./components/RoleChangeRequest";
import RoleRequestManager from "./components/RoleRequestManager";
import ProtectedRoute from "./ProtectedRoute";
import LoginPage from "./components/LoginPage";
import CallbackPage from "./components/CallBackPage";
import CityHistory from "./components/CityHistory";
import ImportHistory from "./components/ImportHistory";

const App = () => {
    const [user, setUser] = useState({roles: []});

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
                <Route path="/" element={<LoginPage/>}/>
                <Route path="/import-history" element={<ProtectedRoute>
                    <ImportHistory/> </ProtectedRoute> } />
                <Route path="/city-history" element={<ProtectedRoute>
                    <CityHistory/> </ProtectedRoute> } />
                <Route path="/register" element={<Register/>}/>
                <Route path="/city-actions" element={<ProtectedRoute>
                    <CityActionSelector/> </ProtectedRoute> }/>
                <Route path="/cities/create" element={<ProtectedRoute>
                    <CityForm/></ProtectedRoute>}/>
                <Route path="/cities/update/:id" element={<ProtectedRoute>
                    <CityUpdate/></ProtectedRoute>}/>
                <Route path="/cities/update" element={<ProtectedRoute>
                    <CitySelectForUpdate/></ProtectedRoute>}/>
                <Route path="/cities/delete" element={<ProtectedRoute>
                    <CityDelete/></ProtectedRoute>}/>
                <Route path="/cities/info/:id" element={<ProtectedRoute>
                    <CityInfo/></ProtectedRoute>}/>
                <Route path="/request-role-change" element={<ProtectedRoute>
                    <RoleChangeRequest/></ProtectedRoute>}/>
                <Route path="/role-requests" element={<ProtectedRoute>
                    <RoleRequestManager/></ProtectedRoute>}/>
                <Route path="/auth/callback" element={<CallbackPage />} />

            </Routes>
        </Router>
    );
};

export default App;
