import React, {useEffect} from "react";
import {useAuth} from "../AuthProvider";
import {useNavigate} from "react-router-dom";

const LoginPage = () => {
    const navigate = useNavigate();

    const {token, setToken} = useAuth();

    useEffect(() => {
        if (token) {
            navigate("/city-actions");
        }
    }, [token, navigate]);
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
                            .then(({handler}) => handler())
                            .then((data) => {
                                console.log("Сообщение с токеном:", data);
                                if (data && data.access_token) {
                                    setToken(data.access_token);
                                    console.log("token sohranen");
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
        <div style={{textAlign: "center", marginTop: "50px"}}>
            <h1>Вход через Яндекс</h1>
            <button id="button">Авторизоваться</button>
            <div id="buttonContainer"></div>
        </div>
    );
};

export default LoginPage;
