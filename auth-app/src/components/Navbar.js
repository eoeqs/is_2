import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

const Navigation = ({ user, setUser }) => {
    const navigate = useNavigate();
    const [isAdmin, setIsAdmin] = useState(false);
    const [stompClient, setStompClient] = useState(null);

    useEffect(() => {
        if (user && user.roles.includes('ADMIN')) {
            setIsAdmin(true);
        }

        const socket = new SockJS('http://localhost:8080/ws');
        const client = Stomp.over(socket);
        client.connect({}, () => {
            client.subscribe('/topic/admin-updates', message => {
                console.log('Received WebSocket message:', message.body);
            });
        });
        setStompClient(client);

        return () => {
            if (stompClient) stompClient.disconnect();
        };
    }, [user]);

    const handleLogout = async () => {
        try {
            await axios.post('http://localhost:8080/api/users/logout');
            setUser(null);
            navigate('/login');
        } catch (error) {
            console.error('Logout error:', error);
        }
    };

    return (
        <nav>
            <ul>
                <li><Link to="/">Home</Link></li>
                {user && (
                    <>
                        {isAdmin && <li><Link to="/admin">Admin Panel</Link></li>}
                        <li><button onClick={handleLogout}>Logout</button></li>
                    </>
                )}
            </ul>
        </nav>
    );
};

export default Navigation;
