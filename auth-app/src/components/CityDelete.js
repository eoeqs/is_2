import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from '../AuthProvider';
import { useNavigate } from 'react-router-dom';

const CityDelete = () => {
    const { token } = useAuth();  // Получаем токен из контекста
    const [cities, setCities] = useState([]);
    const [selectedCityId, setSelectedCityId] = useState('');
    const [isDeleting, setIsDeleting] = useState(false);
    const navigate = useNavigate();
    const [message, setMessage] = useState('');

    useEffect(() => {
        const fetchCities = async () => {
            try {
                const response = await axios.get('/cities/editable', {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
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

    const handleCityChange = (e) => {
        setSelectedCityId(e.target.value);
    };

    const handleDeleteCity = async () => {
        if (selectedCityId) {
            try {
                setIsDeleting(true);
                await axios.delete(`/cities/${selectedCityId}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                setMessage('City successfully deleted!');
                setTimeout(() => {
                    navigate('/city-actions');
                }, 3000);
            } catch (error) {
                setMessage('Error deleting city');
                console.error('Error deleting city:', error);
            } finally {
                setIsDeleting(false);
            }
        }
    };

    return (
        <div>
            <h2>Delete City</h2>
            <select onChange={handleCityChange} value={selectedCityId}>
                <option value="">Select a City to Delete</option>
                {cities.map((city) => (
                    <option key={city.id} value={city.id}>
                        {city.name}
                    </option>
                ))}
            </select>
            <button onClick={handleDeleteCity} disabled={!selectedCityId || isDeleting}>
                {isDeleting ? 'Deleting...' : 'Delete City'}
            </button>
            {message && <p>{message}</p>}

            <button onClick={() => navigate('/city-actions')}>Back to Actions</button>

        </div>
    );
};

export default CityDelete;
