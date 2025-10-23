package com.gearsync.backend.service;

import com.gearsync.backend.exception.BadRequestException;
import com.gearsync.backend.exception.ResourceNotFoundException;
import com.gearsync.backend.model.*;
import com.gearsync.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeService {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final ProjectRepository projectRepository;
    private final TimeLogRepository timeLogRepository;

    public EmployeeService(UserRepository userRepository,
                           AppointmentRepository appointmentRepository,
                           ProjectRepository projectRepository,
                           TimeLogRepository timeLogRepository) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.projectRepository = projectRepository;
        this.timeLogRepository = timeLogRepository;
    }

    public List<Appoinment> listMyAppointments(String email) {
        User me = getEmployee(email);
        return appointmentRepository.findByAssignedEmployee(me);
    }

    public Appoinment getMyAppointment(String email, Long id) {
        User me = getEmployee(email);
        Appoinment a = appointmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        if (a.getAssignedEmployee() == null || !a.getAssignedEmployee().getId().equals(me.getId())) throw new ResourceNotFoundException("Appointment not assigned to you");
        return a;
    }

    @Transactional
    public Appoinment updateAppointmentStatus(String email, Long id, AppointmentStatus status) {
        if (status == null) throw new BadRequestException("Status required");
        Appoinment a = getMyAppointment(email, id);
        a.setStatus(status);
        return appointmentRepository.save(a);
    }

    public List<Project> listMyProjects(String email) {
        User me = getEmployee(email);
        // assignedEmployee may be null; filter by assignedEmployee
        return projectRepository.findAll().stream().filter(p -> p.getAssignedEmployee() != null && p.getAssignedEmployee().getId().equals(me.getId())).toList();
    }

    public Project getMyProject(String email, Long id) {
        User me = getEmployee(email);
        Project p = projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        if (p.getAssignedEmployee() == null || !p.getAssignedEmployee().getId().equals(me.getId())) throw new ResourceNotFoundException("Project not assigned to you");
        return p;
    }

    @Transactional
    public Project updateProjectStatus(String email, Long id, ProjectStatus status) {
        if (status == null) throw new BadRequestException("Status required");
        if (status == ProjectStatus.REQUESTED) throw new BadRequestException("Cannot set status to REQUESTED");
        Project p = getMyProject(email, id);
        p.setStatus(status);
        return projectRepository.save(p);
    }

    @Transactional
    public TimeLog createTimeLog(String email, Long projectId, TimeLog payload) {
        User me = getEmployee(email);
        Project p = getMyProject(email, projectId);
        payload.setId(null);
        payload.setEmployee(me);
        payload.setProject(p);
        return timeLogRepository.save(payload);
    }

    public List<TimeLog> getProjectTimeLogs(String email, Long projectId) {
        Project p = getMyProject(email, projectId);
        return timeLogRepository.findByProject(p);
    }

    @Transactional
    public TimeLog updateTimeLog(String email, Long id, TimeLog update) {
        TimeLog existing = timeLogRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("TimeLog not found"));
        User me = getEmployee(email);
        if (!existing.getEmployee().getId().equals(me.getId())) throw new ResourceNotFoundException("TimeLog not owned by you");
        existing.setStartTime(update.getStartTime());
        existing.setEndTime(update.getEndTime());
        existing.setDurationMinutes(update.getDurationMinutes());
        existing.setNotes(update.getNotes());
        return timeLogRepository.save(existing);
    }

    @Transactional
    public void deleteTimeLog(String email, Long id) {
        TimeLog existing = timeLogRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("TimeLog not found"));
        User me = getEmployee(email);
        if (!existing.getEmployee().getId().equals(me.getId())) throw new ResourceNotFoundException("TimeLog not owned by you");
        timeLogRepository.delete(existing);
    }

    private User getEmployee(String email) {
        User me = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (me.getRole() != Role.EMPLOYEE && me.getRole() != Role.ADMIN) throw new BadRequestException("Not an employee");
        return me;
    }
}


