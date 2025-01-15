package com.example.renthouses.service;

import com.example.renthouses.entity.Property;
import com.example.renthouses.entity.PropertyStatus;
import com.example.renthouses.exception.InvalidRequestException;
import com.example.renthouses.exception.ResourceNotFoundException;
import com.example.renthouses.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
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
}