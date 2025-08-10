package io.bookyourcook.bookyourcookbackend.controller;

import io.bookyourcook.bookyourcookbackend.dto.AuthenticationResponse;
import io.bookyourcook.bookyourcookbackend.dto.LoginRequest;
import io.bookyourcook.bookyourcookbackend.dto.RefreshTokenRequest;
import io.bookyourcook.bookyourcookbackend.dto.RegistrationRequest;
import io.bookyourcook.bookyourcookbackend.model.BookingStatus;
import io.bookyourcook.bookyourcookbackend.model.User;
import io.bookyourcook.bookyourcookbackend.repository.UserRepository;
import io.bookyourcook.bookyourcookbackend.service.UserService;
import io.bookyourcook.bookyourcookbackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for handling authentication-related requests, such as registration and login.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository; // Dodajemy repozytorium

    @Autowired
    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest request) {
        try {
            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setPassword(request.getPassword());
            newUser.setFirstName(request.getFirstName());
            newUser.setLastName(request.getLastName());
            newUser.setCity(request.getCity());
            newUser.setPostalCode(request.getPostalCode());
            newUser.setStreet(request.getStreet());
            newUser.setBuildingNumber(request.getBuildingNumber());
            newUser.setApartmentNumber(request.getApartmentNumber());
            newUser.setPhoneNumber(request.getPhoneNumber());
            newUser.setRole(request.getRole());

            userService.registerUser(newUser);

            return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            final User user = userService.getUserProfile(request.getUsername());
            final String accessToken = jwtUtil.generateToken(user);
            final String refreshToken = jwtUtil.generateRefreshToken(user);

            userService.saveUserRefreshToken(user.getUsername(), refreshToken);

            return ResponseEntity.ok(new AuthenticationResponse(accessToken, refreshToken));

        } catch (Exception e) {
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> updateBookingStatus() {
        userService.resetPassword();
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        try {
            String username = jwtUtil.extractUsername(requestRefreshToken);
            User user = userService.getUserProfile(username);

            if (user.getRefreshToken() != null && user.getRefreshToken().equals(requestRefreshToken) && !jwtUtil.isTokenExpired(requestRefreshToken)) {
                String newAccessToken = jwtUtil.generateToken(user);
                return ResponseEntity.ok(new AuthenticationResponse(newAccessToken, requestRefreshToken));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid refresh token");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid refresh token");
        }
    }
}