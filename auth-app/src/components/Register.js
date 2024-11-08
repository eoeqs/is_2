import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from "axios";

const Register = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    // const handleSubmit = async (e) => {
    //     e.preventDefault();
    //     console.log('Attempting to register user:', username);
    //
    //     try {
    //         const response = await fetch('http://localhost:8080/api/users/register', {
    //             method: 'POST',
    //             headers: {
    //                 'Content-Type': 'application/json',
    //             },
    //             body: JSON.stringify({ username, password }),
    //         });
    //
    //         console.log('Server response:', response);
    //
    //         if (!response.ok) {
    //             throw new Error('Registration failed!');
    //         }
    //
    //         console.log('Registration successful, redirecting to login...');
    //         navigate('/login');
    //     } catch (err) {
    //         console.error('Registration error:', err.message);
    //         setError(err.message);
    //     }
    // };


    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const user = {
                username: username,  // Убедитесь, что переменные username и password определены
                password: password
            };
            console.log(user)
            // Отправка данных на сервер
            const response = await axios.post('http://localhost:8080/api/users/register', user);
            console.log('Server response:', response);
            // Вывод успешного ответа или редирект на другой маршрут
        } catch (error) {
            console.error('Registration error:', error.response ? error.response.data : error);
            // Вывод ошибки регистрации
        }
    };
    return (
        <div>
            <h1>Register</h1>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Username:</label>
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Password:</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <button type="submit">Register</button>
            </form>
        </div>
    );
};

export default Register;
