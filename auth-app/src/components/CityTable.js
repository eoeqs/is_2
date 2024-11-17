import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from "../AuthProvider";
import CityEditDialog from "./CityEditDialog";

const CityTable = () => {
    const { token, userId, isAdmin } = useAuth();
    const [cities, setCities] = useState([]);
    const [selectedCity, setSelectedCity] = useState(null);
    const [showEditDialog, setShowEditDialog] = useState(false);

    useEffect(() => {
        const fetchCities = async () => {
            try {
                const response = await axios.get('/api/cities', {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setCities(response.data);
            } catch (error) {
                console.error('Error fetching cities:', error);
            }
        };

        fetchCities();
    }, [token]);

    const handleCityUpdate = (updatedCity) => {
        setCities(prevCities =>
            prevCities.map(city => city.id === updatedCity.id ? updatedCity : city)
        );
    };

    const handleEdit = (city) => {
        setSelectedCity(city);
        setShowEditDialog(true);
    };

    const handleDelete = async (cityId) => {
        if (window.confirm("Are you sure you want to delete this city?")) {
            try {
                await axios.delete(`/api/cities/${cityId}`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setCities(cities.filter(city => city.id !== cityId));
            } catch (error) {
                console.error('Error deleting city:', error);
            }
        }
    };


    return (
        <div>
            <h2>Cities</h2>
            <table>
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Population</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {cities.map(city => (
                    <tr key={city.id}>
                        <td>{city.name}</td>
                        <td>{city.population}</td>
                        <td>
                            <button onClick={() => handleEdit(city)}>Edit</button>
                            {(city.user.id === userId || isAdmin) && (
                                <button onClick={() => handleDelete(city.id)}>Delete</button>
                            )}
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
            {showEditDialog && selectedCity && (
                <CityEditDialog
                    city={selectedCity}
                    onClose={() => setShowEditDialog(false)}
                    onSave={(updatedCity) => {
                        handleCityUpdate(updatedCity);
                        setShowEditDialog(false);
                    }}
                />
            )}
        </div>
    );
};

export default CityTable;
