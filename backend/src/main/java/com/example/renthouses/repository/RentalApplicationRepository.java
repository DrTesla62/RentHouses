package com.example.renthouses.repository;


import com.example.renthouses.entity.ApplicationStatus;
import com.example.renthouses.entity.Property;
import com.example.renthouses.entity.RentalApplication;
import com.example.renthouses.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalApplicationRepository extends JpaRepository<RentalApplication, Long> {
    List<RentalApplication> findByRenter(User renter);
    List<RentalApplication> findByProperty(Property property);
    List<RentalApplication> findByStatus(ApplicationStatus status);
    List<RentalApplication> findByPropertyOwnerId(Long ownerId);
    @Query("SELECT ra FROM RentalApplication ra WHERE ra.property.owner.id = :ownerId")
    List<RentalApplication> findAllByOwnerId(@Param("ownerId") Long ownerId);
}