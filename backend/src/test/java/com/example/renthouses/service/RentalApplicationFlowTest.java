package com.example.renthouses.service;

import com.example.renthouses.entity.*;
import com.example.renthouses.exception.InvalidRequestException;
import com.example.renthouses.repository.RentalApplicationRepository;
import com.example.renthouses.repository.PropertyRepository;
import com.example.renthouses.repository.UserRepository;
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
class RentalApplicationFlowTest {

    @Mock
    private RentalApplicationRepository rentalApplicationRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UserRepository userRepository;

    private RentalApplicationService rentalApplicationService;
    private RentalApplication testApplication;
    private Property testProperty;
    private User testRenter;
    private User testOwner;

    @BeforeEach
    void setUp() {
        rentalApplicationService = new RentalApplicationServiceImpl(rentalApplicationRepository, userRepository, propertyRepository);

        // Setup test users
        Role ownerRole = new Role("OWNER");
        testOwner = new User("testowner", "password", ownerRole);
        testOwner.setId(1L);

        Role renterRole = new Role("RENTER");
        testRenter = new User("testrenter", "password", renterRole);
        testRenter.setId(2L);

        // Setup test property
        testProperty = new Property();
        testProperty.setId(1L);
        testProperty.setTitle("Test Property");
        testProperty.setStatus(PropertyStatus.APPROVED);
        testProperty.setOwner(testOwner);

        // Setup test application
        testApplication = new RentalApplication();
        testApplication.setId(1L);
        testApplication.setRenter(testRenter);
        testApplication.setProperty(testProperty);
    }

    @Test
    void createApplication_ShouldSetStatusToPending() {
        // Arrange
        testProperty.setStatus(PropertyStatus.APPROVED);
        when(rentalApplicationRepository.save(any(RentalApplication.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RentalApplication savedApplication = rentalApplicationService.createRentalApplication(testApplication);

        // Assert
        assertEquals(ApplicationStatus.PENDING, savedApplication.getStatus());
    }

    @Test
    void approveApplication_ShouldUpdateStatusAndMarkPropertyAsRented() {
        // Arrange
        testApplication.setStatus(ApplicationStatus.PENDING);
        when(rentalApplicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));
        when(rentalApplicationRepository.save(any(RentalApplication.class))).thenReturn(testApplication);
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        // Act
        rentalApplicationService.approveRentalApplication(1L);

        // Assert
        assertEquals(ApplicationStatus.APPROVED, testApplication.getStatus());
        assertEquals(PropertyStatus.RENTED, testApplication.getProperty().getStatus());
        verify(propertyRepository).save(testProperty);
    }

    @Test
    void rejectApplication_ShouldUpdateStatus() {
        // Arrange
        testApplication.setStatus(ApplicationStatus.PENDING);
        when(rentalApplicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));
        when(rentalApplicationRepository.save(any(RentalApplication.class))).thenReturn(testApplication);

        // Act
        rentalApplicationService.rejectRentalApplication(1L);

        // Assert
        assertEquals(ApplicationStatus.REJECTED, testApplication.getStatus());
        verify(rentalApplicationRepository).save(testApplication);
    }

    @Test
    void createApplication_ForAlreadyRentedProperty_ShouldThrowException() {
        // Arrange
        testProperty.setStatus(PropertyStatus.RENTED);
        testApplication.setProperty(testProperty);

        // Act & Assert
        assertThrows(InvalidRequestException.class, () -> {
            rentalApplicationService.createRentalApplication(testApplication);
        });
    }

    @Test
    void approveApplication_WhenAlreadyApproved_ShouldThrowException() {
        // Arrange
        testApplication.setStatus(ApplicationStatus.APPROVED);
        when(rentalApplicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));

        // Act & Assert
        assertThrows(InvalidRequestException.class, () -> {
            rentalApplicationService.approveRentalApplication(1L);
        });
    }

    @Test
    void approveApplication_WhenAlreadyRejected_ShouldThrowException() {
        // Arrange
        testApplication.setStatus(ApplicationStatus.REJECTED);
        when(rentalApplicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));

        // Act & Assert
        assertThrows(InvalidRequestException.class, () -> {
            rentalApplicationService.approveRentalApplication(1L);
        });
    }

    @Test
    void rejectApplication_WhenAlreadyProcessed_ShouldThrowException() {
        // Arrange
        testApplication.setStatus(ApplicationStatus.APPROVED);
        when(rentalApplicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));

        // Act & Assert
        assertThrows(InvalidRequestException.class, () -> {
            rentalApplicationService.rejectRentalApplication(1L);
        });
    }
}