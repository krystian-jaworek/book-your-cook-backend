package io.bookyourcook.bookyourcookbackend.controller;

import io.bookyourcook.bookyourcookbackend.dto.MealDetailResponse;
import io.bookyourcook.bookyourcookbackend.dto.MenuItemResponse;
import io.bookyourcook.bookyourcookbackend.model.MenuItem;
import io.bookyourcook.bookyourcookbackend.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Publicly accessible controller for browsing menu items.
 */
@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/search")
    public ResponseEntity<Page<MenuItem>> searchMenuItems(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(menuService.searchMenuItems(query, pageable));
    }

    @GetMapping("/random")
    public ResponseEntity<List<MenuItemResponse>> getRandomMenuItems(@RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(menuService.getRandomMenuItems(size));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<MealDetailResponse> getMealDetails(@PathVariable String id) {
        return ResponseEntity.ok(menuService.getMealDetails(id));
    }

}