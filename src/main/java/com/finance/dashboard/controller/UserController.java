package com.finance.dashboard.controller;

import com.finance.dashboard.dto.UserDTO;
import com.finance.dashboard.model.User;
import com.finance.dashboard.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // This endpoint is open - no login needed - for creating first admin
    @PostMapping("/setup")
    public ResponseEntity<?> setupAdmin(@Valid @RequestBody UserDTO dto) {
        try {
            User user = userService.createUser(dto);
            return ResponseEntity.ok(Map.of(
                "message", "User created successfully",
                "userId", user.getId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // This endpoint requires ADMIN login
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO dto) {
        try {
            User user = userService.createUser(dto);
            return ResponseEntity.ok(Map.of(
                "message", "User created successfully",
                "userId", user.getId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestParam boolean active) {
        try {
            User user = userService.updateUserStatus(id, active);
            return ResponseEntity.ok(Map.of("message", "Status updated", "active", user.isActive()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}