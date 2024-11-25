import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../AuthProvider";

const CallbackPage = () => {
    const { setToken } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        const scriptId = "yandex-sdk-token";

        if (!document.getElementById(scriptId)) {
            const script = document.createElement("script");
            script.id = scriptId;
            script.src = "https://yastatic.net/s3/passport-sdk/autofill/v1/sdk-suggest-token-with-polyfills-latest.js";
            script.async = true;

            script.onload = () => {
                console.log("Яндекс SDK Token успешно загружен.");

                if (typeof window.YaSendSuggestToken === "function") {
                    console.log("Отправляем токен Яндекса на сервер для валидации...");

                    window.YaSendSuggestToken("https://localhost:8686/api/auth/yandex")
                        .then((response) => {
                            console.log("Ответ от сервера получен. Статус: ", response.status);
                            if (!response.ok) {
                                console.error("Ошибка: Сервер отклонил токен.");
                                throw new Error("Invalid response from server.");
                            }
                            return response.json();
                        })
                        .then((data) => {
                            if (data.jwtToken) {
                                console.log("JWT получен от сервера: ", data.jwtToken);
                                setToken(data.jwtToken);
                                console.log("JWT сохранён в контексте.");

                                if (window.opener) {
                                    console.log("Перенаправляем родительское окно на /city-actions...");
                                    window.opener.location.href = "/city-actions";
                                    window.close();
                                } else {
                                    console.log("Редирект на /city-actions...");
                                    navigate("/city-actions");
                                }
                            } else {
                                console.error("Ошибка: JWT токен отсутствует в ответе сервера.");
                            }
                        })
                        .catch((error) => {
                            console.error("Ошибка при обработке токена: ", error);
                        });
                } else {
                    console.error("YaSendSuggestToken не определён. Проверьте SDK.");
                }
            };

            script.onerror = () => {
                console.error("Ошибка загрузки скрипта YaSendSuggestToken.");
            };

            document.body.appendChild(script);
        }
    }, [setToken, navigate]);

    return (
        <div style={{ textAlign: "center", marginTop: "50px" }}>
            <h1>Обработка авторизации...</h1>
        </div>
    );
};

export default CallbackPage;
