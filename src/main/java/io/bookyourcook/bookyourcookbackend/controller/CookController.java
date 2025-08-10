package io.bookyourcook.bookyourcookbackend.controller;

import io.bookyourcook.bookyourcookbackend.dto.*;
import io.bookyourcook.bookyourcookbackend.model.BookingStatus;
import io.bookyourcook.bookyourcookbackend.model.User;
import io.bookyourcook.bookyourcookbackend.service.BookingService;
import io.bookyourcook.bookyourcookbackend.service.CalendarService;
import io.bookyourcook.bookyourcookbackend.service.FileStorageService;
import io.bookyourcook.bookyourcookbackend.service.MenuService;
import io.bookyourcook.bookyourcookbackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cook")
public class CookController {

    @Autowired
    private UserService userService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private CalendarService calendarService;
    @Autowired
    private BookingService bookingService;

    // --- Profile Endpoints ---
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User user = userService.getUserProfile(currentPrincipalName);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile/details")
    public ResponseEntity<User> updateProfileDetails(@Valid @RequestBody BioUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User updatedUser = userService.updateCookProfileDetails(currentPrincipalName, request.getBio());
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/profile/picture")
    public ResponseEntity<User> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String filename = fileStorageService.storeProfilePicture(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/uploads/profilePictures/").path(filename).toUriString();
        User updatedUser = userService.updateProfilePicture(username, fileDownloadUri, filename);
        return ResponseEntity.ok(updatedUser);
    }

    // --- Cook's Own Menu Management Endpoints ---
    @GetMapping("/my-menu")
    public ResponseEntity<List<MenuItemResponse>> getMyMenu() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.getUserProfile(username);
        return ResponseEntity.ok(menuService.getMenuForCook(user.getId()));
    }

    @PostMapping("/my-menu")
    public ResponseEntity<MenuItemResponse> addMenuItem(@Valid @RequestBody MenuItemRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(menuService.addMenuItem(username, request));
    }

    @PutMapping("/my-menu/{itemId}")
    public ResponseEntity<MenuItemResponse> updateMenuItem(@PathVariable String itemId, @Valid @RequestBody MenuItemRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(menuService.updateMenuItem(username, itemId, request));
    }

    @DeleteMapping("/my-menu/{itemId}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable String itemId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        menuService.deleteMenuItem(username, itemId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/menu/image")
    public ResponseEntity<?> uploadMenuImage(@RequestParam("file") MultipartFile file) {
        String filename = fileStorageService.storeMenuPicture(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/uploads/menuPictures/").path(filename).toUriString();
        return ResponseEntity.ok(Map.of("url", fileDownloadUri));
    }

    // --- Calendar Endpoints ---
    @GetMapping("/calendar")
    public ResponseEntity<CalendarMonthResponse> getCalendarData(@RequestParam int year, @RequestParam int month) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(calendarService.getCalendarDataForMonth(username, year, month));
    }

    @PostMapping("/calendar/availability")
    public ResponseEntity<Void> setAvailability(@RequestBody Map<String, String> payload) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        LocalDate date = LocalDate.parse(payload.get("date"));
        String from = payload.get("from");
        String to = payload.get("to");

        if (from.isBlank() || to.isBlank()) {
            calendarService.removeAvailability(username, date);
        } else {
            calendarService.setAvailability(username, date, from, to);
        }
        return ResponseEntity.ok().build();
    }

    // --- Booking Management Endpoints ---
    @GetMapping("/bookings/{id}")
    public ResponseEntity<CookBookingDetailResponse> getBookingDetails(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(bookingService.getCookBookingDetails(id, username));
    }

    @PutMapping("/bookings/{id}/status")
    public ResponseEntity<?> updateBookingStatus(@PathVariable String id, @RequestBody Map<String, String> payload) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        BookingStatus newStatus = BookingStatus.valueOf(payload.get("status").toUpperCase());
        bookingService.updateBookingStatus(id, username, newStatus);
        return ResponseEntity.ok().build();
    }


}
