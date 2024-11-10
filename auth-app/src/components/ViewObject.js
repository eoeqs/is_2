import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';

const ViewObject = () => {
    const { id } = useParams();
    const [object, setObject] = useState(null);

    useEffect(() => {
        const fetchObject = async () => {
            try {
                const response = await axios.get(`http://localhost:8080/api/objects/${id}`);
                setObject(response.data);
            } catch (error) {
                console.error('Error fetching object:', error);
            }
        };
        fetchObject();
    }, [id]);

    if (!object) return <div>Loading...</div>;

    return (
        <div>
            <h2>Object Details</h2>
            <p>Name: {object.name}</p>
            <p>Attribute 1: {object.attribute1}</p>
            <p>Attribute 2: {object.attribute2}</p>
            <h3>Related Objects</h3>
            <ul>
                {object.relatedObjects?.map((related) => (
                    <li key={related.id}>{related.name}</li>
                ))}
            </ul>
        </div>
    );
};

export default ViewObject;
