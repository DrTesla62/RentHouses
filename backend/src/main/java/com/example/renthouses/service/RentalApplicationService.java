package com.example.renthouses.service;

import com.example.renthouses.entity.RentalApplication;

import java.util.List;

public interface RentalApplicationService {
    RentalApplication createRentalApplication(RentalApplication rentalApplication);
    RentalApplication getRentalApplicationById(Long id);
    List<RentalApplication> getAllRentalApplications();
    RentalApplication updateRentalApplication(RentalApplication rentalApplication);
    void deleteRentalApplication(Long id);
    void approveRentalApplication(Long id);
    void rejectRentalApplication(Long id);
    List<RentalApplication> getApplicationsForOwner(Long ownerId);
}