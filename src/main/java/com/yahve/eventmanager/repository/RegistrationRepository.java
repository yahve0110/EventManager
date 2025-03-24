package com.yahve.eventmanager.repository;

import com.yahve.eventmanager.entity.Registration;
import com.yahve.eventmanager.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
  boolean existsByUserIdAndEvent(Long userId, Event event);
  Optional<Registration> findByUserIdAndEvent(Long userId, Event event);
  List<Registration> findByUserId(Long userId);
  List<Registration> findByEventId(Integer eventId);

}
