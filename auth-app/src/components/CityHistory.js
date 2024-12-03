import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';
import { useAuth } from '../AuthProvider';

const CityHistory = () => {
    const { token } = useAuth();
    const [history, setHistory] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const fetchHistory = async () => {
        setLoading(true);
        setError(null);
        try {
            const response = await axios.get(`/api/cities/history`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            console.log(response.data)
            setHistory(response.data);
        } catch (error) {
            console.error('Error fetching history:', error);
            setError('Error fetching history');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchHistory();
    }, [ token]);

    return (
        <div>
            <h2>City History</h2>

            {error && <p style={{color: 'red'}}>{error}</p>}

            {loading ? (
                <p>Loading...</p>
            ) : history.length === 0 ? (
                <p>No history available for this city.</p>
            ) : (
                <table>
                    <thead>
                    <tr>
                        <th>City ID</th>
                        <th>City Name</th>
                        <th>Created By</th>
                        <th>Created Date</th>
                        <th>Updated By</th>
                        <th>Updated Date</th>
                    </tr>
                    </thead>
                    <tbody>
                    {history.map((entry, index) => (
                        <tr key={index}>
                            <td>{entry.cityId}</td>
                            <td>{entry.cityName}</td>
                            <td>{entry.createdBy}</td>
                            <td>{new Date(entry.createdDate).toLocaleString()}</td>
                            <td>{entry.updatedBy}</td>
                            <td>{entry.updatedDate ? entry.updatedDate : 'N/A'}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
            <button onClick={() => navigate('/city-actions')}>Back to Actions</button>
        </div>
    );
};

export default CityHistory;
