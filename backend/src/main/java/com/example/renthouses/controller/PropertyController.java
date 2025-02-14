package com.example.renthouses.controller;

import com.example.renthouses.entity.Property;
import com.example.renthouses.entity.PropertyStatus;
import com.example.renthouses.entity.User;
import com.example.renthouses.service.PropertyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/properties")
public class PropertyController {
    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('OWNER')")
    public Property createProperty(@RequestBody Property property) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Set the owner of the new property
        property.setOwner(currentUser);
        return propertyService.createProperty(property);
    }

    @GetMapping("/{id}")
    public Property getPropertyById(@PathVariable Long id) {
        return propertyService.getPropertyById(id);
    }


    @GetMapping
    public List<Property> getAllProperties() {
        return propertyService.getAllProperties().stream()
                .filter(p -> p.getStatus() == PropertyStatus.APPROVED)
                .collect(Collectors.toList());
    }

    @GetMapping("/approved")
    public List<Property> getApprovedProperties() {
        return propertyService.getApprovedProperties();
    }

    @PutMapping
    @PreAuthorize("hasRole('OWNER')")
    public Property updateProperty(@RequestBody Property property) {
        return propertyService.updateProperty(property);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public void deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ADMIN')")  // Changed from hasRole('ADMIN')
    public ResponseEntity<Void> approveProperty(@PathVariable Long id) {
        propertyService.approveProperty(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('ADMIN')")  // Changed from hasRole('ADMIN')
    public ResponseEntity<Void> rejectProperty(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        String reason = payload.get("reason");
        propertyService.rejectProperty(id, reason);
        return ResponseEntity.ok().build();
    }
}