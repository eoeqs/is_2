import React, {useState, useEffect} from 'react';
import axios from 'axios';
import {useAuth} from '../AuthProvider';
import {useNavigate} from 'react-router-dom';

const CityDelete = () => {
        const {token} = useAuth();
        const [cities, setCities] = useState([]);
        const [selectedCityId, setSelectedCityId] = useState('');
        const [isDeleting, setIsDeleting] = useState(false);
        const navigate = useNavigate();
        const [message, setMessage] = useState('');
        const [reassignCityId, setReassignCityId] = useState('');
        const [error, setError] = useState('');

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
                    setError('Error fetching cities');
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
        const handleReassignCityChange = (e) => {
            setReassignCityId(e.target.value);
        };

        const handleDeleteCity = async () => {
            if (!selectedCityId || !reassignCityId) {
                setMessage('Please select a city and a reassignment target');
                return;
            }

            if (selectedCityId === reassignCityId) {
                setMessage('The city to delete and the reassignment target cannot be the same');
                return;
            }
            try {
                setIsDeleting(true);
                await axios.delete(`/cities/${selectedCityId}/reassign`, {
                    params: {reassignToCityId: reassignCityId},

                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                setMessage('City successfully deleted and dependencies reassigned!');
                setTimeout(() => {
                    navigate('/city-actions');
                }, 3000);
            } catch (error) {
                setMessage('Error deleting city');
                console.error('Error deleting city:', error);
            } finally {
                setIsDeleting(false);
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
                <select onChange={handleReassignCityChange} value={reassignCityId}>
                    <option value="">Select a City to Reassign Dependencies</option>
                    {cities.map((city) => (
                        <option key={city.id} value={city.id}>
                            {city.name}
                        </option>
                    ))}
                </select>
                <button onClick={handleDeleteCity} disabled={!selectedCityId || !reassignCityId || isDeleting}>
                    {isDeleting ? 'Deleting...' : 'Delete City'}
                </button>
                {message && <p>{message}</p>}

                <button onClick={() => navigate('/city-actions')}>Back to Actions</button>

            </div>
        );
    }
;

export default CityDelete;
