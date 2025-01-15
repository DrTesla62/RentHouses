package com.example.renthouses.service;

import org.springframework.stereotype.Service;
import com.example.renthouses.entity.ApplicationStatus;
import com.example.renthouses.entity.PropertyStatus;
import com.example.renthouses.entity.RentalApplication;
import com.example.renthouses.exception.InvalidRequestException;
import com.example.renthouses.exception.ResourceNotFoundException;
import com.example.renthouses.repository.RentalApplicationRepository;
import com.example.renthouses.repository.PropertyRepository;

import java.util.List;

@Service
public class RentalApplicationServiceImpl implements RentalApplicationService {
    private final RentalApplicationRepository rentalApplicationRepository;
    private final PropertyRepository propertyRepository;

    public RentalApplicationServiceImpl(RentalApplicationRepository rentalApplicationRepository,
                                        PropertyRepository propertyRepository) {
        this.rentalApplicationRepository = rentalApplicationRepository;
        this.propertyRepository = propertyRepository;
    }

    @Override
    public RentalApplication createRentalApplication(RentalApplication rentalApplication) {
        // Check if property is available for rent
        if (rentalApplication.getProperty().getStatus() != PropertyStatus.APPROVED) {
            throw new InvalidRequestException("Can only apply for properties with APPROVED status");
        }

        rentalApplication.setStatus(ApplicationStatus.PENDING);
        return rentalApplicationRepository.save(rentalApplication);
    }

    @Override
    public RentalApplication getRentalApplicationById(Long id) {
        return rentalApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental application not found with id: " + id));
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
    public void approveRentalApplication(Long id) {
        RentalApplication application = getRentalApplicationById(id);
        validateApplicationCanBeProcessed(application);

        // Update application status
        application.setStatus(ApplicationStatus.APPROVED);
        rentalApplicationRepository.save(application);

        // Update property status
        application.getProperty().setStatus(PropertyStatus.RENTED);
        propertyRepository.save(application.getProperty());
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