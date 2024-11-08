import React from 'react';
import { Link } from 'react-router-dom';

const Navigation = ({ user, onLogout }) => (
    <nav>
        <Link to="/">Home</Link>
        {user && user.roles.includes('ADMIN') && <Link to="/admin">Admin Panel</Link>}
        <button onClick={onLogout}>Logout</button>
    </nav>
);

export default Navigation;
