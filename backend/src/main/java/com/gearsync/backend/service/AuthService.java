package com.gearsync.backend.service;

import com.gearsync.backend.model.User;
import com.gearsync.backend.model.Vehicle;
import com.gearsync.backend.repository.VehicleRepository;
import com.gearsync.backend.dto.SignupRequest;
import com.gearsync.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean isEmailRegistered(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User register(User user) {
        // encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User signupWithVehicle(SignupRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setRole(com.gearsync.backend.model.Role.CUSTOMER);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        User saved = userRepository.save(user);

        if (request.getVehicle() != null) {
            Vehicle v = new Vehicle();
            v.setCustomer(saved);
            v.setMake(request.getVehicle().getMake());
            v.setModel(request.getVehicle().getModel());
            v.setYear(request.getVehicle().getYear());
            v.setVin(request.getVehicle().getVin());
            v.setRegNumber(request.getVehicle().getRegNumber());
            v.setMileage(request.getVehicle().getMileage());
            v.setColor(request.getVehicle().getColor());
            vehicleRepository.save(v);
        }

        return saved;
    }

    public boolean authenticate(String email, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return false;
        User user = userOpt.get();
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    // 👇 Add this so controller can fetch the full User
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
}