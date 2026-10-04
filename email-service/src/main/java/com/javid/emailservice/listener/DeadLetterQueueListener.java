package com.javid.emailservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javid.emailservice.config.RabbitMQConfig;
import com.javid.emailservice.entity.FailedEventEntity;
import com.javid.emailservice.enums.FailedEventStatus;
import com.javid.emailservice.repository.FailedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeadLetterQueueListener {

    private final FailedEventRepository failedEventRepository;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQConfig.DEAD_LETTER_QUEUE)
    public void processFailedMessages(Message message) {

        try {
            MessageProperties props = message.getMessageProperties();
            String payload = new String(message.getBody(), StandardCharsets.UTF_8);
            String typeId = (String) props.getHeaders().get("__TypeId__");

            Map<String, Object> headers = props.getHeaders();
            Map<String, Object> xDeathData = extractFirstDeathHeader(headers);

            String exceptionMessage = extractErrorMessage(headers, xDeathData);
            String exceptionStackTrace = extractStackTrace(headers);
            String eventId = extractEventIdFromJson(payload);

            String originalQueue = xDeathData != null ? (String) xDeathData.get("queue") : props.getConsumerQueue();
            String originalExchange = xDeathData != null ? (String) xDeathData.get("exchange") : props.getReceivedExchange();
            String routingKey = props.getReceivedRoutingKey();
            Integer retryCount = extractRetryCount(xDeathData);

            FailedEventEntity failedEvent = FailedEventEntity.builder()
                    .eventId(eventId)
                    .eventType(typeId)
                    .payload(payload)
                    .errorMessage(exceptionMessage)
                    .exceptionStackTrace(exceptionStackTrace)
                    .originalQueue(originalQueue)
                    .originalExchange(originalExchange)
                    .routingKey(routingKey)
                    .retryCount(retryCount)
                    .status(FailedEventStatus.PENDING_RETRY)
                    .build();

            failedEventRepository.save(failedEvent);
            log.error("CRITICAL: Message saved to failed_events. EventType: {}, EventId: {}, Queue: {}, Retries: {}",
                    typeId, eventId, originalQueue, retryCount);

        } catch (Exception e) {
            log.error("Failed to save DLQ message into database", e);
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractFirstDeathHeader(Map<String, Object> headers) {
        if (headers != null && headers.containsKey("x-death")) {
            List<Map<String, Object>> xDeathList = (List<Map<String, Object>>) headers.get("x-death");
            if (xDeathList != null && !xDeathList.isEmpty()) {
                return xDeathList.get(0);
            }
        }
        return null;
    }

    private String extractErrorMessage(Map<String, Object> headers, Map<String, Object> xDeathData) {
        if (headers.containsKey("x-exception-message")) {
            return headers.get("x-exception-message").toString();
        }
        if (xDeathData != null && xDeathData.containsKey("reason")) {
            return "Death reason: " + xDeathData.get("reason");
        }
        return "Max retries exceeded or message rejected";
    }

    private String extractStackTrace(Map<String, Object> headers) {
        if (headers != null && headers.containsKey("x-exception-stacktrace")) {
            return headers.get("x-exception-stacktrace").toString();
        }
        return null;
    }

    private Integer extractRetryCount(Map<String, Object> xDeathData) {
        if (xDeathData != null && xDeathData.containsKey("count")) {
            Object countObj = xDeathData.get("count");
            if (countObj instanceof Number number) {
                return number.intValue();
            }
        }
        return 1;
    }

    private String extractEventIdFromJson(String json) {
        try {
            var node = objectMapper.readTree(json).get("eventId");
            return (node != null) ? node.asText() : "UNKNOWN";
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }
}