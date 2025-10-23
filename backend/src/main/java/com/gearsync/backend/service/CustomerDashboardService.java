package com.gearsync.backend.service;

import com.gearsync.backend.dto.CustomerDashboardDto;
import com.gearsync.backend.model.*;
import com.gearsync.backend.repository.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CustomerDashboardService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final AppointmentRepository appointmentRepository;
    private final ProjectRepository projectRepository;

    public CustomerDashboardService(UserRepository userRepository,
                                    VehicleRepository vehicleRepository,
                                    AppointmentRepository appointmentRepository,
                                    ProjectRepository projectRepository) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.appointmentRepository = appointmentRepository;
        this.projectRepository = projectRepository;
    }

    public CustomerDashboardDto getDashboard(String email) {
        User me = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        long totalVehicles = vehicleRepository.findByCustomer(me).size();
        long totalAppointments = appointmentRepository.findByCustomer(me).size();
        long upcomingAppointments = appointmentRepository.findByCustomer(me)
                .stream().filter(a -> a.getSlotStart() != null && a.getSlotStart().isAfter(Instant.now())).count();
        long requested = projectRepository.findByVehicle_Customer(me)
                .stream().filter(p -> p.getStatus() == ProjectStatus.REQUESTED).count();
        long inProgress = projectRepository.findByVehicle_Customer(me)
                .stream().filter(p -> p.getStatus() == ProjectStatus.IN_PROGRESS).count();
        long completed = projectRepository.findByVehicle_Customer(me)
                .stream().filter(p -> p.getStatus() == ProjectStatus.COMPLETED).count();

        CustomerDashboardDto dto = new CustomerDashboardDto();
        dto.setTotalVehicles(totalVehicles);
        dto.setTotalAppointments(totalAppointments);
        dto.setUpcomingAppointments(upcomingAppointments);
        dto.setProjectsRequested(requested);
        dto.setProjectsInProgress(inProgress);
        dto.setProjectsCompleted(completed);
        return dto;
    }
}


