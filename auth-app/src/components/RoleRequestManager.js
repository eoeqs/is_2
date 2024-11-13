import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from '../AuthProvider';
import { useNavigate } from 'react-router-dom';

const RoleRequestManager = () => {
    const { token } = useAuth();
    const [roleRequests, setRoleRequests] = useState([]);
    const [message, setMessage] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const fetchRoleRequests = async () => {
            try {
                const response = await axios.get('/api/users/role-requests', {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                setRoleRequests(response.data);
            } catch (error) {
                console.error('Error fetching role requests:', error);
            }
        };

        if (token) {
            fetchRoleRequests();
        }
    }, [token]);

    const handleApproveRequest = async (requestId) => {
        try {
            await axios.post(`/api/users/role-requests/${requestId}/approve`, {}, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            setMessage('Role change request approved successfully!');
            setRoleRequests(roleRequests.filter(request => request.id !== requestId));
        } catch (error) {
            console.error('Error approving role change request:', error);
            setMessage('Error approving role change request');
        }
    };

    const handleRejectRequest = async (requestId) => {
        try {
            await axios.post(`/api/users/role-requests/${requestId}/reject`, {}, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            setMessage('Role change request rejected successfully!');
            setRoleRequests(roleRequests.filter(request => request.id !== requestId));
        } catch (error) {
            console.error('Error rejecting role change request:', error);
            setMessage('Error rejecting role change request');
        }
    };

    return (
        <div>
            <h2>Role Change Requests</h2>
            {message && <p>{message}</p>}
            {roleRequests.length === 0 ? (
                <p>No pending role change requests.</p>
            ) : (
                <table>
                    <thead>
                    <tr>
                        <th>User ID</th>
                        <th>Username</th>
                        <th>Requested Role</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {roleRequests.map((request) => (
                        <tr key={request.id}>
                            <td>{request.user.id}</td>
                            <td>{request.user.username}</td>
                            <td>{request.requestedRole}</td>
                            <td>
                                <button onClick={() => handleApproveRequest(request.id)}>
                                    Approve
                                </button>
                                <button onClick={() => handleRejectRequest(request.id)}>
                                    Reject
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
            <button onClick={() => navigate('/city-actions')}>Back to Actions</button>
        </div>
    );
};

export default RoleRequestManager;
