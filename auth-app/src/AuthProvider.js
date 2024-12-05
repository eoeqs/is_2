import React, { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext();

export const useAuth = () => {
    return useContext(AuthContext);
};

export const AuthProvider = ({ children }) => {
    const [token, setToken] = useState(() => {
        const storedToken = localStorage.getItem('token');
        return storedToken ? storedToken : null;
    });

    const handleSetToken = (newToken) => {
        console.log("Setting token in AuthProvider:", newToken);
        setToken(newToken);
        localStorage.setItem('token', newToken);
    };

    const logout = () => {
        console.log("Logging out...");
        setToken(null);
        localStorage.removeItem('token');
    };

    const initializeYandexSDK = () => {
        const scriptId = "yandex-sdk";
        if (!document.getElementById(scriptId)) {
            const script = document.createElement("script");
            script.id = scriptId;
            script.src = "https://yastatic.net/s3/passport-sdk/autofill/v1/sdk-suggest-with-polyfills-latest.js";
            script.async = true;

            script.onload = () => {
                console.log("Яндекс SDK успешно загружен.");
            };
            script.onerror = () => {
                console.error("Ошибка загрузки SDK Яндекса.");
            };
            document.body.appendChild(script);
        }
    };

    const loginWithYandex = () => {
        if (typeof window.YaAuthSuggest !== "undefined") {
            window.YaAuthSuggest.init(
                {
                    client_id: "22ab263505f3407382296692d3f31087",
                    response_type: "token",
                    redirect_uri: "https://localhost:8686/auth/callback",
                },
                "https://localhost:8686",
                {
                    view: "button",
                    parentId: "buttonContainer",
                    buttonSize: "xxl",
                    buttonView: "main",
                    buttonTheme: "light",
                    buttonBorderRadius: "22",
                    buttonIcon: "ya",
                }
            )
                .then(({ handler }) => handler())
                .then((data) => {
                    console.log("Received token:", data);
                    if (data && data.access_token) {
                        handleSetToken(data.access_token);
                    }
                })
                .catch((error) => console.log("Error handling Yandex login:", error));
        } else {
            console.error("YaAuthSuggest is not available. Check if SDK is loaded.");
        }
    };

    useEffect(() => {
        initializeYandexSDK();
    }, []);

    const value = {
        token,
        setToken: handleSetToken,
        logout,
        loginWithYandex,
    };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
