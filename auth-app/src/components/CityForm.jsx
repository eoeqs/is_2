import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from "../AuthProvider";

const CityForm = ({ cityId }) => {
    const { token, userId } = useAuth();
    const [action, setAction] = useState(cityId ? 'update' : 'create');
    const [city, setCity] = useState({
        name: '',
        population: '',
        area: '',
        capital: true,
        metersAboveSeaLevel: '',
        carCode: '',
        agglomeration: '',
        climate: 'RAIN_FOREST',
        coordinates: null,
        governor: null,
        user: { id: userId },
    });
    const [availableCoordinates, setAvailableCoordinates] = useState([]);
    const [availableGovernors, setAvailableGovernors] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const coordinatesResponse = await axios.get('/coordinates', {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                const governorsResponse = await axios.get('/humans', {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                setAvailableCoordinates(coordinatesResponse.data);
                setAvailableGovernors(governorsResponse.data);

                if (cityId) {
                    const cityResponse = await axios.get(`/cities/${cityId}`, {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    });
                    setCity(cityResponse.data);
                }
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        };

        fetchData();
    }, [token, cityId]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = action === 'create'
                ? await axios.post('/cities', city, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                })
                : await axios.put(`/cities/${cityId}`, city, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
            console.log(`${action === 'create' ? 'Created' : 'Updated'} city:`, response.data);
        } catch (error) {
            console.error(`Error ${action === 'create' ? 'creating' : 'updating'} city:`, error);
        }
    };

    const handleDelete = async () => {
        try {
            await axios.delete(`/cities/${cityId}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            console.log('Deleted city:', cityId);
        } catch (error) {
            console.error('Error deleting city:', error);
        }
    };

    return (
        <div>
            <h2>{action === 'create' ? 'Create City' : 'Edit City'}</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Name:</label>
                    <input
                        type="text"
                        value={city.name}
                        onChange={(e) => setCity({ ...city, name: e.target.value })}
                        required
                    />
                </div>

                <div>
                    <label>Population:</label>
                    <input
                        type="number"
                        value={city.population}
                        onChange={(e) => setCity({ ...city, population: e.target.value })}
                        required
                    />
                </div>

                <div>
                    <label>Area:</label>
                    <input
                        type="number"
                        value={city.area}
                        onChange={(e) => setCity({ ...city, area: e.target.value })}
                        required
                    />
                </div>

                <div>
                    <label>Capital:</label>
                    <input
                        type="checkbox"
                        checked={city.capital}
                        onChange={() => setCity({ ...city, capital: !city.capital })}
                    />
                </div>

                <div>
                    <label>Meters Above Sea Level:</label>
                    <input
                        type="number"
                        value={city.metersAboveSeaLevel}
                        onChange={(e) => setCity({ ...city, metersAboveSeaLevel: e.target.value })}
                    />
                </div>

                <div>
                    <label>Car Code:</label>
                    <input
                        type="number"
                        value={city.carCode}
                        onChange={(e) => setCity({ ...city, carCode: e.target.value })}
                    />
                </div>

                <div>
                    <label>Agglomeration:</label>
                    <input
                        type="number"
                        value={city.agglomeration}
                        onChange={(e) => setCity({ ...city, agglomeration: e.target.value })}
                        required
                    />
                </div>

                <div>
                    <label>Climate:</label>
                    <select
                        value={city.climate}
                        onChange={(e) => setCity({ ...city, climate: e.target.value })}
                    >
                        <option value="RAIN_FOREST">Rain Forest</option>
                        <option value="MONSOON">Monsoon</option>
                        <option value="HUMIDCONTINENTAL">Humid Continental</option>
                    </select>
                </div>

                <div>
                    <label>Coordinates:</label>
                    <select
                        value={city.coordinates ? city.coordinates.id : ''}
                        onChange={(e) => setCity({ ...city, coordinates: availableCoordinates.find(coord => coord.id === parseInt(e.target.value)) })}
                    >
                        <option value="">Select Coordinates</option>
                        {availableCoordinates.map(coord => (
                            <option key={coord.id} value={coord.id}>
                                X: {coord.x}, Y: {coord.y}
                            </option>
                        ))}
                    </select>
                </div>

                <div>
                    <label>Governor:</label>
                    <select
                        value={city.governor ? city.governor.id : ''}
                        onChange={(e) => setCity({ ...city, governor: availableGovernors.find(gov => gov.id === parseInt(e.target.value)) })}
                    >
                        <option value="">Select Governor</option>
                        {availableGovernors.map(gov => (
                            <option key={gov.id} value={gov.id}>
                                {gov.height}
                            </option>
                        ))}
                    </select>
                </div>

                <button type="submit">{action === 'create' ? 'Create' : 'Update'} City</button>
            </form>

            {action === 'update' && (
                <button onClick={handleDelete} style={{ marginTop: '10px', backgroundColor: 'red' }}>
                    Delete City
                </button>
            )}
        </div>
    );
};

export default CityForm;
