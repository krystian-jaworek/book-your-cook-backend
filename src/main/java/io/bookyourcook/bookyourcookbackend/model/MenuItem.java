package io.bookyourcook.bookyourcookbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.stream.Collectors;

import io.bookyourcook.bookyourcookbackend.util.SearchUtil;

@Data
@NoArgsConstructor
@Document(collection = "menu_items")
@CompoundIndex(def = "{'nameNormalized': 'text', 'descriptionNormalized': 'text', 'ingredientsNormalized': 'text'}")
public class MenuItem {

    @Id
    private String id;
    private String name;
    @Size(max = 500, message = "Description cannot be longer than 500 characters.")
    private String description;
    private double cost;
    private String imageUrl;
    private String cookId;
    private List<String> ingredients;

    @JsonIgnore
    private String nameNormalized;
    @JsonIgnore
    private String descriptionNormalized;
    @JsonIgnore
    private List<String> ingredientsNormalized;

    public MenuItem(String name, String description, double cost, String imageUrl, String cookId, List<String> ingredients) {
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.imageUrl = imageUrl;
        this.cookId = cookId;
        this.ingredients = ingredients;
        normalizeFields();
    }

    public void normalizeFields() {
        this.nameNormalized = SearchUtil.normalize(this.name);
        this.descriptionNormalized = SearchUtil.normalize(this.description);
        if (this.ingredients != null) {
            this.ingredientsNormalized = this.ingredients.stream()
                    .map(SearchUtil::normalize)
                    .collect(Collectors.toList());
        }
    }
}
