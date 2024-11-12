import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {Link, useNavigate} from 'react-router-dom';
import { useAuth } from '../AuthProvider';

const CityActionSelector = () => {
    const navigate = useNavigate();

    const { token, logout } = useAuth();
    const [cities, setCities] = useState([]);
    const [selectedCityId, setSelectedCityId] = useState('');

    useEffect(() => {
        const fetchCities = async () => {
            try {
                const response = await axios.get('/cities', {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                setCities(response.data);
            } catch (error) {
                console.error('Error fetching cities:', error);
            }
        };

        fetchCities();
    }, [token]);

    const handleCityChange = (e) => {
        setSelectedCityId(e.target.value);
    };
    const handleLogout = () => {
        logout();
        navigate('/');
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
            <div>
                <Link to="/cities/delete">
                    <button>Delete an Existing City</button>
                </Link>
            </div>

            <div>
                <button onClick={handleLogout}>Logout</button>
            </div>
        </div>
    );
};

export default CityActionSelector;
