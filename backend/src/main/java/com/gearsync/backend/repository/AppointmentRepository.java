package com.gearsync.backend.repository;

import com.gearsync.backend.model.Appoinment;
import com.gearsync.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appoinment, Long> {
    List<Appoinment> findByCustomer(User customer);
    List<Appoinment> findByAssignedEmployee(User employee);
}
