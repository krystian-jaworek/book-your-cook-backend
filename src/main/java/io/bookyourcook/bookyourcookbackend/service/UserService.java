package io.bookyourcook.bookyourcookbackend.service;

import io.bookyourcook.bookyourcookbackend.dto.CookProfileResponse;
import io.bookyourcook.bookyourcookbackend.dto.MenuItemResponse;
import io.bookyourcook.bookyourcookbackend.exception.ResourceNotFoundException;
import io.bookyourcook.bookyourcookbackend.model.Role;
import io.bookyourcook.bookyourcookbackend.model.User;
import io.bookyourcook.bookyourcookbackend.repository.UserRepository;
import io.bookyourcook.bookyourcookbackend.util.SearchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private MongoTemplate mongoTemplate;

    private final MenuService menuService;

    @Autowired
    public UserService(MenuService menuService) {
        this.menuService = menuService;
    }

    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username '" + user.getUsername() + "' is already taken.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.normalizeFields();
        return userRepository.save(user);
    }

    public Page<User> searchCooks(String query, Pageable pageable) {
        if (query != null && !query.isBlank()) {
            String normalizedQuery = SearchUtil.normalize(query);
            return userRepository.findCooksByRoleAndPartialMatch(Role.COOK, normalizedQuery, pageable);
        } else {
            return userRepository.findAllByRole(Role.COOK, pageable);
        }
    }

    public List<User> getRandomCooks(int size) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("role").is(Role.COOK)),
                Aggregation.sample(size)
        );
        return mongoTemplate.aggregate(aggregation, "users", User.class).getMappedResults();
    }

    public User getUserProfile(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public CookProfileResponse getCookProfileDetails(String cookId) {
        User cook = userRepository.findById(cookId)
                .orElseThrow(() -> new ResourceNotFoundException("Cook not found with id: " + cookId));

        List<MenuItemResponse> menu = menuService.getMenuForCook(cook.getId());

        return new CookProfileResponse(
                cook.getId(),
                cook.getFirstName(),
                cook.getLastName(),
                cook.getProfileImageUrl(),
                cook.getBio(),
                cook.getSessionPrice(),
                cook.getShoppingCost(),
                menu
        );
    }

    public User updateCookProfileDetails(String username, String bio) {
        User user = getUserProfile(username);
        user.setBio(bio);
        user.normalizeFields();
        return userRepository.save(user);
    }

    public User updateProfilePicture(String username, String newImageUrl, String newFilename) {
        User user = getUserProfile(username);
        String oldImageUrl = user.getProfileImageUrl();
        if (oldImageUrl != null && !oldImageUrl.isBlank()) {
            String oldFilename = oldImageUrl.substring(oldImageUrl.lastIndexOf("/") + 1);
            fileStorageService.deleteProfilePicture(oldFilename);
        }
        user.setProfileImageUrl(newImageUrl);
        return userRepository.save(user);
    }

    public void saveUserRefreshToken(String username, String refreshToken) {
        User user = getUserProfile(username);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
    }

    public void resetPassword() {
        userRepository.findAll().forEach(user -> {
            String newPassword = passwordEncoder.encode("test");
            user.setPassword(newPassword);
            userRepository.save(user);
        });
    }
}
