import React, { useState } from 'react';
import axios from 'axios';
import { useAuth } from '../AuthProvider';

const DistanceToLargestCity = () => {
    const { token } = useAuth();
    const [distance, setDistance] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const handleCalculateDistance = async () => {
        setLoading(true);
        setError(null);
        try {
            const response = await axios.get('/cities/distance-to-largest-city', {
                headers: { Authorization: `Bearer ${token}` },
            });
            setDistance(response.data);
        } catch (err) {
            setError('Error calculating distance');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            <h3>Distance to the Largest City</h3>
            <button onClick={handleCalculateDistance} disabled={loading}>
                {loading ? 'Calculating...' : 'Find Distance'}
            </button>

            {error && <p style={{ color: 'red' }}>{error}</p>}

            {distance !== null && !loading && !error && (
                <div>
                    <h4>Distance: {distance} km</h4>
                </div>
            )}
        </div>
    );
};

export default DistanceToLargestCity;
