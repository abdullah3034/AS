package com.gearsync.backend.controller;

import com.gearsync.backend.model.Appoinment;
import com.gearsync.backend.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/appointments")
public class CustomerAppointmentController {

    private final AppointmentService appointmentService;

    public CustomerAppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<Appoinment>> list(Authentication authentication) {
        return ResponseEntity.ok(appointmentService.listMyAppointments(authentication.getName()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Appoinment> get(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getMyAppointment(authentication.getName(), id));
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Appoinment> create(Authentication authentication,
                                             @RequestParam("vehicleId") Long vehicleId,
                                             @RequestBody Appoinment payload) {
        return ResponseEntity.ok(appointmentService.bookAppointment(authentication.getName(), vehicleId, payload));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Appoinment> update(Authentication authentication,
                                             @PathVariable Long id,
                                             @RequestBody Appoinment payload) {
        return ResponseEntity.ok(appointmentService.updateMyAppointment(authentication.getName(), id, payload));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable Long id) {
        appointmentService.cancelMyAppointment(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}


