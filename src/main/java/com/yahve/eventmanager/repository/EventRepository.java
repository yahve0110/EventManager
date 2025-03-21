package com.yahve.eventmanager.repository;

import com.yahve.eventmanager.entity.Event;
import com.yahve.eventmanager.event.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer>, JpaSpecificationExecutor<Event> {
  boolean existsByLocationId(Integer locationId);

  @Query("""
    SELECT e FROM Event e 
    WHERE (:name IS NULL OR e.name LIKE %:name%) 
    AND (:placesMin IS NULL OR e.maxPlaces >= :placesMin) 
    AND (:placesMax IS NULL OR e.maxPlaces <= :placesMax) 
    AND (CAST(:dateStartAfter as date) IS NULL OR e.date >= :dateStartAfter) 
    AND (CAST(:dateStartBefore as date) IS NULL OR e.date <= :dateStartBefore) 
    AND (:costMin IS NULL OR e.cost >= :costMin) 
    AND (:costMax IS NULL OR e.cost <= :costMax) 
    AND (:durationMin IS NULL OR e.duration >= :durationMin) 
    AND (:durationMax IS NULL OR e.duration <= :durationMax) 
    AND (:locationId IS NULL OR e.locationId = :locationId) 
    AND (:eventStatus IS NULL OR e.status = :eventStatus)
    """)
  List<Event> findEvents(
    @Param("name") String name,
    @Param("placesMin") Integer placesMin,
    @Param("placesMax") Integer placesMax,
    @Param("dateStartAfter") LocalDateTime dateStartAfter,
    @Param("dateStartBefore") LocalDateTime dateStartBefore,
    @Param("costMin") BigDecimal costMin,
    @Param("costMax") BigDecimal costMax,
    @Param("durationMin") Integer durationMin,
    @Param("durationMax") Integer durationMax,
    @Param("locationId") Integer locationId,
    @Param("eventStatus") EventStatus eventStatus
  );

  List<Event> findByOwnerId(Long ownerId);

  List<Event> findByStatusAndDateBefore(EventStatus status, LocalDateTime now);

  @Query(value = "SELECT * FROM events e WHERE e.status = :status AND (e.date + (e.duration * INTERVAL '1 minute')) <= :now", nativeQuery = true)
  List<Event> findByStatusAndEndDateBefore(@Param("status") String status, @Param("now") LocalDateTime now);
}
