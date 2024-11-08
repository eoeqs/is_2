
const connectWebSocket = (onUpdate) => {
    const socket = new WebSocket('ws://localhost:8080/ws');

    socket.onopen = () => {
        console.log('WebSocket connection established');
    };

    socket.onmessage = (event) => {
        const updatedCities = JSON.parse(event.data);
        onUpdate(updatedCities);
    };

    socket.onerror = (error) => {
        console.error('WebSocket error:', error);
    };

    socket.onclose = () => {
        console.log('WebSocket connection closed');
    };

    return socket;
};

export { connectWebSocket };
