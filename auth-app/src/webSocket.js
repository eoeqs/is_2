import { useAuth } from './AuthProvider';
import { useEffect } from 'react';

const useWebSocket = (url, onMessage) => {
    const { token } = useAuth();

    useEffect(() => {
        if (token) {
            const socket = new WebSocket(`${url}?token=${token}`);

            socket.onopen = () => {
                console.log("WebSocket connected");
            };

            socket.onmessage = (event) => {
                const data = JSON.parse(event.data);
                console.log("Message from server:", data);

                if (onMessage) {
                    onMessage(data);
                }
            };

            socket.onclose = () => {
                console.log("WebSocket disconnected");
            };

            return () => socket.close();
        }
    }, [url, token, onMessage]);
};

export default useWebSocket;
