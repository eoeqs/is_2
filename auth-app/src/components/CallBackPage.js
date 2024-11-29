
import React, { useEffect } from "react";

const CallbackPage = () => {
    useEffect(() => {
        const scriptId = "yandex-sdk-token";

        if (!document.getElementById(scriptId)) {
            const script = document.createElement("script");
            script.id = scriptId;
            script.src = "https://yastatic.net/s3/passport-sdk/autofill/v1/sdk-suggest-token-with-polyfills-latest.js";
            script.async = true;

            script.onload = () => {
                console.log("Яндекс SDK  успешно загружен.");

                if (typeof window.YaSendSuggestToken === "function") {
                    console.log("Отправляем токен Яндекса на сервер для валидации...");

                    window.YaSendSuggestToken("https://localhost:8686/api/auth/yandex", {
                        token: true,
                    })
                        .then((response) => {
                            console.log("Ответ от сервера: ", response);
                        })
                        .catch((error) => {
                            console.error("Ошибка при отправке токена: ", error);
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
        }
    }, []);

    return (
        <html lang="ru">
        <head>
            <meta charSet="utf-8" />
            <meta
                name="viewport"
                content="width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1, shrink-to-fit=no, viewport-fit=cover"
            />
            <meta httpEquiv="X-UA-Compatible" content="ie=edge" />
            <style>
                {`
                        html, body {
                            background: #eee;
                        }
                    `}
            </style>
        </head>
        <body>
        <h1>Обработка токена...</h1>
        </body>
        </html>
    );
};

export default CallbackPage;
