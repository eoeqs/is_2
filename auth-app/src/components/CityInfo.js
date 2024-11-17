import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from "../AuthProvider";
import { useNavigate, useParams } from 'react-router-dom';

const CityInfo = () => {
    const { id } = useParams();
    const { token } = useAuth();
    const [city, setCity] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchCityData = async () => {
            try {
                const response = await axios.get(`/api/cities/${id}`, {
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
                    <p><strong>Governor (Height):</strong> {city.governor ? city.governor.height : 'N/A'}</p>
                    <p><strong>Creation Date:</strong> {city.creationDate}</p>
                    <p><strong>Establishment Date:</strong> {city.establishmentDate}</p>
                    <p><strong>Meters Above Sea Level:</strong> {city.metersAboveSeaLevel}</p>
                    <p><strong>Car Code:</strong> {city.carCode !== null ? city.carCode : 'N/A'}</p>
                    <p><strong>Agglomeration:</strong> {city.agglomeration}</p>
                </div>
            ) : (
                <p>City not found.</p>
            )}
            <button onClick={() => navigate('/city-actions')}>Back to Actions</button>
        </div>
    );
};

export default CityInfo;
