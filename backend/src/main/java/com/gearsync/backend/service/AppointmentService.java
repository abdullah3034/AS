package com.gearsync.backend.service;

import com.gearsync.backend.model.*;
import com.gearsync.backend.repository.AppointmentRepository;
import com.gearsync.backend.repository.UserRepository;
import com.gearsync.backend.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              UserRepository userRepository,
                              VehicleRepository vehicleRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public List<Appoinment> listMyAppointments(String email) {
        User me = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return appointmentRepository.findByCustomer(me);
    }

    public Appoinment getMyAppointment(String email, Long id) {
        User me = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Appoinment a = appointmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Appointment not found"));
        if (!a.getCustomer().getId().equals(me.getId())) throw new RuntimeException("Forbidden");
        return a;
    }

    @Transactional
    public Appoinment bookAppointment(String email, Long vehicleId, Appoinment payload) {
        User me = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Vehicle v = vehicleRepository.findById(vehicleId).orElseThrow(() -> new RuntimeException("Vehicle not found"));
        if (!v.getCustomer().getId().equals(me.getId())) throw new RuntimeException("Forbidden");
        payload.setId(null);
        payload.setCustomer(me);
        payload.setVehicle(v);
        if (payload.getStatus() == null) payload.setStatus(AppointmentStatus.BOOKED);
        return appointmentRepository.save(payload);
    }

    @Transactional
    public Appoinment updateMyAppointment(String email, Long id, Appoinment update) {
        Appoinment existing = getMyAppointment(email, id);
        existing.setSlotStart(update.getSlotStart());
        existing.setSlotEnd(update.getSlotEnd());
        existing.setStatus(update.getStatus());
        return appointmentRepository.save(existing);
    }

    @Transactional
    public void cancelMyAppointment(String email, Long id) {
        Appoinment existing = getMyAppointment(email, id);
        existing.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(existing);
    }
}
