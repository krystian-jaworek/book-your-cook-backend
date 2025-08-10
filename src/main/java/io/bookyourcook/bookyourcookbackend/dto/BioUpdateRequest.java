package io.bookyourcook.bookyourcookbackend.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object for updating a user's bio.
 */
@Data
public class BioUpdateRequest {

    @Size(max = 1000, message = "Bio cannot be longer than 1000 characters.")
    private String bio;
}