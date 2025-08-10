package io.bookyourcook.bookyourcookbackend.dto;


import lombok.Data;

/**
 * Data Transfer Object for cook profile update requests.
 */
@Data
public class ProfileUpdateRequest {
    private String bio;
    private String profileImageUrl;
}