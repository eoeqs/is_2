import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { useAuth } from '../AuthProvider';  // Импортируем useAuth

const CityActionSelector = () => {
    const { token } = useAuth();  // Получаем токен из контекста
    const [cities, setCities] = useState([]);
    const [selectedCityId, setSelectedCityId] = useState('');

    useEffect(() => {
        const fetchCities = async () => {
            try {
                // Передаем токен в заголовках запроса
                const response = await axios.get('/cities', {
                    headers: {
                        Authorization: `Bearer ${token}`,  // Добавляем токен
                    },
                });
                setCities(response.data);  // Заполняем список городов
            } catch (error) {
                console.error('Error fetching cities:', error);
            }
        };

        if (token) {
            fetchCities();  // Загружаем города, если есть токен
        }
    }, [token]);

    const handleCityChange = (e) => {
        setSelectedCityId(e.target.value);
    };

    return (
        <div>
            <h2>What would you like to do?</h2>
            <div>
                <Link to="/cities/create">
                    <button>Create a New City</button>
                </Link>
            </div>
            <div>
                <Link to="/cities/update">
                    <button>Update an Existing City</button>
                </Link>
            </div>
            <div>
                <select onChange={handleCityChange} value={selectedCityId}>
                    <option value="">Select a City</option>
                    {cities.map((city) => (
                        <option key={city.id} value={city.id}>
                            {city.name}
                        </option>
                    ))}
                </select>
                <Link to={`/cities/info/${selectedCityId}`}>
                    <button disabled={!selectedCityId}>Show City Info</button>
                </Link>
            </div>
        </div>
    );
};

export default CityActionSelector;
