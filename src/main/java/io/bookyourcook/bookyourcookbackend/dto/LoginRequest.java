package io.bookyourcook.bookyourcookbackend.dto;

import lombok.Data;

/**
 * Data Transfer Object for user login requests.
 */
@Data
public class LoginRequest {
    private String username;
    private String password;
}