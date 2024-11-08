package eoeqs.controller;

import eoeqs.model.City;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class CityWebSocketController {
    private final SimpMessagingTemplate messagingTemplate;

    public CityWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/city")
    public void sendCityUpdate(City city) {
        messagingTemplate.convertAndSend("/topic/cities", city); // Отправка обновлений
    }
}
