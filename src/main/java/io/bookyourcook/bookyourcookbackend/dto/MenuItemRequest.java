package io.bookyourcook.bookyourcookbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * DTO for creating and updating menu items.
 */
@Data
public class MenuItemRequest {
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Size(max = 500, message = "Description cannot be longer than 500 characters")
    private String description;

    @Positive(message = "Cost must be positive")
    private double cost;

    private String imageUrl;

    private List<String> ingredients;
}