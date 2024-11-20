import React, { useEffect } from "react";
import {useNavigate} from "react-router-dom";
import {useAuth} from "../AuthProvider";

const CallbackPage = () => {
    const { token, setToken } = useAuth();
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
                    window.YaSendSuggestToken("https://localhost:8686/api/auth/yandex")
                        .then(() => {
                            console.log("Токен успешно отправлен на сервер.");

                            window.close();
                            if (window.opener) {
                                window.opener.location.href = "/city-actions";
                            }
                        })
                        .catch((error) => {
                            console.error("Ошибка при отправке токена:", error);
                        });
                } else {
                    console.error("YaSendSuggestToken не определён. Проверьте SDK.");
                }
            };

            script.onerror = () => {
                console.error("Ошибка загрузки скрипта YaSendSuggestToken.");
            };

            document.body.appendChild(script);
        } else {
            console.log("Скрипт уже загружен.");
            if (typeof window.YaSendSuggestToken === "function") {
                window.YaSendSuggestToken("https://localhost:8686/api/auth/yandex")
                    .then(() => {
                        console.log("Токен успешно отправлен на сервер.");
                        window.close();
                        if (window.opener) {
                            window.opener.location.href = "/city-actions";
                        }
                    })
                    .catch((error) => {
                        console.error("Ошибка при отправке токена:", error);
                    });
            } else {
                console.error("YaSendSuggestToken не определён. Проверьте SDK.");
            }
        }
    }, []);

    return (
        <div style={{ textAlign: "center", marginTop: "50px" }}>
            <h1>Обработка авторизации...</h1>
        </div>
    );
};

export default CallbackPage;
