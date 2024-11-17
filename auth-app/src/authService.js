import axios from 'axios';

export const loginUser = async (username, password, setToken) => {
    try {
        const response = await axios.post('/api/users/login', { username, password }, { withCredentials: true });
        const token = response.data.token;
        setToken(token);
        console.log("Token received from server:", token);
        return token;
    } catch (error) {
        console.error("Login error:", error);
        throw error;
    }
};
