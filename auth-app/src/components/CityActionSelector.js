import React, {useState, useEffect} from 'react';
import axios from 'axios';
import {Link, useNavigate, useParams} from 'react-router-dom';
import {useAuth} from '../AuthProvider';
import ReactPaginate from 'react-paginate';
import ClimateFilter from './ClimateFilter';
import AgglomerationFilter from './AgglomerationFilter';
import SockJS from 'sockjs-client';
import {Client} from '@stomp/stompjs';


const CityActionSelector = () => {
    const navigate = useNavigate();

    const {token, logout} = useAuth();
    const {id} = useParams();
    const [userId, setUserId] = useState(null);
    const [primaryRole, setPrimaryRole] = useState(null);
    const [cities, setCities] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [citiesPerPage] = useState(5);
    const [selectedCityId, setSelectedCityId] = useState('');
    const [sortConfig, setSortConfig] = useState({key: null, direction: 'asc'});
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedAgglomeration, setSelectedAgglomeration] = useState('');
    const [showUniqueAgglomerations, setShowUniqueAgglomerations] = useState(false);
    const [distance, setDistance] = useState(null);
    const [loadingDistance, setLoadingDistance] = useState(false);
    const [totalPages, setTotalPages] = useState(0);
    const [isLoading, setIsLoading] = useState(false);
    const [importFile, setImportFile] = useState(null);
    const [importError, setImportError] = useState(null);
    const [errorMessage, setErrorMessage] = useState('');
    const [isImporting, setIsImporting] = useState(false);

    const fetchCities = async (page) => {
        setIsLoading(true);
        try {
            const response = await axios.get('/api/cities/paginated', {
                headers: {Authorization: `Bearer ${token}`},
                params: {
                    page,
                    size: citiesPerPage,
                },
            });
            console.log(token, "token in city actions")
            console.log(`Fetched Cities for page ${currentPage}:`, response.data.content);

            setCities(response.data.content || []);
            setTotalPages(response.data.totalPages || 0);


        } catch (error) {
            console.error('Error fetching cities:', error);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        const fetchUserInfo = async () => {
            try {
                const response = await axios.get(`/api/users/current-user-info`, {
                    headers: {Authorization: `Bearer ${token}`},

                });

                console.log(response)
                setUserId(response.data.id);
                setPrimaryRole(response.data.role);
            } catch (error) {
                console.error('Error fetching user info:', error);
            }
        }


        const connectToWebSocket = () => {
            const socket = new WebSocket(`wss://${window.location.host}/api/ws`);
            const stompClient = new Client({
                webSocketFactory: () => socket,
                debug: (str) => {
                    console.log(str);
                },
                onConnect: () => {
                    console.log('Connected to WebSocket with STOMP');
                    stompClient.subscribe('/topic/cities', (message) => {
                        const update = JSON.parse(message.body);
                        if (update) {
                            console.log('Received update:', update);
                            fetchCities(currentPage);
                        }
                    });
                },
                onStompError: (error) => {
                    console.error('STOMP Error:', error);
                },
            });
            stompClient.activate();
        };
        if (token) {
            fetchUserInfo();
            fetchCities(0);
            connectToWebSocket();


        }
    }, [token, userId]);

    const indexOfLastCity = (currentPage + 1) * citiesPerPage;
    const indexOfFirstCity = indexOfLastCity - citiesPerPage;

    const filteredCities = Array.isArray(cities) ? cities.filter((city) => {
        const searchableString = Object.values(city).join(' ').toLowerCase();
        return searchableString.includes(searchTerm.toLowerCase()) &&
            (selectedAgglomeration ? city.agglomeration === selectedAgglomeration : true);
    }) : [];

    const getSortValue = (city, key) => {
        if (key === 'coordinates') {
            return `${city.coordinates?.x || ''}, ${city.coordinates?.y || ''}`;
        }
        if (key === 'governor') {
            return city.governor?.height || '';
        }
        return city[key] ? city[key].toString().toLowerCase() : '';
    };

    const sortedCities = [...filteredCities].sort((a, b) => {
        if (sortConfig.key) {
            const aValue = getSortValue(a, sortConfig.key);
            const bValue = getSortValue(b, sortConfig.key);

            if (aValue < bValue) return sortConfig.direction === 'asc' ? -1 : 1;
            if (aValue > bValue) return sortConfig.direction === 'asc' ? 1 : -1;
        }
        return 0;
    });

    const currentCities = sortedCities.slice();

    const handlePageChange = async ({selected}) => {
        console.log('Switching to page:', selected);
        setCurrentPage(selected);

        await fetchCities(selected);

    };

    const handleCityChange = (e) => {
        setSelectedCityId(e.target.value);
    };

    const requestSort = (key) => {
        let direction = 'asc';
        if (sortConfig.key === key && sortConfig.direction === 'asc') {
            direction = 'desc';
        }
        setSortConfig({key, direction});
    };

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    const handleFindDistance = async () => {
        setLoadingDistance(true);
        setDistance(null);

        try {
            const response = await axios.get('/api/cities/distance-to-largest-city', {
                headers: {Authorization: `Bearer ${token}`},
            });
            setDistance(response.data);
        } catch (error) {
            console.error('Error fetching distance:', error);
        } finally {
            setLoadingDistance(false);
        }
    };
    const handleFileChange = (e) => {
        setImportFile(e.target.files[0]);
    };


    const handleImport = async () => {
        setErrorMessage('');
        setIsImporting(true);
        if (!importFile) {
            setErrorMessage('Please select a file to import.');
            setIsImporting(false);
            return;
        }

        const formData = new FormData();
        formData.append('file', importFile);

        try {
            const response = await axios.post('/api/cities/import', formData, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'multipart/form-data',
                },
            });

            if (response.status === 200) {
                setErrorMessage('');
                fetchCities(currentPage);
            }
        } catch (error) {
            console.error('Error importing file:', error);
            setErrorMessage('Failed to import file. Please try again.');
        } finally {
            setIsImporting(false);
        }
    };

    return (
        <div>
            <h2>What would you like to do?</h2>
            {primaryRole === 'ROLE_ADMIN' ? (
                <div>
                    <Link to="/role-requests">
                        <button>Go to Role Requests</button>
                    </Link>
                </div>
            ) : primaryRole === 'ROLE_USER' ? (
                <div>
                    <Link to="/request-role-change">
                        <button>Request Role Change</button>
                    </Link>
                </div>
            ) : null}
            <div>
                <Link to="/cities/create">
                    <button>Create a New City</button>
                </Link>
            </div>
            <div>
                <Link to="/cities/update">
                    <button>Update an Existing City</button>
                </Link>
            </div>
            <div>
                <select onChange={handleCityChange} value={selectedCityId}>
                    <option value="">Select a City</option>
                    {cities.map((city) => (
                        <option key={city.id} value={city.id}>
                            {city.name}
                        </option>
                    ))}
                </select>
                <Link to={`/cities/info/${selectedCityId}`}>
                    <button disabled={!selectedCityId}>Show City Info</button>
                </Link>
            </div>
            <div>
                <Link to="/cities/delete">
                    <button>Delete an Existing City</button>
                </Link>
            </div>

            <ClimateFilter/>


            {showUniqueAgglomerations && <AgglomerationFilter/>}

            <div>
                <button onClick={handleFindDistance} disabled={loadingDistance}>
                    {loadingDistance ? 'Loading...' : 'Find Distance to Largest City from (0;0)'}
                </button>

                {distance !== null && !loadingDistance && (
                    <div>
                        <h4>Distance to the Largest City: {distance} km</h4>
                    </div>
                )}
            </div>
            <h3>City List</h3>
            <input
                type="text"
                placeholder="Search..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
            />
            {isLoading ? (
                <p>Loading...</p>
            ) : cities.length > 0 ? (
                <table>
                    <thead>
                    <tr>
                        {[
                            {label: 'ID', key: 'id'},
                            {label: 'Name', key: 'name'},
                            {label: 'Population', key: 'population'},
                            {label: 'Area', key: 'area'},
                            {label: 'Capital', key: 'capital'},
                            {label: 'Climate', key: 'climate'},
                            {label: 'Coordinates', key: 'coordinates'},
                            {label: 'Governor (Height)', key: 'governor'},
                            {label: 'Creation Date', key: 'creationDate'},
                            {label: 'Establishment Date', key: 'establishmentDate'},
                            {label: 'Meters Above Sea Level', key: 'metersAboveSeaLevel'},
                            {label: 'Car Code', key: 'carCode'},
                            {label: 'Agglomeration', key: 'agglomeration'},
                        ].map((column) => (
                            <th key={column.key}>
                                {column.label}
                                <button onClick={() => requestSort(column.key)}>
                                    {sortConfig.key === column.key ? (
                                        sortConfig.direction === 'asc' ? '↑' : '↓'
                                    ) : '↕'}
                                </button>
                            </th>
                        ))}
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {currentCities.map((city) => (
                        <tr key={city.id}>
                            <td>{city.id}</td>
                            <td>{city.name}</td>
                            <td>{city.population}</td>
                            <td>{city.area}</td>
                            <td>{city.capital ? 'Yes' : 'No'}</td>
                            <td>{city.climate}</td>
                            <td>{city.coordinates ? `X: ${city.coordinates.x}, Y: ${city.coordinates.y}` : 'N/A'}</td>
                            <td>{city.governor ? city.governor.height : 'N/A'}</td>
                            <td>{city.creationDate}</td>
                            <td>{city.establishmentDate}</td>
                            <td>{city.metersAboveSeaLevel}</td>
                            <td>{city.carCode !== null ? city.carCode : 'N/A'}</td>
                            <td>{city.agglomeration}</td>
                            <td>
                                {primaryRole === 'ROLE_ADMIN' ? (
                                    <div>
                                        <Link to={`/cities/update/${city.id}`}>
                                            <button>
                                                Edit
                                            </button>
                                        </Link>
                                    </div>
                                ) : primaryRole === 'ROLE_USER' ? (
                                    <div>
                                        <Link to={`/cities/update/${city.id}`}>
                                            <button
                                                disabled={userId !== city.user.id}
                                                style={{
                                                    backgroundColor: userId !== city.user.id ? '#d3d3d3' : '',
                                                    cursor: userId !== city.user.id ? 'not-allowed' : 'pointer',
                                                }}
                                            >
                                                Edit
                                            </button>
                                        </Link>
                                    </div>
                                ) : null}

                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            ) : (
                <p>No cities available.</p>
            )}

            <ReactPaginate
                previousLabel={<button className="pagination-button">Previous</button>}
                nextLabel={<button className="pagination-button">Next</button>}
                pageCount={totalPages}
                onPageChange={handlePageChange}
                containerClassName={"pagination"}
                activeClassName={"active"}
                pageClassName="page-item"
                pageLinkClassName="page-link"
                previousClassName="previous-item"
                nextClassName="next-item"
            />
            <div>
                <Link to="/city-history">
                    <button>Show Change History</button>
                </Link>
            </div>
            <div>
                <Link to="/import-history">
                    <button>View Import History</button>
                </Link>

            </div>
            <div>
                <h2>Import Cities</h2>
                <div>
                    <label htmlFor="file-input">Upload File:</label>
                    <input
                        id="file-input"
                        type="file"
                        onChange={(e) => setImportFile(e.target.files[0])}
                        accept=".yaml,.yml"
                    />
                    <button onClick={handleImport} disabled={isImporting}>
                        {isImporting ? 'Importing...' : 'Import Cities'}
                    </button>
                </div>

                {errorMessage && <p style={{color: 'red'}}>{errorMessage}</p>}
            </div>
            <div>
                <button onClick={handleLogout}>Logout</button>
            </div>
        </div>
    );
};


export default CityActionSelector;
