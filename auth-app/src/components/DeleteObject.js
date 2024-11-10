import React from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';

const DeleteObject = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const handleDelete = async () => {
        try {
            await axios.delete(`http://localhost:8080/api/objects/${id}`);
            alert('Object deleted successfully!');
            navigate('/');
        } catch (error) {
            console.error('Error deleting object:', error);
        }
    };

    return (
        <div>
            <h2>Delete Object</h2>
            <p>Are you sure you want to delete this object?</p>
            <button onClick={handleDelete}>Yes, delete</button>
            <button onClick={() => navigate('/')}>Cancel</button>
        </div>
    );
};

export default DeleteObject;
