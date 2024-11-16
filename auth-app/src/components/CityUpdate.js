import React, {useState, useEffect} from 'react';
import axios from 'axios';
import {useNavigate, useParams} from 'react-router-dom';
import {useAuth} from '../AuthProvider';

const CityUpdate = () => {
    const {id} = useParams();
    const {token, userId} = useAuth();
    const [cityData, setCityData] = useState(null);
    const [updatedCityData, setUpdatedCityData] = useState(null);
    const [climateOptions] = useState(["RAIN_FOREST", "MONSOON", "HUMIDCONTINENTAL"]);

    useEffect(() => {
        const fetchCityData = async () => {
            try {
                const response = await axios.get(`/cities/${id}`, {
                    headers: {Authorization: `Bearer ${token}`}
                });
                console.log(response.data)
                console.log(response)
                setCityData(response.data);
                setUpdatedCityData(response.data);
            } catch (error) {
                console.error('Error fetching city data:', error);
            }
        };

        if (token) {
            fetchCityData();
        }
    }, [id, token]);

    const handleChange = (field, value, entity) => {
        if (entity) {
            setUpdatedCityData((prevData) => ({
                ...prevData,
                [entity]: {
                    ...prevData[entity],
                    [field]: value,
                },
            }));
        } else {
            setUpdatedCityData((prevData) => ({
                ...prevData,
                [field]: value,
            }));
        }
    };
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();

        const cityToUpdate = {
            ...updatedCityData,
            updatedBy: userId,
            updatedDate: new Date().toISOString(),
            user: {id: userId},
        };

        try {
            await axios.put(`/cities/${id}`, cityToUpdate, {
                headers: {Authorization: `Bearer ${token}`}
            });
            navigate('/city-actions');
            console.log('City updated successfully');
        } catch (error) {
            console.error('Error updating city:', error);
        }
    };

    if (!updatedCityData) return <div>Loading...</div>;

    const hasCoordinates = updatedCityData.coordinates && Object.keys(updatedCityData.coordinates).length > 0;
    const hasGovernor = updatedCityData.governor && Object.keys(updatedCityData.governor).length > 0;

    return (
        <div>
            <h2>Update City</h2>
            <form onSubmit={handleSubmit}>
                <table>
                    <tbody>
                    {Object.entries(updatedCityData).map(([key, value]) => (
                        (key !== 'coordinates' && key !== 'governor' && key !== 'user') && (
                            <tr key={key}>
                                <td>{key}</td>
                                <td>
                                    {key === 'id' || key === 'updatedBy' || key === 'updatedDate' ? (
                                        <input
                                            type="text"
                                            value={value || ''}
                                            readOnly
                                            style={{backgroundColor: '#f0f0f0'}}
                                        />
                                    ) : key === 'capital' ? (
                                        <input
                                            type="checkbox"
                                            checked={value || false}
                                            onChange={(e) => handleChange(key, e.target.checked)}
                                        />
                                    ) : key === 'climate' ? (
                                        <select
                                            value={value || ''}
                                            onChange={(e) => handleChange(key, e.target.value)}
                                        >
                                            {climateOptions.map((climate) => (
                                                <option key={climate} value={climate}>
                                                    {climate.replace(/_/g, ' ')}
                                                </option>
                                            ))}
                                        </select>
                                    ) : (
                                        <input
                                            type={typeof value === 'number' ? 'number' : 'text'}
                                            value={value || ''}
                                            onChange={(e) => handleChange(key, e.target.value)}
                                        />
                                    )}
                                </td>
                                <td>
                                    {key === 'id' &&
                                        <span>Значение этого поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически</span>}
                                    {key === 'name' &&
                                        <span>Поле не может быть null, Строка не может быть пустой</span>}
                                    {key === 'coordinates' && <span>Поле не может быть null</span>}
                                    {key === 'creationDate' &&
                                        <span>Поле не может быть null, Значение этого поля должно генерироваться автоматически</span>}
                                    {key === 'area' && <span>Значение поля должно быть больше 0</span>}
                                    {key === 'population' &&
                                        <span>Значение поля должно быть больше 0, Поле не может быть null</span>}
                                    {key === 'establishmentDate' && <span>Поле не может быть null</span>}
                                    {key === 'capital' && <span>Поле не может быть null</span>}
                                    {key === 'metersAboveSeaLevel' && <span>Значение поля должно быть больше 0</span>}
                                    {key === 'carCode' &&
                                        <span>Значение поля должно быть больше 0, Максимальное значение поля: 1000, Поле может быть null</span>}
                                    {key === 'agglomeration' && <span>Значение поля должно быть больше 0</span>}
                                    {key === 'climate' && <span>Поле не может быть null</span>}
                                    {key === 'governor' && <span>Поле не может быть null</span>}
                                </td>
                            </tr>
                        )
                    ))}
                    </tbody>
                </table>

                {hasCoordinates && (
                    <div>
                        <h3>Coordinates</h3>
                        <table>
                            <tbody>
                            {Object.entries(updatedCityData.coordinates).map(([key, value]) => (
                                <tr key={key}>
                                    <td>{key}</td>
                                    <td>
                                        {key !== 'id' ? (
                                            <input
                                                type="text"
                                                value={value || ''}
                                                onChange={(e) => handleChange(key, e.target.value, 'coordinates')}
                                            />
                                        ) : (
                                            <input
                                                type="text"
                                                value={value || ''}
                                                readOnly
                                                style={{backgroundColor: '#f0f0f0'}}
                                            />
                                        )}
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )}

                {hasGovernor && (
                    <div>
                        <h3>Governor</h3>
                        <table>
                            <tbody>
                            {Object.entries(updatedCityData.governor).map(([key, value]) => (
                                <tr key={key}>
                                    <td>{key}</td>
                                    <td>
                                        {key !== 'id' ? (
                                            <input
                                                type="text"
                                                value={value || ''}
                                                onChange={(e) => handleChange(key, e.target.value, 'governor')}
                                            />
                                        ) : (
                                            <input
                                                type="text"
                                                value={value || ''}
                                                readOnly
                                                style={{backgroundColor: '#f0f0f0'}}
                                            />
                                        )}
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )}

                <button type="submit">Save Changes</button>
                <button onClick={() => navigate('/city-actions')}>Back to Actions</button>

            </form>
        </div>
    );
};

export default CityUpdate;
