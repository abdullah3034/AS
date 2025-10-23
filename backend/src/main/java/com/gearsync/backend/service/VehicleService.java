package com.gearsync.backend.service;

import com.gearsync.backend.model.User;
import com.gearsync.backend.model.Vehicle;
import com.gearsync.backend.repository.UserRepository;
import com.gearsync.backend.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public VehicleService(VehicleRepository vehicleRepository, UserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    public List<Vehicle> listMyVehicles(String email) {
        User me = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return vehicleRepository.findByCustomer(me);
    }

    public Vehicle getMyVehicle(String email, Long id) {
        User me = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Vehicle v = vehicleRepository.findById(id).orElseThrow(() -> new RuntimeException("Vehicle not found"));
        if (!v.getCustomer().getId().equals(me.getId())) throw new RuntimeException("Forbidden");
        return v;
    }

    @Transactional
    public Vehicle addMyVehicle(String email, Vehicle payload) {
        User me = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        payload.setId(null);
        payload.setCustomer(me);
        return vehicleRepository.save(payload);
    }

    @Transactional
    public Vehicle updateMyVehicle(String email, Long id, Vehicle update) {
        Vehicle existing = getMyVehicle(email, id);
        existing.setMake(update.getMake());
        existing.setModel(update.getModel());
        existing.setYear(update.getYear());
        existing.setVin(update.getVin());
        existing.setRegNumber(update.getRegNumber());
        existing.setMileage(update.getMileage());
        existing.setColor(update.getColor());
        return vehicleRepository.save(existing);
    }

    @Transactional
    public void deleteMyVehicle(String email, Long id) {
        Vehicle existing = getMyVehicle(email, id);
        vehicleRepository.delete(existing);
    }
}
