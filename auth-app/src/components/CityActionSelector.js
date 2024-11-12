import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { useAuth } from '../AuthProvider';
import ReactPaginate from 'react-paginate';

const CityActionSelector = () => {
    const { token } = useAuth();
    const [cities, setCities] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [citiesPerPage] = useState(10);
    const [selectedCityId, setSelectedCityId] = useState('');

    useEffect(() => {
        const fetchCities = async () => {
            try {
                const response = await axios.get('/cities', {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setCities(response.data);
            } catch (error) {
                console.error('Error fetching cities:', error);
            }
        };

        fetchCities();
    }, [token]);

    const indexOfLastCity = (currentPage + 1) * citiesPerPage;
    const indexOfFirstCity = indexOfLastCity - citiesPerPage;
    const currentCities = cities.slice(indexOfFirstCity, indexOfLastCity);

    const handlePageChange = ({ selected }) => {
        setCurrentPage(selected);
    };

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
            <div>
                <Link to="/cities/delete">
                    <button>Delete an Existing City</button>
                </Link>
            </div>

            <h3>City List</h3>
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Population</th>
                    <th>Area</th>
                    <th>Capital</th>
                    <th>Climate</th>
                    <th>Coordinates</th>
                    <th>Governor (Height)</th>
                    <th>Creation Date</th>
                    <th>Establishment Date</th>
                    <th>Meters Above Sea Level</th>
                    <th>Car Code</th>
                    <th>Agglomeration</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {currentCities.map((city) => (
                    <tr key={city.id}>
                        <td>{city.id}</td>
                        <td>{city.name}</td>
                        <td>{city.population}</td>
                        <td>{city.area}</td>
                        <td>{city.capital ? 'Yes' : 'No'}</td>
                        <td>{city.climate}</td>
                        <td>{city.coordinates ? `X: ${city.coordinates.x}, Y: ${city.coordinates.y}` : 'N/A'}</td>
                        <td>{city.governor ? city.governor.height : 'N/A'}</td>
                        <td>{city.creationDate}</td>
                        <td>{city.establishmentDate}</td>
                        <td>{city.metersAboveSeaLevel}</td>
                        <td>{city.carCode !== null ? city.carCode : 'N/A'}</td>
                        <td>{city.agglomeration}</td>
                        <td>
                            <Link to={`/cities/info/${city.id}`}>
                                <button>View Info</button>
                            </Link>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            <ReactPaginate
                previousLabel={<button className="pagination-button">Previous</button>}
                nextLabel={<button className="pagination-button">Next</button>}
                onPageChange={handlePageChange}
                containerClassName={"pagination"}
                activeClassName={"active"}
                pageClassName="page-item"
                pageLinkClassName="page-link"
                previousClassName="previous-item"
                nextClassName="next-item"
            />
        </div>
    );
};

export default CityActionSelector;
