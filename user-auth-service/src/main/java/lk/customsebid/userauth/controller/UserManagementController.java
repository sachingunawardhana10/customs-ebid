package lk.customsebid.userauth.controller;

import jakarta.validation.Valid;
import lk.customsebid.userauth.dto.RoleAssignmentRequest;
import lk.customsebid.userauth.dto.UserResponse;
import lk.customsebid.userauth.entity.User;
import lk.customsebid.userauth.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserManagementController {

    private final UserService userService;

    public UserManagementController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> assignRole(
            @PathVariable UUID id,
            @Valid @RequestBody RoleAssignmentRequest request
    ) {
        User user = userService.assignRole(id, request.getRole());

        return ResponseEntity.ok(toUserResponse(user));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getUserProfile(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        User requestedUser = userService.findUserById(id);

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                );

        boolean isOwnProfile = requestedUser.getEmail()
                .equals(authentication.getName());

        if (!isAdmin && !isOwnProfile) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(toUserResponse(requestedUser));
    }

    private UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setStatus(user.getStatus());
        response.setCreatedAt(user.getCreatedAt());
        response.setRoles(
                user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toSet())
        );

        return response;
    }
}