import React, { useEffect } from "react";
import { useAuth } from "../AuthProvider";
import { useNavigate } from "react-router-dom";
import axios from "axios";

const LoginPage = () => {
    const navigate = useNavigate();
    const { token, setToken } = useAuth();

    useEffect(() => {
        if (token) {
            console.log("Отправка токена на сервер...", token);

            axios
                .get("https://login.yandex.ru/info", {
                    headers: {
                        Authorization: `OAuth ${token}`,
                    },
                    params: {
                        format: "jwt",
                    },
                })
                .then((userInfoResponse) => {
                    console.log("Полученные данные от Яндекса в формате JWT:", userInfoResponse.data);

                    setToken(userInfoResponse.data);
                    console.log("JWT токен сохранен в контексте.");

                    axios
                        .post('/api/auth/yandex', { token: userInfoResponse.data }, {
                            headers: {
                                Authorization: `Bearer ${userInfoResponse.data}`,
                            },
                        })
                        .then((response) => {
                            console.log("Ответ от API:", response.data);
                            navigate("/city-actions");
                        })
                        .catch((error) => {
                            console.error("Ошибка при отправке JWT на сервер:", error);
                        });
                })
                .catch((error) => {
                    console.error("Ошибка при получении данных пользователя от Яндекса:", error);
                });
        }
    }, [token, navigate, setToken]);

    useEffect(() => {
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

        window.onload = () => {
            const button = document.getElementById("button");

            if (button) {
                button.onclick = () => {
                    console.log("Кнопка нажата. Инициализация YaAuthSuggest...");

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
                                console.log("Сообщение с токеном:", data);
                                if (data && data.access_token) {
                                    setToken(data.access_token);
                                    console.log("Token saved");
                                }
                            })
                            .catch((error) => console.log("Обработка ошибки:", error));
                    } else {
                        console.error("YaAuthSuggest недоступен. Проверьте, загружен ли SDK.");
                    }
                };
            } else {
                console.error("Кнопка с ID 'button' не найдена.");
            }
        };
    }, [setToken]);

    return (
        <div style={{ textAlign: "center", marginTop: "50px" }}>
            <h1>Вход через Яндекс</h1>
            <button id="button">Авторизоваться</button>
            <div id="buttonContainer"></div>
        </div>
    );
};

export default LoginPage;
