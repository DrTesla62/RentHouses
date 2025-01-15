package com.example.renthouses.service;

import com.example.renthouses.entity.Property;
import com.example.renthouses.entity.PropertyStatus;
import com.example.renthouses.entity.User;
import com.example.renthouses.entity.Role;
import com.example.renthouses.exception.ResourceNotFoundException;
import com.example.renthouses.repository.PropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    private PropertyService propertyService;
    private Property testProperty;
    private User testOwner;

    @BeforeEach
    void setUp() {
        propertyService = new PropertyServiceImpl(propertyRepository);

        // Setup test owner
        Role ownerRole = new Role("OWNER");
        testOwner = new User("testowner", "password", ownerRole);
        testOwner.setId(1L);

        // Setup test property
        testProperty = new Property();
        testProperty.setId(1L);
        testProperty.setTitle("Test Property");
        testProperty.setDescription("Test Description");
        testProperty.setAddress("Test Address");
        testProperty.setPrice(1000.0);
        testProperty.setOwner(testOwner);
        testProperty.setStatus(PropertyStatus.PENDING_APPROVAL);
    }

    @Test
    void createProperty_ShouldSetStatusToPendingApproval() {
        // Arrange
        Property propertyToCreate = new Property();
        propertyToCreate.setTitle("New Property");
        propertyToCreate.setStatus(PropertyStatus.APPROVED); // Try to set it as approved

        when(propertyRepository.save(any(Property.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Property createdProperty = propertyService.createProperty(propertyToCreate);

        // Assert
        assertEquals(PropertyStatus.PENDING_APPROVAL, createdProperty.getStatus());
        verify(propertyRepository).save(propertyToCreate);
    }

    @Test
    void getPropertyById_ShouldReturnProperty_WhenExists() {
        // Arrange
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));

        // Act
        Property foundProperty = propertyService.getPropertyById(1L);

        // Assert
        assertNotNull(foundProperty);
        assertEquals(testProperty.getId(), foundProperty.getId());
        assertEquals(testProperty.getTitle(), foundProperty.getTitle());
    }

    @Test
    void getPropertyById_ShouldThrowException_WhenNotExists() {
        // Arrange
        when(propertyRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            propertyService.getPropertyById(999L);
        });
    }

    @Test
    void getAllProperties_ShouldReturnAllProperties() {
        // Arrange
        List<Property> expectedProperties = Arrays.asList(testProperty);
        when(propertyRepository.findAll()).thenReturn(expectedProperties);

        // Act
        List<Property> actualProperties = propertyService.getAllProperties();

        // Assert
        assertEquals(expectedProperties.size(), actualProperties.size());
        assertEquals(expectedProperties.get(0).getId(), actualProperties.get(0).getId());
    }

    @Test
    void approveProperty_ShouldUpdateStatus() {
        // Arrange
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(Property.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        propertyService.approveProperty(1L);

        // Assert
        assertEquals(PropertyStatus.APPROVED, testProperty.getStatus());
        verify(propertyRepository).save(testProperty);
    }

    @Test
    void deleteProperty_ShouldCallRepository() {
        // Arrange
        Long propertyId = 1L;

        // Act
        propertyService.deleteProperty(propertyId);

        // Assert
        verify(propertyRepository).deleteById(propertyId);
    }
}