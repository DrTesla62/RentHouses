package com.example.renthouses.service;

import com.example.renthouses.entity.Property;
import com.example.renthouses.entity.PropertyStatus;
import com.example.renthouses.entity.User;
import com.example.renthouses.entity.Role;
import com.example.renthouses.exception.InvalidRequestException;
import com.example.renthouses.repository.PropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PropertyStatusFlowTest {

    @Mock
    private PropertyRepository propertyRepository;

    private PropertyService propertyService;
    private Property testProperty;

    @BeforeEach
    void setUp() {
        propertyService = new PropertyServiceImpl(propertyRepository);

        Role ownerRole = new Role("OWNER");
        User owner = new User("testowner", "password", ownerRole);
        owner.setId(1L);

        testProperty = new Property();
        testProperty.setId(1L);
        testProperty.setTitle("Test Property");
        testProperty.setDescription("Test Description");
        testProperty.setPrice(1000.0);
        testProperty.setOwner(owner);
    }

    @Test
    void newProperty_ShouldBeInPendingApprovalStatus() {
        // Arrange
        testProperty.setStatus(PropertyStatus.APPROVED); // Try to set it as approved
        when(propertyRepository.save(any(Property.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Property savedProperty = propertyService.createProperty(testProperty);

        // Assert
        assertEquals(PropertyStatus.PENDING_APPROVAL, savedProperty.getStatus());
    }

    @Test
    void approveProperty_ShouldChangeStatusToApproved() {
        // Arrange
        testProperty.setStatus(PropertyStatus.PENDING_APPROVAL);
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        // Act
        propertyService.approveProperty(1L);

        // Assert
        assertEquals(PropertyStatus.APPROVED, testProperty.getStatus());
    }

    @Test
    void markPropertyAsRented_ShouldChangeStatusToRented() {
        // Arrange
        testProperty.setStatus(PropertyStatus.APPROVED);
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        // Act
        propertyService.markPropertyAsRented(1L);

        // Assert
        assertEquals(PropertyStatus.RENTED, testProperty.getStatus());
    }

    @Test
    void markPropertyAsUnavailable_ShouldChangeStatusToUnavailable() {
        // Arrange
        testProperty.setStatus(PropertyStatus.APPROVED);
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        // Act
        propertyService.markPropertyAsUnavailable(1L);

        // Assert
        assertEquals(PropertyStatus.UNAVAILABLE, testProperty.getStatus());
    }

    @Test
    void approveProperty_WhenAlreadyApproved_ShouldThrowException() {
        // Arrange
        testProperty.setStatus(PropertyStatus.APPROVED);
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));

        // Act & Assert
        assertThrows(InvalidRequestException.class, () -> {
            propertyService.approveProperty(1L);
        });
    }

    @Test
    void markAsRented_WhenNotApproved_ShouldThrowException() {
        // Arrange
        testProperty.setStatus(PropertyStatus.PENDING_APPROVAL);
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));

        // Act & Assert
        assertThrows(InvalidRequestException.class, () -> {
            propertyService.markPropertyAsRented(1L);
        });
    }
}