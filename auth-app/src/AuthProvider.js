import React, {createContext, useContext, useEffect, useState} from 'react';
import axios from "axios";

const AuthContext = createContext();

export const useAuth = () => {
    return useContext(AuthContext);
};

export const AuthProvider = ({children}) => {
    const [token, setToken] = useState(null);

    const handleSetToken = (newToken) => {
        console.log("Setting token in AuthProvider:", newToken);
        setToken(newToken);
    };

    // const logout = async () => {
    //     console.log(token)
    //     if (token) {
    //         try {
    //             console.log("бу")
    //             const clientId = '22ab263505f3407382296692d3f31087';
    //             const clientSecret = '7e05ff83dbd449f09cac6d8217b42012';
    //             const data = new URLSearchParams();
    //             data.append('access_token', token);
    //             data.append('client_id', clientId);
    //             data.append('client_secret', clientSecret);
    //
    //             console.log("Data to be sent in request:", data.toString());
    //
    //             await axios.post('https://oauth.yandex.ru/revoke_token', data, {
    //                 headers: {
    //                     'Content-Type': 'application/x-www-form-urlencoded',
    //                 },
    //
    //             });
    //
    //             console.log('Token revoked successfully');
    //         } catch (error) {
    //             console.error('Error revoking token:', error);
    //         }
    //     }
    //
    //     setToken(null);
    // };
    const logout = async () => {
        console.log("Starting logout process...");

        if (token) {
            try {
                const clientId = '22ab263505f3407382296692d3f31087';
                const clientSecret = '7e05ff83dbd449f09cac6d8217b42012';

                const authHeader = `Basic ${btoa(clientId + ':' + clientSecret)}`;

                const data = new URLSearchParams();
                data.append('access_token', token);

                console.log("Data to be sent in request:", data.toString());

                const response = await axios.post('https://oauth.yandex.ru/revoke_token', data, {
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                        'Authorization': authHeader,
                    },
                });

                console.log('Token revoked successfully:', response.data);

            } catch (error) {
                console.error('Error revoking token:', error);
            }
        }

        setToken(null);
        console.log("Token cleared from state.");
    };


    const value = {
        token,
        setToken: handleSetToken,
        logout,
    };


    return <AuthContext.Provider value={{token, setToken: handleSetToken, logout}}>{children}</AuthContext.Provider>;
};
