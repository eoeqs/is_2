import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';  // Используем useNavigate вместо useHistory
import { useAuth } from "../AuthProvider";

const CityDelete = ({ match }) => {
    const { token } = useAuth();
    const [city, setCity] = useState(null);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();  // Инициализация navigate

    useEffect(() => {
        const fetchCityData = async () => {
            try {
                const response = await axios.get(`/cities/${match.params.id}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                setCity(response.data);
                setLoading(false);
            } catch (error) {
                console.error('Error fetching city:', error);
                setLoading(false);
            }
        };

        fetchCityData();
    }, [token, match.params.id]);

    const handleDelete = async () => {
        try {
            await axios.delete(`/cities/${match.params.id}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            alert('City deleted successfully');
            navigate('/cities');
        } catch (error) {
            console.error('Error deleting city:', error);
        }
    };

    if (loading) return <div>Loading...</div>;

    return (
        <div>
            <h2>Delete City</h2>
            {city ? (
                <div>
                    <p>Are you sure you want to delete the city: {city.name}?</p>
                    <button onClick={handleDelete}>Delete</button>
                </div>
            ) : (
                <p>City not found.</p>
            )}
        </div>
    );
};

export default CityDelete;
