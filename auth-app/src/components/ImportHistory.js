import React, {useEffect, useState} from 'react';
import axios from 'axios';
import {useAuth} from '../AuthProvider';
import {useNavigate} from 'react-router-dom';


const ImportHistory = () => {
    const navigate = useNavigate();

    const {token} = useAuth();
    const [history, setHistory] = useState([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const fetchHistory = async () => {
            try {
                const response = await axios.get('/api/cities/import-history', {
                    headers: {Authorization: `Bearer ${token}`},
                });
                setHistory(response.data);
            } catch (error) {
                console.error('Error fetching import history:', error);
            } finally {
                setIsLoading(false);
            }
        };

        fetchHistory();
    }, [token]);


    const formatCustomTimestampFromArray = (timestampArray) => {
        console.log("Received timestamp:", timestampArray);

        if (!Array.isArray(timestampArray) || timestampArray.length < 6) {
            console.error("Invalid or missing timestamp. Returning 'N/A'.");
            return 'N/A';
        }

        try {
            const [year, month, day, hour, minute, second] = timestampArray;

            console.log("Parsed values - Year:", year, "Month:", month, "Day:", day, "Hour:", hour, "Minute:", minute, "Second:", second);

            const formattedDate = `${day}/${month}/${year} ${hour}:${minute}:${second}`;
            console.log("Formatted date:", formattedDate);

            return formattedDate;
        } catch (error) {
            console.error("Error occurred while formatting timestamp:", error);
            return 'N/A';
        }
    };




    const formatDate = (timestampArray) => formatCustomTimestampFromArray(timestampArray);

    return (
        <div>
            <h2>Import History</h2>
            {isLoading ? (
                <p>Loading...</p>
            ) : history.length > 0 ? (
                <table>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Status</th>
                        <th>User</th>
                        <th>Objects Imported</th>
                        <th>Error Message</th>
                        <th>Timestamp</th>
                    </tr>
                    </thead>
                    <tbody>
                    {history.map((entry) => (
                        <tr key={entry.id}>
                            <td>{entry.id}</td>
                            <td>{entry.status}</td>
                            <td>{entry.user.username}</td>
                            <td>{entry.objectsImported ?? 'N/A'}</td>
                            <td>{entry.errorMessage ?? 'N/A'}</td>
                            <td>{formatDate(entry.timestamp)}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            ) : (
                <p>No import history available.</p>
            )}
            <button onClick={() => navigate('/city-actions')}>Back to Actions</button>
        </div>
    );
};

export default ImportHistory;
