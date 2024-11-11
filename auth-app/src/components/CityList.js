import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { connectWebSocket } from '../webSocket';

const CityList = () => {
    const [cities, setCities] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);

    const fetchCities = async (page) => {
        try {
            const response = await axios.get(`/cities?page=${page}`);
            setCities(response.data.cities);
            setTotalPages(response.data.totalPages);
        } catch (error) {
            console.error('Error fetching cities:', error);
        }
    };

    useEffect(() => {
        const fetchData = async () => {
            try {
                await fetchCities(currentPage);
            } catch (error) {
                console.error('Error fetching data in useEffect:', error);
            }
        };
        fetchData();
    }, [currentPage]);

    useEffect(() => {
        const socket = connectWebSocket((updatedCities) => {
            setCities(updatedCities);
        });

        return () => socket.close();
    }, []);

    const handlePageChange = (newPage) => {
        setCurrentPage(newPage);
    };

    return (
        <div>
            <h1>City List</h1>
            <table>
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Population</th>
                    <th>Area</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {cities.map((city) => (
                    <tr key={city.id}>
                        <td>{city.name}</td>
                        <td>{city.population}</td>
                        <td>{city.area}</td>
                        <td>
                            <button>Edit</button>
                            <button>Delete</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
            <div className="pagination">
                {Array.from({ length: totalPages }, (_, index) => (
                    <button
                        key={index}
                        onClick={() => handlePageChange(index + 1)}
                        className={currentPage === index + 1 ? 'active' : ''}
                    >
                        {index + 1}
                    </button>
                ))}
            </div>
        </div>
    );
};

export default CityList;
