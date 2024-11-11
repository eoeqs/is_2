import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from "../AuthProvider";

const CityForm = () => {
    const { token, userId } = useAuth(); // Получаем токен и id пользователя из контекста
    const [name, setName] = useState('');
    const [population, setPopulation] = useState('');
    const [area, setArea] = useState('');
    const [capital, setCapital] = useState(true);
    const [metersAboveSeaLevel, setMetersAboveSeaLevel] = useState('');
    const [carCode, setCarCode] = useState('');
    const [agglomeration, setAgglomeration] = useState('');
    const [climate, setClimate] = useState('RAIN_FOREST');
    const [coordinates, setCoordinates] = useState(null);
    const [governor, setGovernor] = useState(null);
    const [availableCoordinates, setAvailableCoordinates] = useState([]);
    const [availableGovernors, setAvailableGovernors] = useState([]);

    // Загрузка доступных координат и губернаторов при загрузке формы
    useEffect(() => {
        const fetchData = async () => {
            try {
                const coordinatesResponse = await axios.get('/coordinates', {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                const governorsResponse = await axios.get('/humans', {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                setAvailableCoordinates(coordinatesResponse.data);
                setAvailableGovernors(governorsResponse.data);
            } catch (error) {
                console.error('Error fetching available coordinates or governors:', error);
            }
        };

        fetchData();
    }, [token]);

    // Обработчик отправки формы для города
    const handleSubmit = async (e) => {
        e.preventDefault();
        const city = {
            name,
            population: Number(population), // Убедитесь, что данные числовые
            coordinates,
            governor,
            area: Number(area),
            capital,
            metersAboveSeaLevel: Number(metersAboveSeaLevel),
            carCode: Number(carCode), // Может быть пустым, но если передается - убедитесь в корректности
            agglomeration: Number(agglomeration),
            climate,
            user: { id: userId }, // Добавляем user из контекста
        };

        try {
            const response = await axios.post('/cities', city, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            console.log('City created:', response.data);
        } catch (error) {
            console.error('Error creating city:', error);
        }
    };

    return (
        <div>
            <h2>Create City</h2>
            <form onSubmit={handleSubmit}>
                {/* Поле для имени города */}
                <div>
                    <label>Name:</label>
                    <input
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                    />
                </div>

                {/* Поле для населения */}
                <div>
                    <label>Population:</label>
                    <input
                        type="number"
                        value={population}
                        onChange={(e) => setPopulation(e.target.value)}
                        required
                    />
                </div>

                {/* Поле для площади */}
                <div>
                    <label>Area:</label>
                    <input
                        type="number"
                        value={area}
                        onChange={(e) => setArea(e.target.value)}
                        required
                    />
                </div>

                {/* Поле для столицы */}
                <div>
                    <label>Capital:</label>
                    <input
                        type="checkbox"
                        checked={capital}
                        onChange={() => setCapital(!capital)}
                    />
                </div>

                {/* Поле для высоты над уровнем моря */}
                <div>
                    <label>Meters Above Sea Level:</label>
                    <input
                        type="number"
                        value={metersAboveSeaLevel}
                        onChange={(e) => setMetersAboveSeaLevel(e.target.value)}
                    />
                </div>

                {/* Поле для кода города */}
                <div>
                    <label>Car Code (Optional):</label>
                    <input
                        type="number"
                        value={carCode}
                        onChange={(e) => setCarCode(e.target.value)}
                    />
                </div>

                {/* Поле для агломерации */}
                <div>
                    <label>Agglomeration:</label>
                    <input
                        type="number"
                        value={agglomeration}
                        onChange={(e) => setAgglomeration(e.target.value)}
                        required
                    />
                </div>

                {/* Поле для климата */}
                <div>
                    <label>Climate:</label>
                    <select
                        value={climate}
                        onChange={(e) => setClimate(e.target.value)}
                    >
                        <option value="RAIN_FOREST">Rain Forest</option>
                        <option value="MONSOON">Monsoon</option>
                        <option value="HUMIDCONTINENTAL">Humid Continental</option>
                    </select>
                </div>

                {/* Выпадающий список для выбора координат */}
                <div>
                    <label>Coordinates:</label>
                    <select
                        value={coordinates ? coordinates.id : ''}
                        onChange={(e) => setCoordinates(availableCoordinates.find(coord => coord.id === e.target.value))}
                    >
                        <option value="">Select Coordinates</option>
                        {availableCoordinates.map(coord => (
                            <option key={coord.id} value={coord.id}>
                                {`X: ${coord.x}, Y: ${coord.y}`}
                            </option>
                        ))}
                    </select>
                </div>

                {/* Выпадающий список для выбора губернатора */}
                <div>
                    <label>Governor:</label>
                    <select
                        value={governor ? governor.id : ''}
                        onChange={(e) => setGovernor(availableGovernors.find(gov => gov.id === e.target.value))}
                    >
                        <option value="">Select Governor</option>
                        {availableGovernors.map(gov => (
                            <option key={gov.id} value={gov.id}>
                                {`${gov.firstName} ${gov.lastName}`}
                            </option>
                        ))}
                    </select>
                </div>

                {/* Отправка формы */}
                <button type="submit">Create City</button>
            </form>
        </div>
    );
};

export default CityForm;
