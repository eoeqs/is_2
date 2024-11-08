import React, { useState, useEffect } from 'react';
import axios from 'axios';

const AdminPanel = () => {
    const [users, setUsers] = useState([]);
    const [selectedRole, setSelectedRole] = useState('USER');

    useEffect(() => {
        axios.get('/api/users')
            .then(response => setUsers(response.data))
            .catch(error => console.error('Error fetching users:', error));
    }, []);

    const handleRoleChange = (userId) => {
        axios.post(`/api/users/${userId}/role`, { role: selectedRole })
            .then(() => alert('Role updated successfully'))
            .catch(error => console.error('Error updating role:', error));
    };

    return (
        <div>
            <h1>Admin Panel</h1>
            <ul>
                {users.map(user => (
                    <li key={user.id}>
                        {user.username} - Current Role: {user.roles[0]}
                        <select value={selectedRole} onChange={(e) => setSelectedRole(e.target.value)}>
                            <option value="USER">USER</option>
                            <option value="ADMIN">ADMIN</option>
                        </select>
                        <button onClick={() => handleRoleChange(user.id)}>Update Role</button>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default AdminPanel;
