package io.bookyourcook.bookyourcookbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealDetailResponse {
    private String id;
    private String name;
    private String description;
    private List<String> ingredients;
    private String imageUrl;
    private double cost;
    private CookInfo cook;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CookInfo {
        private String id;
        private String firstName;
        private String lastName;
        private String profileImageUrl;
        private String bio;
    }
}
