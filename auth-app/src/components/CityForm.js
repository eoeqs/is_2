import React, { useState } from 'react';
import axios from 'axios';

const CityForm = ({ city, onSubmit }) => {
    const [name, setName] = useState(city ? city.name : '');
    const [population, setPopulation] = useState(city ? city.population : '');
    const [area, setArea] = useState(city ? city.area : '');

    const handleSubmit = async (e) => {
        e.preventDefault();
        const cityData = { name, population, area };
        try {
            if (city) {
                await axios.put(`/api/cities/${city.id}`, cityData);
            } else {
                await axios.post('/api/cities', cityData);
            }
            onSubmit();
        } catch (error) {
            console.error('Error submitting city data:', error);
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <div>
                <label>Name:</label>
                <input type="text" value={name} onChange={(e) => setName(e.target.value)} required />
            </div>
            <div>
                <label>Population:</label>
                <input type="number" value={population} onChange={(e) => setPopulation(e.target.value)} required />
            </div>
            <div>
                <label>Area:</label>
                <input type="number" value={area} onChange={(e) => setArea(e.target.value)} required />
            </div>
            <button type="submit">{city ? 'Update City' : 'Create City'}</button>
        </form>
    );
};

export default CityForm;
