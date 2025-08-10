package io.bookyourcook.bookyourcookbackend.controller;

import io.bookyourcook.bookyourcookbackend.dto.BookingDetailResponse;
import io.bookyourcook.bookyourcookbackend.dto.BookingRequest;
import io.bookyourcook.bookyourcookbackend.dto.CustomerCalendarResponse;
import io.bookyourcook.bookyourcookbackend.model.Booking;
import io.bookyourcook.bookyourcookbackend.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/bookings")
    public ResponseEntity<Booking> createBooking(@RequestBody BookingRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Booking newBooking = bookingService.createBooking(username, request);
        return new ResponseEntity<>(newBooking, HttpStatus.CREATED);
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<CustomerCalendarResponse> getMyBookings(@RequestParam int year, @RequestParam int month) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(bookingService.getCustomerCalendar(username, year, month));
    }

    @GetMapping("/bookings/{id}")
    public ResponseEntity<BookingDetailResponse> getBookingDetails(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(bookingService.getBookingDetails(id, username));
    }

    @PutMapping("/bookings/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(bookingService.cancelBooking(id, username));
    }
}
