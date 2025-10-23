package com.gearsync.backend.controller;

import com.gearsync.backend.model.Vehicle;
import com.gearsync.backend.service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/vehicles")
public class CustomerVehicleController {

    private final VehicleService vehicleService;

    public CustomerVehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<Vehicle>> list(Authentication authentication) {
        return ResponseEntity.ok(vehicleService.listMyVehicles(authentication.getName()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Vehicle> get(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getMyVehicle(authentication.getName(), id));
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Vehicle> create(Authentication authentication, @RequestBody Vehicle payload) {
        return ResponseEntity.ok(vehicleService.addMyVehicle(authentication.getName(), payload));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Vehicle> update(Authentication authentication, @PathVariable Long id, @RequestBody Vehicle payload) {
        return ResponseEntity.ok(vehicleService.updateMyVehicle(authentication.getName(), id, payload));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable Long id) {
        vehicleService.deleteMyVehicle(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}


