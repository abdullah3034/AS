package com.gearsync.backend.repository;

import com.gearsync.backend.model.Project;
import com.gearsync.backend.model.User;
import com.gearsync.backend.model.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByVehicle(Vehicle vehicle);
    List<Project> findByVehicle_Customer(User customer);
    Page<Project> findByVehicle_CustomerAndStatusInAndTypeIn(User customer, List<com.gearsync.backend.model.ProjectStatus> statuses, List<com.gearsync.backend.model.ProjectType> types, Pageable pageable);
}
