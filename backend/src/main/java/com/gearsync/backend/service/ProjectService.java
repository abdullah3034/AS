package com.gearsync.backend.service;

import com.gearsync.backend.model.*;
import com.gearsync.backend.repository.ProjectRepository;
import com.gearsync.backend.repository.UserRepository;
import com.gearsync.backend.repository.VehicleRepository;
import com.gearsync.backend.exception.BadRequestException;
import com.gearsync.backend.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public ProjectService(ProjectRepository projectRepository,
                          UserRepository userRepository,
                          VehicleRepository vehicleRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public Page<Project> listMyProjects(String email, List<ProjectStatus> statuses, List<ProjectType> types, int page, int size) {
        User me = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Pageable pageable = PageRequest.of(page, size);
        if (statuses == null || statuses.isEmpty()) statuses = java.util.Arrays.asList(ProjectStatus.values());
        if (types == null || types.isEmpty()) types = java.util.Arrays.asList(ProjectType.values());
        return projectRepository.findByVehicle_CustomerAndStatusInAndTypeIn(me, statuses, types, pageable);
    }

    public Project getMyProject(String email, Long id) {
        User me = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Project p = projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        if (!p.getVehicle().getCustomer().getId().equals(me.getId())) throw new RuntimeException("Forbidden");
        return p;
    }

    @Transactional
    public Project createMyProject(String email, Long vehicleId, Project payload) {
        User me = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Vehicle v = vehicleRepository.findById(vehicleId).orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        if (!v.getCustomer().getId().equals(me.getId())) throw new RuntimeException("Forbidden");
        payload.setId(null);
        payload.setVehicle(v);
        if (payload.getStatus() == null) payload.setStatus(ProjectStatus.REQUESTED);
        else if (!isCustomerAllowedStatus(payload.getStatus())) {
            throw new BadRequestException("Customers cannot set status to " + payload.getStatus());
        }
        return projectRepository.save(payload);
    }

    @Transactional
    public Project updateMyProject(String email, Long id, Project update) {
        Project existing = getMyProject(email, id);
        existing.setType(update.getType());
        existing.setTitle(update.getTitle());
        existing.setDescription(update.getDescription());
        if (update.getStatus() != null) {
            if (!isCustomerAllowedStatus(update.getStatus())) {
                throw new BadRequestException("Customers cannot set status to " + update.getStatus());
            }
            existing.setStatus(update.getStatus());
        }
        return projectRepository.save(existing);
    }

    @Transactional
    public void deleteMyProject(String email, Long id) {
        Project existing = getMyProject(email, id);
        projectRepository.delete(existing);
    }
    private boolean isCustomerAllowedStatus(ProjectStatus status) {
        return status == ProjectStatus.REQUESTED || status == ProjectStatus.CANCELLED;
    }
}
