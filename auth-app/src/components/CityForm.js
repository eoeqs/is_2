import React, {useState, useEffect} from 'react';
import axios from 'axios';
import {useAuth} from "../AuthProvider";
import {useNavigate} from "react-router-dom";

const CityForm = () => {
    const {token, userId} = useAuth();
    const [name, setName] = useState('');
    const [population, setPopulation] = useState('');
    const [area, setArea] = useState('');
    const [capital, setCapital] = useState(true);
    const [metersAboveSeaLevel, setMetersAboveSeaLevel] = useState('');
    const [carCode, setCarCode] = useState('');
    const [agglomeration, setAgglomeration] = useState('');
    const [climate, setClimate] = useState('RAIN_FOREST');

    const [useCustomCoordinates, setUseCustomCoordinates] = useState(false);
    const [coordinates, setCoordinates] = useState(null);
    const [customX, setCustomX] = useState('');
    const [customY, setCustomY] = useState('');
    const [availableCoordinates, setAvailableCoordinates] = useState([]);

    const [useCustomGovernor, setUseCustomGovernor] = useState(false);
    const [governor, setGovernor] = useState(null);
    const [customHeight, setCustomHeight] = useState('');
    const [availableGovernors, setAvailableGovernors] = useState([]);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchData = async () => {
            try {
                const coordinatesResponse = await axios.get('/coordinates', {
                    headers: {Authorization: `Bearer ${token}`},
                });
                const governorsResponse = await axios.get('/humans', {
                    headers: {Authorization: `Bearer ${token}`},
                });
                setAvailableCoordinates(coordinatesResponse.data);
                setAvailableGovernors(governorsResponse.data);
            } catch (error) {
                setError('Error fetching available coordinates or governors');
                console.error('Error fetching available coordinates or governors:', error);
            }
        };

        fetchData();
    }, [token]);

    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!name.trim()) {
            setError('Name cannot be empty.');
            return;
        }
        if (!coordinates && !useCustomCoordinates) {
            setError('Coordinates cannot be null.');
            return;
        }
        if (useCustomCoordinates && (customX === '' || customY === '')) {
            setError('Custom coordinates must have valid X and Y values.');
            return;
        }
        if (area <= 0) {
            setError('Area must be greater than 0.');
            return;
        }
        if (population === '' || population <= 0) {
            setError('Population must be greater than 0.');
            return;
        }
        if (capital === null) {
            setError('Capital cannot be null.');
            return;
        }
        if (carCode !== '' && (carCode <= 0 || carCode > 1000)) {
            setError('Car Code must be between 1 and 1000 or left blank.');
            return;
        }
        if (agglomeration <= 0) {
            setError('Agglomeration must be greater than 0.');
            return;
        }
        if (!climate) {
            setError('Climate cannot be null.');
            return;
        }
        if (!governor && !useCustomGovernor) {
            setError('Governor cannot be null.');
            return;
        }
        if (useCustomGovernor && (customHeight === '' || customHeight <= 0)) {
            setError('Custom governor height must be greater than 0.');
            return;
        }
        if (population <= 0 || area <= 0 || !name.trim()) {
            setError('Please ensure all fields are filled correctly.');
            return;
        }
        setError('');


        try {
            let coordinatesId = coordinates ? coordinates.id : null;
            if (useCustomCoordinates) {
                const newCoordinates = {x: Number(customX), y: Number(customY)};
                const coordinatesResponse = await axios.post('/coordinates', newCoordinates, {
                    headers: {Authorization: `Bearer ${token}`},
                });
                coordinatesId = coordinatesResponse.data.id;
            }

            let governorId = governor ? governor.id : null;
            if (useCustomGovernor) {
                const newGovernor = {height: Number(customHeight)};
                const governorResponse = await axios.post('/humans', newGovernor, {
                    headers: {Authorization: `Bearer ${token}`},
                });
                governorId = governorResponse.data.id;
            }

            const city = {
                name,
                population: Number(population),
                coordinates: coordinatesId ? {id: coordinatesId} : null,
                governor: governorId ? {id: governorId} : null,
                area: Number(area),
                capital,
                metersAboveSeaLevel: Number(metersAboveSeaLevel),
                carCode: Number(carCode),
                agglomeration: Number(agglomeration),
                climate,
                user: {id: userId},
            };

            const response = await axios.post('/cities', city, {
                headers: {Authorization: `Bearer ${token}`},
            });
            console.log('City created:', response.data);
            navigate('/city-actions');
        } catch (error) {
            setError('Error creating city');
            console.error('Error creating city:', error);
        }
    };

    return (
        <div>
            <h2>Create City</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Name:</label>
                    <input type="text" value={name} onChange={(e) => setName(e.target.value)} required/>
                </div>

                <div>
                    <label>Population:</label>
                    <input type="number" value={population} onChange={(e) => setPopulation(e.target.value)} required/>
                </div>

                <div>
                    <label>Area:</label>
                    <input type="number" value={area} onChange={(e) => setArea(e.target.value)} required/>
                </div>

                <div>
                    <label>Capital:</label>
                    <input type="checkbox" checked={capital} onChange={() => setCapital(!capital)}/>
                </div>

                <div>
                    <label>Meters Above Sea Level:</label>
                    <input type="number" value={metersAboveSeaLevel}
                           onChange={(e) => setMetersAboveSeaLevel(e.target.value)}/>
                </div>

                <div>
                    <label>Car Code (Optional):</label>
                    <input type="number" value={carCode} onChange={(e) => setCarCode(e.target.value)}/>
                </div>

                <div>
                    <label>Agglomeration:</label>
                    <input type="number" value={agglomeration} onChange={(e) => setAgglomeration(e.target.value)}
                           required/>
                </div>

                <div>
                    <label>Climate:</label>
                    <select value={climate} onChange={(e) => setClimate(e.target.value)}>
                        <option value="RAIN_FOREST">Rain Forest</option>
                        <option value="MONSOON">Monsoon</option>
                        <option value="HUMIDCONTINENTAL">Humid Continental</option>
                    </select>
                </div>

                <div>
                    <label>Coordinates:</label>
                    <div>
                        <label>
                            <input type="radio" checked={!useCustomCoordinates}
                                   onChange={() => setUseCustomCoordinates(false)}/>
                            Select Coordinates
                        </label>
                        <label>
                            <input type="radio" checked={useCustomCoordinates}
                                   onChange={() => setUseCustomCoordinates(true)}/>
                            Enter Custom Coordinates
                        </label>
                    </div>
                    {useCustomCoordinates ? (
                        <div>
                            <label>X:</label>
                            <input type="number" value={customX} onChange={(e) => setCustomX(e.target.value)} required/>
                            <label>Y:</label>
                            <input type="number" value={customY} onChange={(e) => setCustomY(e.target.value)} required/>
                        </div>
                    ) : (
                        <select value={coordinates ? coordinates.id : ''}
                                onChange={(e) => setCoordinates(availableCoordinates.find(coord => coord.id === parseInt(e.target.value)))}>
                            <option value="">Select Coordinates</option>
                            {availableCoordinates.map(coord => (
                                <option key={coord.id} value={coord.id}>
                                    {`X: ${coord.x}, Y: ${coord.y}`}
                                </option>
                            ))}
                        </select>
                    )}
                </div>

                <div>
                    <label>Governor:</label>
                    <div>
                        <label>
                            <input type="radio" checked={!useCustomGovernor}
                                   onChange={() => setUseCustomGovernor(false)}/>
                            Select Governor
                        </label>
                        <label>
                            <input type="radio" checked={useCustomGovernor}
                                   onChange={() => setUseCustomGovernor(true)}/>
                            Enter Custom Governor
                        </label>
                    </div>
                    {useCustomGovernor ? (
                        <div>
                            <label>Height:</label>
                            <input type="number" value={customHeight} onChange={(e) => setCustomHeight(e.target.value)}
                                   required/>
                        </div>
                    ) : (
                        <select value={governor ? governor.id : ''}
                                onChange={(e) => setGovernor(availableGovernors.find(gov => gov.id === parseInt(e.target.value)))}>
                            <option value="">Select Governor</option>
                            {availableGovernors.map(gov => (
                                <option key={gov.id} value={gov.id}>
                                    {`${gov.height} cm`}
                                </option>
                            ))}
                        </select>
                    )}
                </div>

                <button type="submit">Create City</button>
                <button type="button" onClick={() => navigate('/city-actions')}>Back to Actions</button>
            </form>
            {error && (
                <div style={{ color: 'red', padding: '10px', border: '1px solid red', borderRadius: '5px' }}>
                    {error}
                </div>
            )}
        </div>
    );
};

export default CityForm;
