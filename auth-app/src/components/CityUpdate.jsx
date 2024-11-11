import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from "../AuthProvider";

const CityUpdate = ({ match }) => {
    const { token } = useAuth();
    const [city, setCity] = useState(null);
    const [name, setName] = useState('');
    const [population, setPopulation] = useState('');
    const [area, setArea] = useState('');
    const [capital, setCapital] = useState(true);
    const [climate, setClimate] = useState('RAIN_FOREST');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchCityData = async () => {
            try {
                const response = await axios.get(`/cities/${match.params.id}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                const cityData = response.data;
                setCity(cityData);
                setName(cityData.name);
                setPopulation(cityData.population);
                setArea(cityData.area);
                setCapital(cityData.capital);
                setClimate(cityData.climate);
                setLoading(false);
            } catch (error) {
                console.error('Error fetching city:', error);
                setLoading(false);
            }
        };

        fetchCityData();
    }, [token, match.params.id]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        const updatedCity = {
            ...city,
            name,
            population: Number(population),
            area: Number(area),
            capital,
            climate,
        };

        try {
            const response = await axios.put(`/cities/${match.params.id}`, updatedCity, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            console.log('City updated:', response.data);
        } catch (error) {
            console.error('Error updating city:', error);
        }
    };

    if (loading) return <div>Loading...</div>;

    return (
        <div>
            <h2>Update City</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Name:</label>
                    <input
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                    />
                </div>
                <div>
                    <label>Population:</label>
                    <input
                        type="number"
                        value={population}
                        onChange={(e) => setPopulation(e.target.value)}
                    />
                </div>
                <div>
                    <label>Area:</label>
                    <input
                        type="number"
                        value={area}
                        onChange={(e) => setArea(e.target.value)}
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
                <button type="submit">Update City</button>
            </form>
        </div>
    );
};

export default CityUpdate;
