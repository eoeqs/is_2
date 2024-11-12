import React, { useState } from 'react';
import axios from 'axios';
import { useAuth } from "../AuthProvider";

const CityEditDialog = ({ city, onClose, onSave }) => {
    const { token } = useAuth();
    const [name, setName] = useState(city.name);
    const [population, setPopulation] = useState(city.population);
    const [error, setError] = useState(null);

    const handleSave = async () => {
        if (!name.trim() || population <= 0) {
            setError("Please ensure all fields are filled correctly.");
            return;
        }
        try {
            const response = await axios.put(`/cities/${city.id}`, {
                name,
                population: Number(population),
            }, {
                headers: { Authorization: `Bearer ${token}` },
            });
            onSave(response.data);
        } catch (error) {
            console.error("Error updating city:", error);
            setError("Failed to update city. Please try again.");
        }
    };

    return (
        <div className="dialog-overlay">
            <div className="dialog">
                <h3>Edit City</h3>
                {error && <p style={{ color: "red" }}>{error}</p>}
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
                <button onClick={handleSave}>Save</button>
                <button onClick={onClose}>Cancel</button>
            </div>
        </div>
    );
};

export default CityEditDialog;
