import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from "../AuthProvider";

const CityInfo = ({ match }) => {
    const { token } = useAuth();
    const [city, setCity] = useState(null);
    const [loading, setLoading] = useState(true);

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

    if (loading) return <div>Loading...</div>;

    return (
        <div>
            <h2>City Details</h2>
            {city ? (
                <div>
                    <p>Name: {city.name}</p>
                    <p>Population: {city.population}</p>
                    <p>Area: {city.area}</p>
                    <p>Capital: {city.capital ? 'Yes' : 'No'}</p>
                    <p>Climate: {city.climate}</p>
                </div>
            ) : (
                <p>City not found.</p>
            )}
        </div>
    );
};

export default CityInfo;
