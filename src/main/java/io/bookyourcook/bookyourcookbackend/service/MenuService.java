package io.bookyourcook.bookyourcookbackend.service;

import io.bookyourcook.bookyourcookbackend.dto.MealDetailResponse;
import io.bookyourcook.bookyourcookbackend.dto.MenuItemRequest;
import io.bookyourcook.bookyourcookbackend.dto.MenuItemResponse;
import io.bookyourcook.bookyourcookbackend.exception.ResourceNotFoundException;
import io.bookyourcook.bookyourcookbackend.model.MenuItem;
import io.bookyourcook.bookyourcookbackend.model.User;
import io.bookyourcook.bookyourcookbackend.repository.MenuItemRepository;
import io.bookyourcook.bookyourcookbackend.repository.UserRepository;
import io.bookyourcook.bookyourcookbackend.util.SearchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private MongoTemplate mongoTemplate;

    private MenuItemResponse toResponse(MenuItem menuItem) {
        return new MenuItemResponse(menuItem.getId(), menuItem.getName(), menuItem.getDescription(), menuItem.getCost(), menuItem.getImageUrl(), menuItem.getCookId(), menuItem.getIngredients());
    }

    public Page<MenuItem> searchMenuItems(String query, Pageable pageable) {
        if (query != null && !query.isBlank()) {
            String normalizedQuery = SearchUtil.normalize(query);
            return menuItemRepository.findByPartialMatch(normalizedQuery, pageable);
        } else {
            return menuItemRepository.findAll(pageable);
        }
    }

    public List<MenuItemResponse> getRandomMenuItems(int size) {
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.sample(size));
        List<MenuItem> results = mongoTemplate.aggregate(aggregation, "menu_items", MenuItem.class).getMappedResults();
        return results.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<MenuItemResponse> getMenuForCook(String cookId) {
        return menuItemRepository.findByCookId(cookId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public MenuItemResponse addMenuItem(String username, MenuItemRequest request) {
        User cook = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Cook not found"));
        MenuItem newItem = new MenuItem(request.getName(), request.getDescription(), request.getCost(), request.getImageUrl(), cook.getId(), request.getIngredients());
        MenuItem savedItem = menuItemRepository.save(newItem);
        return toResponse(savedItem);
    }

    public MenuItemResponse updateMenuItem(String username, String itemId, MenuItemRequest request) {
        User cook = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Cook not found"));
        MenuItem itemToUpdate = menuItemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("Menu item not found with id: " + itemId));
        if (!itemToUpdate.getCookId().equals(cook.getId())) {
            throw new SecurityException("User does not have permission to update this menu item.");
        }
        if (request.getImageUrl() != null && !request.getImageUrl().equals(itemToUpdate.getImageUrl())) {
            String oldImageUrl = itemToUpdate.getImageUrl();
            if (oldImageUrl != null && !oldImageUrl.isBlank()) {
                String oldFilename = oldImageUrl.substring(oldImageUrl.lastIndexOf("/") + 1);
                fileStorageService.deleteMenuPicture(oldFilename);
            }
        }
        itemToUpdate.setName(request.getName());
        itemToUpdate.setDescription(request.getDescription());
        itemToUpdate.setCost(request.getCost());
        itemToUpdate.setImageUrl(request.getImageUrl());
        itemToUpdate.setIngredients(request.getIngredients());

        itemToUpdate.normalizeFields();

        MenuItem savedItem = menuItemRepository.save(itemToUpdate);
        return toResponse(savedItem);
    }

    public void deleteMenuItem(String username, String itemId) {
        User cook = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Cook not found"));
        MenuItem itemToDelete = menuItemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("Menu item not found with id: " + itemId));
        if (!itemToDelete.getCookId().equals(cook.getId())) {
            throw new SecurityException("User does not have permission to delete this menu item.");
        }
        String imageUrl = itemToDelete.getImageUrl();
        if (imageUrl != null && !imageUrl.isBlank()) {
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            fileStorageService.deleteMenuPicture(filename);
        }
        menuItemRepository.deleteById(itemId);
    }

    public MealDetailResponse getMealDetails(String mealId) {
        MenuItem menuItem = menuItemRepository.findById(mealId)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found with id: " + mealId));

        User cook = userRepository.findById(menuItem.getCookId())
                .orElseThrow(() -> new ResourceNotFoundException("Cook not found with id: " + menuItem.getCookId()));

        MealDetailResponse.CookInfo cookInfo = new MealDetailResponse.CookInfo(
                cook.getId(),
                cook.getFirstName(),
                cook.getLastName(),
                cook.getProfileImageUrl(),
                cook.getBio()
        );

        return new MealDetailResponse(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getIngredients(),
                menuItem.getImageUrl(),
                menuItem.getCost(), // Populate the cost field
                cookInfo
        );
    }
}
