package com.example.renthouses.controller;

import com.example.renthouses.entity.RentalApplication;
import com.example.renthouses.entity.User;
import com.example.renthouses.service.RentalApplicationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rental-applications")
public class RentalApplicationController {
    private final RentalApplicationService rentalApplicationService;

    public RentalApplicationController(RentalApplicationService rentalApplicationService) {
        this.rentalApplicationService = rentalApplicationService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('RENTER')")
    public RentalApplication createRentalApplication(@RequestBody RentalApplication rentalApplication) {
        return rentalApplicationService.createRentalApplication(rentalApplication);
    }

    @GetMapping("/{id}")
    public RentalApplication getRentalApplicationById(@PathVariable Long id) {
        return rentalApplicationService.getRentalApplicationById(id);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<RentalApplication> getAllRentalApplications() {
        return rentalApplicationService.getAllRentalApplications();
    }

    @GetMapping("/owner")
    @PreAuthorize("hasAuthority('OWNER')")
    public List<RentalApplication> getApplicationsForOwner(@AuthenticationPrincipal User currentUser) {
        return rentalApplicationService.getApplicationsForOwner(currentUser.getId());
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OWNER')")
    public RentalApplication updateRentalApplication(@RequestBody RentalApplication rentalApplication) {
        return rentalApplicationService.updateRentalApplication(rentalApplication);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRentalApplication(@PathVariable Long id) {
        rentalApplicationService.deleteRentalApplication(id);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('OWNER')")
    public void approveRentalApplication(@PathVariable Long id) {
        rentalApplicationService.approveRentalApplication(id);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('OWNER')")
    public void rejectRentalApplication(@PathVariable Long id) {
        rentalApplicationService.rejectRentalApplication(id);
    }
}