package com.example.renthouses.service;

import com.example.renthouses.entity.Property;
import com.example.renthouses.entity.PropertyStatus;
import com.example.renthouses.exception.InvalidRequestException;
import com.example.renthouses.exception.ResourceNotFoundException;
import com.example.renthouses.repository.PropertyRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyServiceImpl implements PropertyService {
    private final PropertyRepository propertyRepository;

    public PropertyServiceImpl(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @Override
    public Property createProperty(Property property) {
        property.setStatus(PropertyStatus.PENDING_APPROVAL);

        return propertyRepository.save(property);
    }

    @Override
    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
    }

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public List<Property> getApprovedProperties() {
        return propertyRepository.findByStatus(PropertyStatus.APPROVED);
    }

    @Override
    public Property updateProperty(Property property) {
        return propertyRepository.save(property);
    }

    @Override
    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }

    @Override
    public void approveProperty(Long id) {
        Property property = getPropertyById(id);
        if (property.getStatus() != PropertyStatus.PENDING_APPROVAL) {
            throw new InvalidRequestException("Can only approve properties in PENDING_APPROVAL status");
        }
        property.setStatus(PropertyStatus.APPROVED);
        propertyRepository.save(property);
    }

    @Override
    public void markPropertyAsRented(Long id) {
        Property property = getPropertyById(id);
        if (property.getStatus() != PropertyStatus.APPROVED) {
            throw new InvalidRequestException("Can only mark APPROVED properties as rented");
        }
        property.setStatus(PropertyStatus.RENTED);
        propertyRepository.save(property);
    }

    @Override
    public void markPropertyAsUnavailable(Long id) {
        Property property = getPropertyById(id);
        if (property.getStatus() == PropertyStatus.PENDING_APPROVAL) {
            throw new InvalidRequestException("Cannot mark PENDING_APPROVAL properties as unavailable");
        }
        property.setStatus(PropertyStatus.UNAVAILABLE);
        propertyRepository.save(property);
    }

    public void rejectProperty(Long id, String reason) {
        Property property = getPropertyById(id);
        property.setStatus(PropertyStatus.UNAVAILABLE);
        // If you want to store the rejection reason, you'll need to add a field to the Property entity
        propertyRepository.save(property);
    }
}