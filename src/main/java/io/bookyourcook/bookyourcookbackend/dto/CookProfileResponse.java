package io.bookyourcook.bookyourcookbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CookProfileResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String profileImageUrl;
    private String bio;
    private double sessionPrice;
    private double shoppingCost;
    private List<MenuItemResponse> menu;
}
