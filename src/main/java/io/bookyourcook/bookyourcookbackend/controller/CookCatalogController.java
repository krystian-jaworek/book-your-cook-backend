package io.bookyourcook.bookyourcookbackend.controller;

import io.bookyourcook.bookyourcookbackend.dto.CookProfileResponse;
import io.bookyourcook.bookyourcookbackend.dto.PublicCalendarResponse;
import io.bookyourcook.bookyourcookbackend.model.User;
import io.bookyourcook.bookyourcookbackend.service.CalendarService;
import io.bookyourcook.bookyourcookbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for publicly browsing cook profiles.
 */
@RestController
@RequestMapping("/api/catalog/cooks")
public class CookCatalogController {

    @Autowired
    private UserService userService;

    @Autowired
    private CalendarService calendarService;

    @GetMapping("/search")
    public ResponseEntity<Page<User>> searchCooks(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.searchCooks(query, pageable));
    }

    @GetMapping("/random")
    public ResponseEntity<List<User>> getRandomCooks(@RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userService.getRandomCooks(size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CookProfileResponse> getCookProfile(@PathVariable String id) {
        return ResponseEntity.ok(userService.getCookProfileDetails(id));
    }

    @GetMapping("/{id}/calendar")
    public ResponseEntity<PublicCalendarResponse> getCookCalendar(
            @PathVariable String id,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(calendarService.getPublicCalendarForCook(id, year, month));
    }
}