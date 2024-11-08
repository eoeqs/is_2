import React from 'react';
import { useNavigate } from 'react-router-dom';


const Home = () => {
    const navigate = useNavigate();

    const handleLoginClick = () => {
        navigate('/login');
    };

    const handleRegisterClick = () => {
        navigate('/register');
    };

    return (
        <div>
            <h1>Welcome to the City Management System</h1>
            <p>This application allows you to manage cities effectively.</p>
            <button onClick={handleLoginClick}>Login</button>
            <button onClick={handleRegisterClick}>Register</button>
        </div>
    );
};

export default Home;
