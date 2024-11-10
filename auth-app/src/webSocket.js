import { useAuth } from './AuthProvider';
import { useEffect } from 'react';

const useWebSocket = (url) => {
    const { token } = useAuth();

    useEffect(() => {
        if (token) {
            const socket = new WebSocket(`${url}?token=${token}`);

            socket.onopen = () => {
                console.log("WebSocket connected");
            };

            socket.onmessage = (event) => {
                console.log("Message from server:", event.data);
            };

            socket.onclose = () => {
                console.log("WebSocket disconnected");
            };

            return () => socket.close();
        }
    }, [url, token]);
};

export default useWebSocket;
