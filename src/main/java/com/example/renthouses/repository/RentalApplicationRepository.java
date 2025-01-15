package com.example.renthouses.repository;


import com.example.renthouses.entity.RentalApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalApplicationRepository extends JpaRepository<RentalApplication, Long> {
    // Custom query methods if needed
}