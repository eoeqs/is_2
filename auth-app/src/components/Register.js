import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { registerUser } from "../authService";

const Register = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess(false);
        try {
            await registerUser(username, password);
            setSuccess(true);
        } catch (err) {
            console.error('Registration failed:', err);
            setError('Registration error. Please try again.');
        }
    };

    return (
        <div>
            <h2>Register</h2>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            {success && (
                <div style={{ color: 'green', padding: '10px', border: '1px solid green', borderRadius: '5px' }}>
                    Registration successful! You can now{' '}
                    <button
                        onClick={() => navigate('/login')}
                        style={{
                            background: 'none',
                            border: 'none',
                            color: 'blue',
                            textDecoration: 'underline',
                            cursor: 'pointer',
                        }}
                    >
                        log in
                    </button>
                    .
                </div>
            )}
            {!success && (
                <form onSubmit={handleSubmit}>
                    <div>
                        <label htmlFor="username">Username:</label>
                        <input
                            type="text"
                            id="username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                    </div>
                    <div>
                        <label htmlFor="password">Password:</label>
                        <input
                            type="password"
                            id="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>
                    <button type="submit">Register</button>
                </form>
            )}
        </div>
    );
};

export default Register;
