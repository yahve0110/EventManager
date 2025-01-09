package com.yahve.eventmanager.Repository;

import com.yahve.eventmanager.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LocationRepository extends JpaRepository<Location, Integer> {
  boolean existsByNameAndAddress(String name, String address);

}
