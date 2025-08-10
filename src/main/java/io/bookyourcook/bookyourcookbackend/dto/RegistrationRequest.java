package io.bookyourcook.bookyourcookbackend.dto;


import io.bookyourcook.bookyourcookbackend.model.Role;
import lombok.Data;

/**
 * Data Transfer Object for user registration requests.
 * This class encapsulates the data sent from the client during registration.
 */
@Data
public class RegistrationRequest {
    private String username; // email
    private String password;
    private String firstName;
    private String lastName;
    private String city;
    private String postalCode;
    private String street;
    private String buildingNumber;
    private String apartmentNumber;
    private String phoneNumber;
    private Role role;
}