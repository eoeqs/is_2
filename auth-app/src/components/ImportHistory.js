import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useAuth } from '../AuthProvider';
import { useNavigate } from 'react-router-dom';

const ImportHistory = () => {
    const navigate = useNavigate();
    const { token } = useAuth();
    const [history, setHistory] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [currentPage, setCurrentPage] = useState(1);
    const [itemsPerPage, setItemsPerPage] = useState(10);

    useEffect(() => {
        const fetchHistory = async () => {
            try {
                const response = await axios.get('/api/cities/import-history', {
                    headers: { Authorization: `Bearer ${token}` },
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
        if (!Array.isArray(timestampArray) || timestampArray.length < 6) {
            return 'N/A';
        }

        try {
            const [year, month, day, hour, minute, second] = timestampArray;
            return `${day}/${month}/${year} ${hour}:${minute}:${second}`;
        } catch (error) {
            return 'N/A';
        }
    };

    const formatDate = (timestampArray) => formatCustomTimestampFromArray(timestampArray);

    const indexOfLastItem = currentPage * itemsPerPage;
    const indexOfFirstItem = indexOfLastItem - itemsPerPage;
    const currentItems = history.slice(indexOfFirstItem, indexOfLastItem);

    const nextPage = () => {
        if (currentPage < Math.ceil(history.length / itemsPerPage)) {
            setCurrentPage(currentPage + 1);
        }
    };

    const prevPage = () => {
        if (currentPage > 1) {
            setCurrentPage(currentPage - 1);
        }
    };

    const totalPages = Math.ceil(history.length / itemsPerPage);

    return (
        <div>
            <h2>Import History</h2>
            {isLoading ? (
                <p>Loading...</p>
            ) : currentItems.length > 0 ? (
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
                    {currentItems.map((entry) => (
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

            <div>
                <button onClick={prevPage} disabled={currentPage === 1}>
                    Previous
                </button>
                <span>
                    Page {currentPage} of {totalPages}
                </span>
                <button onClick={nextPage} disabled={currentPage === totalPages}>
                    Next
                </button>
            </div>

            <button onClick={() => navigate('/city-actions')}>Back to Actions</button>
        </div>
    );
};

export default ImportHistory;
