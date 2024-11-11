import { useState } from "react";

const CoordinatesForm = ({ onCoordinatesCreated }) => {
    const [x, setX] = useState('');
    const [y, setY] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        // Преобразуем значения x и y в числа перед отправкой
        const coordinates = { x: parseFloat(x), y: parseFloat(y) };
        if (!isNaN(coordinates.x) && !isNaN(coordinates.y)) {
            onCoordinatesCreated(coordinates); // Отправка координат родительскому компоненту
        } else {
            // Добавить обработку ошибок, если координаты не числа
            console.error("Invalid coordinates");
        }
    };

    return (
        <div>
            <h3>Coordinates</h3>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>X:</label>
                    <input
                        type="number"
                        value={x}
                        onChange={(e) => setX(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Y:</label>
                    <input
                        type="number"
                        value={y}
                        onChange={(e) => setY(e.target.value)}
                        required
                    />
                </div>
                <button type="submit">Save Coordinates</button>
            </form>
        </div>
    );
};

export default CoordinatesForm;