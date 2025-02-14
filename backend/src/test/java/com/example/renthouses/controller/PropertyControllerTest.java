package com.example.renthouses.controller;

import com.example.renthouses.entity.Property;
import com.example.renthouses.entity.PropertyStatus;
import com.example.renthouses.entity.User;
import com.example.renthouses.entity.Role;
import com.example.renthouses.service.PropertyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PropertyService propertyService;

    private Property testProperty;
    private Map<String, Object> testPropertyMap;

    @BeforeEach
    void setUp() {
        // Setup test property
        testProperty = new Property();
        testProperty.setId(1L);
        testProperty.setTitle("Test Property");
        testProperty.setDescription("Test Description");
        testProperty.setAddress("Test Address");
        testProperty.setPrice(1000.0);
        testProperty.setStatus(PropertyStatus.PENDING_APPROVAL);

        // Create a map representation for JSON requests
        testPropertyMap = new HashMap<>();
        testPropertyMap.put("id", 1L);
        testPropertyMap.put("title", "Test Property");
        testPropertyMap.put("description", "Test Description");
        testPropertyMap.put("address", "Test Address");
        testPropertyMap.put("price", 1000.0);
        testPropertyMap.put("status", "PENDING_APPROVAL");
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void createProperty_WithOwnerRole_ShouldSucceed() throws Exception {
        when(propertyService.createProperty(any(Property.class))).thenReturn(testProperty);

        mockMvc.perform(post("/api/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPropertyMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(testProperty.getTitle()))
                .andExpect(jsonPath("$.status").value(PropertyStatus.PENDING_APPROVAL.toString()));
    }

    @Test
    @WithMockUser(roles = "RENTER")
    void createProperty_WithRenterRole_ShouldFail() throws Exception {
        mockMvc.perform(post("/api/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPropertyMap)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void getPropertyById_ShouldReturnProperty() throws Exception {
        when(propertyService.getPropertyById(1L)).thenReturn(testProperty);

        mockMvc.perform(get("/api/properties/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testProperty.getId()))
                .andExpect(jsonPath("$.title").value(testProperty.getTitle()));
    }

    @Test
    @WithMockUser
    void getAllProperties_ShouldReturnList() throws Exception {
        when(propertyService.getAllProperties()).thenReturn(Arrays.asList(testProperty));

        mockMvc.perform(get("/api/properties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testProperty.getId()))
                .andExpect(jsonPath("$[0].title").value(testProperty.getTitle()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void approveProperty_WithAdminRole_ShouldSucceed() throws Exception {
        mockMvc.perform(post("/api/properties/1/approve"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void approveProperty_WithOwnerRole_ShouldFail() throws Exception {
        mockMvc.perform(post("/api/properties/1/approve"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void updateProperty_WithOwnerRole_ShouldSucceed() throws Exception {
        when(propertyService.updateProperty(any(Property.class))).thenReturn(testProperty);

        mockMvc.perform(put("/api/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPropertyMap)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void deleteProperty_WithOwnerRole_ShouldSucceed() throws Exception {
        mockMvc.perform(delete("/api/properties/1"))
                .andExpect(status().isOk());
    }
}