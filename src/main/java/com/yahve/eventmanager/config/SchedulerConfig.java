package com.yahve.eventmanager.config;

import com.yahve.eventmanager.entity.Event;
import com.yahve.eventmanager.event.EventStatus;
import com.yahve.eventmanager.repository.EventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

  private static final Logger logger = LoggerFactory.getLogger(SchedulerConfig.class);

  private final EventRepository eventRepository;

  @Transactional
  @Scheduled(fixedRateString = "${scheduler.event-status-update-rate}")
  public void updateEventStatuses() {
    logger.info("Checking events for status updates...");

    LocalDateTime now = LocalDateTime.now();

    List<Event> eventsToStart = eventRepository.findByStatusAndDateBefore(EventStatus.WAIT_START, now);
    for (Event event : eventsToStart) {
      event.setStatus(EventStatus.STARTED);
      logger.info("Event {} started", event.getId());
    }

    List<Event> eventsToFinish = eventRepository.findByStatusAndEndDateBefore(EventStatus.STARTED.name(), now);
    for (Event event : eventsToFinish) {
      event.setStatus(EventStatus.FINISHED);
      logger.info("Event {} completed", event.getId());
    }

    eventRepository.saveAll(eventsToStart);
    eventRepository.saveAll(eventsToFinish);

    logger.info("Event status update completed.");
  }
}

