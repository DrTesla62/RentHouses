package com.example.renthouses.service;

import com.example.renthouses.entity.Property;

import java.util.List;

public interface PropertyService {
    Property createProperty(Property property);
    Property getPropertyById(Long id);
    List<Property> getAllProperties();
    List<Property> getApprovedProperties();
    Property updateProperty(Property property);
    void deleteProperty(Long id);
    void approveProperty(Long id);
    void markPropertyAsRented(Long id);
    void markPropertyAsUnavailable(Long id);
    void rejectProperty(Long id, String reason);
}