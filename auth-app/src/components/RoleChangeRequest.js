import React, {useState, useEffect} from 'react';
import axios from 'axios';
import {useAuth} from '../AuthProvider';
import {useNavigate} from "react-router-dom";

const RoleChangeRequest = () => {
    const {token} = useAuth();
    const [currentRole, setCurrentRole] = useState('');
    const [isRequesting, setIsRequesting] = useState(false);
    const [message, setMessage] = useState('');
    const [userId, setUserId] = useState(null);
    const navigate = useNavigate();


    useEffect(() => {
        const fetchUserRole = async () => {
            try {
                const response = await axios.get('/api/users/current-user-info', {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                setUserId(response.data.id);
                setCurrentRole(response.data.role);
            } catch (error) {
                console.error('Error fetching current role:', error);
            }
        };

        if (token) {
            fetchUserRole();
        }
    }, [userId, token]);

    const handleRequestRoleChange = async () => {
        try {
            setIsRequesting(true);
            await axios.post(`/api/users/${userId}/role-request`, {
                role: 'ROLE_ADMIN',
            }, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            setMessage('Role change request submitted successfully!');
        } catch (error) {
            console.error('Error submitting role change request:', error);
            setMessage('Error submitting role change request');
        } finally {
            setIsRequesting(false);
        }
    };

    return (
        <div>
            <h2>Request Role Change</h2>
            <p>Current Role: {currentRole}</p>
            {message && <p>{message}</p>}
            {currentRole !== 'ROLE_ADMIN' ? (
                <button onClick={handleRequestRoleChange} disabled={isRequesting}>
                    {isRequesting ? 'Requesting...' : 'Request Admin Role'}
                </button>
            ) : (
                <p>You already have the highest role.</p>
            )}
            <button onClick={() => navigate('/city-actions')}>Back to Actions</button>

        </div>
    );
};

export default RoleChangeRequest;
