import React, { createContext, useContext, useState } from 'react';

const AuthContext = createContext();

export const useAuth = () => {
    return useContext(AuthContext);
};

export const AuthProvider = ({ children }) => {
    const [token, setToken] = useState(null);
    const handleSetToken = (newToken) => {
        console.log("Setting token in AuthProvider:", newToken);
        setToken(newToken);
    };
    const value = {
        token,
        setToken,
    };

    return <AuthContext.Provider value={{ token, setToken: handleSetToken }}>{children}</AuthContext.Provider>;
};
