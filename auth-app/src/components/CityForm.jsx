import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from "../AuthProvider";
import {useNavigate} from "react-router-dom";

const CityForm = () => {
    const { token, userId } = useAuth(); // Получаем токен и id пользователя из контекста
    const [name, setName] = useState('');
    const [population, setPopulation] = useState('');
    const [area, setArea] = useState('');
    const [capital, setCapital] = useState(true);
    const [metersAboveSeaLevel, setMetersAboveSeaLevel] = useState('');
    const [carCode, setCarCode] = useState('');
    const [agglomeration, setAgglomeration] = useState('');
    const [climate, setClimate] = useState('RAIN_FOREST');
    const [coordinates, setCoordinates] = useState(null);
    const [governor, setGovernor] = useState(null);
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
            } catch (error) {
                console.error('Error fetching available coordinates or governors:', error);
            }
        };

        fetchData();
    }, [token]);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (population <= 0 || area <= 0 || !name.trim()) {
            alert('Please ensure all fields are filled correctly.');
            return;
        }
        const city = {
            name,
            population: Number(population),
            coordinates: coordinates ? { id: coordinates.id } : null,
            governor: governor ? { id: governor.id } : null,
            area: Number(area),
            capital,
            metersAboveSeaLevel: Number(metersAboveSeaLevel),
            carCode: Number(carCode),
            agglomeration: Number(agglomeration),
            climate,
            user: { id: userId },
        };
        console.log(city)
        try {
            const response = await axios.post('/cities', city, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            console.log('City created:', response.data);
            navigate('/city-actions');
        } catch (error) {
            console.error('Error creating city:', error);
        }
    };

    return (
        <div>
            <h2>Create City</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Name:</label>
                    <input
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                    />
                </div>

                <div>
                    <label>Population:</label>
                    <input
                        type="number"
                        value={population}
                        onChange={(e) => setPopulation(e.target.value)}
                        required
                    />
                </div>

                <div>
                    <label>Area:</label>
                    <input
                        type="number"
                        value={area}
                        onChange={(e) => setArea(e.target.value)}
                        required
                    />
                </div>

                <div>
                    <label>Capital:</label>
                    <input
                        type="checkbox"
                        checked={capital}
                        onChange={() => setCapital(!capital)}
                    />
                </div>

                <div>
                    <label>Meters Above Sea Level:</label>
                    <input
                        type="number"
                        value={metersAboveSeaLevel}
                        onChange={(e) => setMetersAboveSeaLevel(e.target.value)}
                    />
                </div>

                <div>
                    <label>Car Code (Optional):</label>
                    <input
                        type="number"
                        value={carCode}
                        onChange={(e) => setCarCode(e.target.value)}
                    />
                </div>

                <div>
                    <label>Agglomeration:</label>
                    <input
                        type="number"
                        value={agglomeration}
                        onChange={(e) => setAgglomeration(e.target.value)}
                        required
                    />
                </div>

                <div>
                    <label>Climate:</label>
                    <select
                        value={climate}
                        onChange={(e) => setClimate(e.target.value)}
                    >
                        <option value="RAIN_FOREST">Rain Forest</option>
                        <option value="MONSOON">Monsoon</option>
                        <option value="HUMIDCONTINENTAL">Humid Continental</option>
                    </select>
                </div>

                <div>
                    <label>Coordinates:</label>
                    <select
                        value={coordinates ? coordinates.id : ''}
                        onChange={(e) => setCoordinates(availableCoordinates.find(coord => coord.id === parseInt(e.target.value)))}
                    >
                        <option value="">Select Coordinates</option>
                        {availableCoordinates.map(coord => (
                            <option key={coord.id} value={coord.id}>
                                {`X: ${coord.x}, Y: ${coord.y}`}
                            </option>
                        ))}
                    </select>
                </div>

                <div>
                    <label>Governor:</label>
                    <select
                        value={governor ? governor.id : ''}
                        onChange={(e) => setGovernor(availableGovernors.find(gov => gov.id === parseInt(e.target.value)))}
                    >
                        <option value="">Select Governor</option>
                        {availableGovernors.map(gov => (
                            <option key={gov.id} value={gov.id}>
                                {`${gov.height} `}
                            </option>
                        ))}
                    </select>
                </div>

                <button type="submit">Create City</button>
                <div></div>
                <button onClick={() => navigate('/city-actions')}>Back to Actions</button>

            </form>
        </div>
    );
};

export default CityForm;
