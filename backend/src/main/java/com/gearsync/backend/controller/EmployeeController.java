package com.gearsync.backend.controller;

import com.gearsync.backend.model.*;
import com.gearsync.backend.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // Appointments
    @GetMapping("/appointments")
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    public ResponseEntity<List<Appoinment>> listAppointments(Authentication authentication) {
        return ResponseEntity.ok(employeeService.listMyAppointments(authentication.getName()));
    }

    @GetMapping("/appointments/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    public ResponseEntity<Appoinment> getAppointment(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getMyAppointment(authentication.getName(), id));
    }

    @PatchMapping("/appointments/{id}/status")
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    public ResponseEntity<Appoinment> updateAppointmentStatus(Authentication authentication,
                                                              @PathVariable Long id,
                                                              @RequestParam("status") AppointmentStatus status) {
        return ResponseEntity.ok(employeeService.updateAppointmentStatus(authentication.getName(), id, status));
    }

    // Projects
    @GetMapping("/projects")
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    public ResponseEntity<List<Project>> listProjects(Authentication authentication) {
        return ResponseEntity.ok(employeeService.listMyProjects(authentication.getName()));
    }

    @GetMapping("/projects/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    public ResponseEntity<Project> getProject(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getMyProject(authentication.getName(), id));
    }

    @PatchMapping("/projects/{id}/status")
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    public ResponseEntity<Project> updateProjectStatus(Authentication authentication,
                                                       @PathVariable Long id,
                                                       @RequestParam("status") ProjectStatus status) {
        return ResponseEntity.ok(employeeService.updateProjectStatus(authentication.getName(), id, status));
    }

    // Time logs
    @PostMapping("/timelogs")
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    public ResponseEntity<TimeLog> createTimeLog(Authentication authentication,
                                                 @RequestParam("projectId") Long projectId,
                                                 @RequestBody TimeLog payload) {
        return ResponseEntity.ok(employeeService.createTimeLog(authentication.getName(), projectId, payload));
    }

    @GetMapping("/projects/{projectId}/timelogs")
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    public ResponseEntity<List<TimeLog>> getProjectTimeLogs(Authentication authentication,
                                                            @PathVariable Long projectId) {
        return ResponseEntity.ok(employeeService.getProjectTimeLogs(authentication.getName(), projectId));
    }

    @PutMapping("/timelogs/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    public ResponseEntity<TimeLog> updateTimeLog(Authentication authentication,
                                                 @PathVariable Long id,
                                                 @RequestBody TimeLog payload) {
        return ResponseEntity.ok(employeeService.updateTimeLog(authentication.getName(), id, payload));
    }

    @DeleteMapping("/timelogs/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    public ResponseEntity<Void> deleteTimeLog(Authentication authentication,
                                              @PathVariable Long id) {
        employeeService.deleteTimeLog(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
