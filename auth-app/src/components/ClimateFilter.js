import React, { useState } from 'react';
import axios from 'axios';
import { useAuth } from '../AuthProvider';

const ClimateFilter = () => {
    const { token } = useAuth();
    const [climateThreshold, setClimateThreshold] = useState('RAIN_FOREST');
    const [filterType, setFilterType] = useState(null);
    const [filteredCities, setFilteredCities] = useState([]);

    const handleFilter = async () => {
        try {
            const response = await axios.get(`/cities/filter-by-climate`, {
                headers: { Authorization: `Bearer ${token}` },
                params: {
                    climate: climateThreshold,
                    filterType: filterType
                }
            });
            setFilteredCities(response.data);
        } catch (error) {
            console.error('Error fetching filtered cities:', error);
        }
    };

    return (
        <div>
            <h3>Filter Cities by Climate</h3>
            <label>
                Select Climate Threshold:
                <select value={climateThreshold} onChange={(e) => setClimateThreshold(e.target.value)}>
                    <option value="RAIN_FOREST">Rain Forest</option>
                    <option value="MONSOON">Monsoon</option>
                    <option value="HUMIDCONTINENTAL">Humid Continental</option>
                </select>
            </label>

            <div>
                <label>
                    <input
                        type="radio"
                        value="less"
                        checked={filterType === 'less'}
                        onChange={() => setFilterType('less')}
                    />
                    Less than selected climate
                </label>
                <label>
                    <input
                        type="radio"
                        value="more"
                        checked={filterType === 'more'}
                        onChange={() => setFilterType('more')}
                    />
                    More than selected climate
                </label>
            </div>

            <button onClick={handleFilter}>Filter Cities</button>

            <h4>Cities matching the filter:</h4>
            <ul>
                {filteredCities.map((city) => (
                    <li key={city.id}>
                        {city.name} - Climate: {city.climate}
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default ClimateFilter;
