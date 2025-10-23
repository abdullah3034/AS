package com.gearsync.backend.repository;

import com.gearsync.backend.model.User;
import com.gearsync.backend.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByCustomer(User customer);
}
