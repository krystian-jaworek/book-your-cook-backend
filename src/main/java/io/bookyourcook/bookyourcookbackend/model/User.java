package io.bookyourcook.bookyourcookbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import io.bookyourcook.bookyourcookbackend.util.SearchUtil;

@Data
@NoArgsConstructor
@Document(collection = "users")
@CompoundIndex(def = "{'firstNameNormalized': 'text', 'lastNameNormalized': 'text', 'bioNormalized': 'text'}")
public class User {

    @Id
    private String id;
    @Indexed(unique = true)
    private String username;
    @JsonIgnore
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
    private String profileImageUrl;
    @Size(max = 1000)
    private String bio;

    private double sessionPrice;
    private double shoppingCost;

    @JsonIgnore
    private String refreshToken;

    @JsonIgnore
    private String firstNameNormalized;
    @JsonIgnore
    private String lastNameNormalized;
    @JsonIgnore
    private String bioNormalized;

    public User(String username, String password, String firstName, String lastName, Role role) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.sessionPrice = 150.0; // Default price for new cooks
        this.shoppingCost = 30.0; // Default shopping cost for new cooks
        normalizeFields();
    }

    public void normalizeFields() {
        this.firstNameNormalized = SearchUtil.normalize(this.firstName);
        this.lastNameNormalized = SearchUtil.normalize(this.lastName);
        this.bioNormalized = SearchUtil.normalize(this.bio);
    }
}
