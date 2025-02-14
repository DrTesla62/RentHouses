package com.example.renthouses.service;

import com.example.renthouses.entity.*;
import com.example.renthouses.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.renthouses.exception.InvalidRequestException;
import com.example.renthouses.exception.ResourceNotFoundException;
import com.example.renthouses.repository.RentalApplicationRepository;
import com.example.renthouses.repository.PropertyRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RentalApplicationServiceImpl implements RentalApplicationService {
    @Autowired
    private final RentalApplicationRepository rentalApplicationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PropertyRepository propertyRepository;

    public RentalApplicationServiceImpl(
            RentalApplicationRepository rentalApplicationRepository,
            UserRepository userRepository,
            PropertyRepository propertyRepository) {
        this.rentalApplicationRepository = rentalApplicationRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
    }

    @Override
    @Transactional
    public RentalApplication createRentalApplication(RentalApplication application) {

        // Check if property is available for rent
        if (!PropertyStatus.APPROVED.equals(application.getProperty().getStatus())) {
            throw new InvalidRequestException("Can only apply for properties with APPROVED status");
        }

        User renter = userRepository.findByUsername(application.getRenter().getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Renter not found"));

        Property property = propertyRepository.findById(application.getProperty().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        application.setRenter(renter);
        application.setProperty(property);
        application.setStatus(ApplicationStatus.PENDING);

        return rentalApplicationRepository.save(application);
    }

    @Override
    public RentalApplication getRentalApplicationById(Long id) {
        return rentalApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental application not found with id: " + id));
    }

    @Override
    public List<RentalApplication> getApplicationsForOwner(Long ownerId) {
        return rentalApplicationRepository.findAllByOwnerId(ownerId);
    }

    @Override
    public List<RentalApplication> getAllRentalApplications() {
        return rentalApplicationRepository.findAll();
    }

    @Override
    public RentalApplication updateRentalApplication(RentalApplication rentalApplication) {
        return rentalApplicationRepository.save(rentalApplication);
    }

    @Override
    public void deleteRentalApplication(Long id) {
        rentalApplicationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void approveRentalApplication(Long id) {
        RentalApplication application = getRentalApplicationById(id);
        validateApplicationCanBeProcessed(application);

        // Update only the application status to APPROVED
        application.setStatus(ApplicationStatus.APPROVED);
        rentalApplicationRepository.save(application);

        // Reject all other pending applications for this property
        List<RentalApplication> pendingApplications = rentalApplicationRepository.findAll().stream()
                .filter(app -> app.getProperty().getId().equals(application.getProperty().getId())
                        && app.getStatus() == ApplicationStatus.PENDING
                        && !app.getId().equals(id))
                .collect(Collectors.toList());

        pendingApplications.forEach(pendingApp -> {
            pendingApp.setStatus(ApplicationStatus.REJECTED);
            rentalApplicationRepository.save(pendingApp);
        });
    }

    @Override
    public void rejectRentalApplication(Long id) {
        RentalApplication application = getRentalApplicationById(id);
        validateApplicationCanBeProcessed(application);

        application.setStatus(ApplicationStatus.REJECTED);
        rentalApplicationRepository.save(application);
    }

    private void validateApplicationCanBeProcessed(RentalApplication application) {
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new InvalidRequestException(
                    "Can only process applications in PENDING status. Current status: " + application.getStatus()
            );
        }
    }
}