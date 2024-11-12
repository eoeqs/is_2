import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from "../AuthProvider";
import { useParams } from 'react-router-dom';

const CityInfo = () => {
    const { id } = useParams();  // Получаем ID из URL
    const { token } = useAuth();
    const [city, setCity] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchCityData = async () => {
            try {
                const response = await axios.get(`/cities/${id}`, {
                    headers: { Authorization: `Bearer ${token}` }
                });
                setCity(response.data);
                setLoading(false);
            } catch (error) {
                console.error('Error fetching city:', error);
                setError('City not found or error occurred');
                setLoading(false);
            }
        };

        if (token) {
            fetchCityData();
        }
    }, [id, token]);

    if (loading) return <div>Loading...</div>;
    if (error) return <div>{error}</div>;

    return (
        <div>
            <h2>City Details</h2>
            {city ? (
                <div>
                    <p><strong>Name:</strong> {city.name}</p>
                    <p><strong>Population:</strong> {city.population}</p>
                    <p><strong>Area:</strong> {city.area}</p>
                    <p><strong>Capital:</strong> {city.capital ? 'Yes' : 'No'}</p>
                    <p><strong>Climate:</strong> {city.climate}</p>
                    <p><strong>Coordinates:</strong> {city.coordinates ? `X: ${city.coordinates.x}, Y: ${city.coordinates.y}` : 'N/A'}</p>
                    <p><strong>Governor (height):</strong> {city.governor ? city.governor.height : 'N/A'}</p>
                </div>
            ) : (
                <p>City not found.</p>
            )}
        </div>
    );
};

export default CityInfo;
