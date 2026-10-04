package com.javid.userservice.producer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.javid.userservice.config.RabbitMQConfig;
import com.javid.userservice.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceProducer {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;


    public void sendEmail(String eventType, String payload) {
        Object eventObj = deserializePayload(eventType, payload);
        rabbitTemplate.convertAndSend(RabbitMQConfig.SEND_EMAIL_QUEUE, eventObj);
    }

    @SneakyThrows
    private Object deserializePayload(String eventType, String payload) {
        return switch (eventType) {
            case "USER_REGISTERED" -> objectMapper.readValue(payload, UserRegisteredEvent.class);
            default -> throw new IllegalArgumentException("Unknown event type: " + eventType);
        };
    }

}
