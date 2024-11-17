import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { useAuth } from '../AuthProvider';

const CitySelectForUpdate = () => {
    const { token } = useAuth();
    const [cities, setCities] = useState([]);
    const [selectedCityId, setSelectedCityId] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchCities = async () => {
            try {
                const response = await axios.get('/api/cities/editable', {
                    headers: { Authorization: `Bearer ${token}` }
                });
                setCities(response.data);
            } catch (error) {
                console.error('Error fetching cities:', error);
            }
        };

        if (token) {
            fetchCities();
        }
    }, [token]);

    const handleCitySelection = (e) => {
        setSelectedCityId(e.target.value);
    };

    const handleProceed = () => {
        if (selectedCityId) {
            navigate(`/cities/update/${selectedCityId}`);
        }
    };

    return (
        <div>
            <h2>Select a City to Update</h2>
            <select onChange={handleCitySelection} value={selectedCityId || ''}>
                <option value="">Select a city</option>
                {cities.map((city) => (
                    <option key={city.id} value={city.id}>
                        {city.name}
                    </option>
                ))}
            </select>
            <button onClick={handleProceed} disabled={!selectedCityId}>
                Proceed to Update
            </button>
            <button onClick={() => navigate('/city-actions')}>Back to Actions</button>

        </div>
    );
};

export default CitySelectForUpdate;
