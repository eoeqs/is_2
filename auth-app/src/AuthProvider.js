import React, {createContext, useContext, useState, useEffect} from 'react';

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

    const logout = async () => {
        console.log("Starting logout process...");

        // if (token) {
        //     try {
        //         console.log("Sending token to backend for revocation...");
        //
        //         const response = await axios.post(
        //             '/api/auth/logout',
        //             { access_token: token },
        //             {
        //                 headers: {
        //                     Authorization: `Bearer ${token}`,
        //                     'Content-Type': 'application/json',
        //                 },
        //             }
        //         );
        //
        //         console.log('Token revoked on backend:', response.data);
        //
        //     } catch (error) {
        //         console.error('Error revoking token on backend:', error);
        //     }
        // }

        setToken(null);
        console.log("Token cleared:", token);
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
                .then(({handler}) => handler())
                .then((data) => {
                    console.log("Сообщение с токеном:", data);
                    if (data && data.access_token) {
                        setToken(data.access_token);
                        console.log("token сохранен");
                    }
                })
                .catch((error) => console.log("Обработка ошибки:", error));
        } else {
            console.error("YaAuthSuggest недоступен. Проверьте, загружен ли SDK.");
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
