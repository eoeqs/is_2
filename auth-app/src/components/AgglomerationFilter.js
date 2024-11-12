import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from '../AuthProvider';

const AgglomerationFilter = () => {
    const { token } = useAuth();
    const [agglomerations, setAgglomerations] = useState([]);

    useEffect(() => {
        const fetchAgglomerations = async () => {
            try {
                const response = await axios.get('/cities', {
                    headers: { Authorization: `Bearer ${token}` },
                });

                const agglomerationValues = response.data.map((city) => city.agglomeration);

                const uniqueAgglomerations = [...new Set(agglomerationValues.filter(Boolean))];

                setAgglomerations(uniqueAgglomerations);
            } catch (error) {
                console.error('Error fetching agglomerations:', error);
            }
        };

        fetchAgglomerations();
    }, [token]);

    return (
        <div>
            <h3>Unique Agglomerations</h3>
            <ul>
                {agglomerations.length > 0 ? (
                    agglomerations.map((agglomeration, index) => (
                        <li key={index}>{agglomeration}</li>
                    ))
                ) : (
                    <li>No agglomerations found.</li>
                )}
            </ul>
        </div>
    );
};

export default AgglomerationFilter;
