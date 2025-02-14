package com.example.renthouses.repository;

import com.example.renthouses.entity.Property;
import com.example.renthouses.entity.PropertyStatus;
import com.example.renthouses.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByStatus(PropertyStatus status);
    List<Property> findByOwner(User owner);
    List<Property> findByTitleContaining(String title);
    List<Property> findByAddressContaining(String address);
}