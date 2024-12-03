import React, { useEffect } from "react";
import { useAuth } from "../AuthProvider";
import { useNavigate } from "react-router-dom";

const LoginPage = () => {
    const navigate = useNavigate();
    const { token, setToken, loginWithYandex } = useAuth();

    useEffect(() => {
        if (token) {
            navigate("/city-actions");
        }
    }, [token, navigate]);

    useEffect(() => {
        const button = document.getElementById("button");
        if (button) {
            button.onclick = () => {
                loginWithYandex();
            };
        }
    }, [loginWithYandex]);

    return (
        <div style={{ textAlign: "center", marginTop: "50px" }}>
            <h1>Вход через Яндекс</h1>
            <button id="button">Авторизоваться</button>
            <div id="buttonContainer"></div>
        </div>
    );
};

export default LoginPage;
