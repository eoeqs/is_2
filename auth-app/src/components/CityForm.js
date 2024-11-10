import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {useAuth} from "../AuthContext";

const CityForm = ({ cityId, onSave }) => {

    const { token } = useAuth();

    useEffect(() => {
        if (cityId && token) {
            axios.get(`/cities/${cityId}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            })
                .then(response => setCity(response.data))
                .catch(error => console.error("Error fetching city:", error));
        }
    }, [cityId, token]);

    const [city, setCity] = useState({
        name: '',
        coordinates: { x: '', y: '' },
        area: '',
        population: '',
        establishmentDate: '',
        capital: false,
        metersAboveSeaLevel: '',
        carCode: '',
        agglomeration: '',
        climate: 'RAIN_FOREST',
        governor: { height: '' }
    });

    const [error, setError] = useState('');

    useEffect(() => {
        if (cityId) {
            axios.get(`/cities/${cityId}`)
                .then(response => setCity(response.data))
                .catch(error => console.error("Error fetching city:", error));
        }
    }, [cityId]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setCity(prevState => ({
            ...prevState,
            [name]: value
        }));
    };

    const handleNestedChange = (e, field, nestedField) => {
        const { value } = e.target;
        setCity(prevState => ({
            ...prevState,
            [field]: {
                ...prevState[field],
                [nestedField]: value
            }
        }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (validateForm()) {
            if (cityId) {
                axios.put(`/cities/${cityId}`, city)
                    .then(onSave)
                    .catch(error => console.error("Error updating city:", error));
            } else {
                axios.post('/cities', city)
                    .then(onSave)
                    .catch(error => console.error("Error creating city:", error));
            }
        }
    };

    const validateForm = () => {
        if (!city.name || city.name.trim() === '') {
            setError('Name is required.');
            return false;
        }
        if (!city.coordinates.x || city.coordinates.x <= -586) {
            setError('X coordinate must be greater than -586.');
            return false;
        }
        if (!city.population || city.population <= 0) {
            setError('Population must be greater than 0.');
            return false;
        }
        if (city.carCode && (city.carCode <= 0 || city.carCode > 1000)) {
            setError('Car code must be between 1 and 1000.');
            return false;
        }
        if (city.capital === null) {
            setError('Capital status is required.');
            return false;
        }
        if (!city.governor.height || city.governor.height <= 0) {
            setError('Governor height must be greater than 0.');
            return false;
        }
        setError('');
        return true;
    };

    return (
        <div>
            <h2>{cityId ? 'Edit City' : 'Create City'}</h2>
            <form onSubmit={handleSubmit}>
                {error && <p style={{ color: 'red' }}>{error}</p>}
                <div>
                    <label>Name:</label>
                    <input type="text" name="name" value={city.name} onChange={handleInputChange} required />
                </div>
                <div>
                    <label>Coordinates X:</label>
                    <input type="number" name="x" value={city.coordinates.x} onChange={(e) => handleNestedChange(e, 'coordinates', 'x')} required />
                    <label>Coordinates Y:</label>
                    <input type="number" name="y" value={city.coordinates.y} onChange={(e) => handleNestedChange(e, 'coordinates', 'y')} required />
                </div>
                <div>
                    <label>Area:</label>
                    <input type="number" name="area" value={city.area} onChange={handleInputChange} required />
                </div>
                <div>
                    <label>Population:</label>
                    <input type="number" name="population" value={city.population} onChange={handleInputChange} required />
                </div>
                <div>
                    <label>Establishment Date:</label>
                    <input type="datetime-local" name="establishmentDate" value={city.establishmentDate} onChange={handleInputChange} />
                </div>
                <div>
                    <label>Capital:</label>
                    <input type="checkbox" name="capital" checked={city.capital} onChange={(e) => setCity(prevState => ({ ...prevState, capital: e.target.checked }))} />
                </div>
                <div>
                    <label>Meters Above Sea Level:</label>
                    <input type="number" name="metersAboveSeaLevel" value={city.metersAboveSeaLevel} onChange={handleInputChange} />
                </div>
                <div>
                    <label>Car Code:</label>
                    <input type="number" name="carCode" value={city.carCode} onChange={handleInputChange} />
                </div>
                <div>
                    <label>Agglomeration:</label>
                    <input type="number" name="agglomeration" value={city.agglomeration} onChange={handleInputChange} required />
                </div>
                <div>
                    <label>Climate:</label>
                    <select name="climate" value={city.climate} onChange={handleInputChange}>
                        <option value="RAIN_FOREST">Rain Forest</option>
                        <option value="MONSOON">Monsoon</option>
                        <option value="HUMIDCONTINENTAL">Humid Continental</option>
                    </select>
                </div>
                <div>
                    <label>Governor Height:</label>
                    <input type="number" name="height" value={city.governor.height} onChange={(e) => handleNestedChange(e, 'governor', 'height')} required />
                </div>
                <button type="submit">{cityId ? 'Update City' : 'Create City'}</button>
            </form>
        </div>
    );
};

export default CityForm;
