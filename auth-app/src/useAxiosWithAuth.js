import axios from 'axios';
import { useAuth } from './AuthProvider';

const useAxiosWithAuth = () => {
    const { token } = useAuth();

    const instance = axios.create();

    instance.interceptors.request.use(
        (config) => {
            if (token) {
                config.headers['Authorization'] = `Bearer ${token}`;
            }
            return config;
        },
        (error) => Promise.reject(error)
    );

    return instance;
};

export default useAxiosWithAuth;
