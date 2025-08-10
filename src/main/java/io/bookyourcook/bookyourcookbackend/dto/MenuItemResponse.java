package io.bookyourcook.bookyourcookbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for sending menu item data to the client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemResponse {
    private String id;
    private String name;
    private String description;
    private double cost;
    private String imageUrl;
    private String cookId;
    private List<String> ingredients;
}