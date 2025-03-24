package com.yahve.eventmanager.event;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventSender {

  private static final Logger logger = LoggerFactory.getLogger(KafkaEventSender.class);
  private final KafkaTemplate<Long, KafkaEventMessage> kafkaTemplate;

  @Value("${kafka.topic.event-updates}")
  private String eventTopic;

  public void sendEvent(KafkaEventMessage kafkaEventMessage) {
    logger.info("Sending event: {}", kafkaEventMessage);

    kafkaTemplate.send(eventTopic, kafkaEventMessage.getEventId(), kafkaEventMessage)
      .whenComplete((result, ex) -> {
        if (ex == null) {
          logger.info("Send successful: {}", result.getRecordMetadata());
        } else {
          logger.error("Send failed for eventId={}: {}", kafkaEventMessage.getEventId(), ex.getMessage());
        }
      });
  }
}

