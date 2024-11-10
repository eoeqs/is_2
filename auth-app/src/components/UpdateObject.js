import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';

const UpdateObject = () => {
    const { id } = useParams();
    const [objectData, setObjectData] = useState({ name: '', attribute1: '', attribute2: '' });

    useEffect(() => {
        const fetchObject = async () => {
            const response = await axios.get(`http://localhost:8080/api/objects/${id}`);
            setObjectData(response.data);
        };
        fetchObject();
    }, [id]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await axios.put(`http://localhost:8080/api/objects/${id}`, objectData);
            alert('Object updated successfully!');
        } catch (error) {
            console.error('Error updating object:', error);
        }
    };

    const handleChange = (e) => {
        setObjectData({ ...objectData, [e.target.name]: e.target.value });
    };

    return (
        <div>
            <h2>Update Object</h2>
            <form onSubmit={handleSubmit}>
                <input name="name" value={objectData.name} onChange={handleChange} />
                <input name="attribute1" value={objectData.attribute1} onChange={handleChange} />
                <input name="attribute2" value={objectData.attribute2} onChange={handleChange} />
                <button type="submit">Update</button>
            </form>
        </div>
    );
};

export default UpdateObject;
