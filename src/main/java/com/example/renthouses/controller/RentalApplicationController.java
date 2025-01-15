package com.example.renthouses.controller;

import com.example.renthouses.entity.RentalApplication;
import com.example.renthouses.service.RentalApplicationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rental-applications")
public class RentalApplicationController {
    private final RentalApplicationService rentalApplicationService;

    public RentalApplicationController(RentalApplicationService rentalApplicationService) {
        this.rentalApplicationService = rentalApplicationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('RENTER')")
    public RentalApplication createRentalApplication(@RequestBody RentalApplication rentalApplication) {
        return rentalApplicationService.createRentalApplication(rentalApplication);
    }

    @GetMapping("/{id}")
    public RentalApplication getRentalApplicationById(@PathVariable Long id) {
        return rentalApplicationService.getRentalApplicationById(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<RentalApplication> getAllRentalApplications() {
        return rentalApplicationService.getAllRentalApplications();
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public RentalApplication updateRentalApplication(@RequestBody RentalApplication rentalApplication) {
        return rentalApplicationService.updateRentalApplication(rentalApplication);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRentalApplication(@PathVariable Long id) {
        rentalApplicationService.deleteRentalApplication(id);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('OWNER')")
    public void approveRentalApplication(@PathVariable Long id) {
        rentalApplicationService.approveRentalApplication(id);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('OWNER')")
    public void rejectRentalApplication(@PathVariable Long id) {
        rentalApplicationService.rejectRentalApplication(id);
    }
}