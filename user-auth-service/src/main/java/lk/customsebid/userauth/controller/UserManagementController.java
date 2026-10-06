package lk.customsebid.userauth.controller;

import jakarta.validation.Valid;
import lk.customsebid.userauth.dto.RoleAssignmentRequest;
import lk.customsebid.userauth.dto.UserResponse;
import lk.customsebid.userauth.entity.User;
import lk.customsebid.userauth.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
            @PathVariable java.util.UUID id,
            @Valid @RequestBody RoleAssignmentRequest request
    ) {
        User user = userService.assignRole(id, request.getRole());

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

        return ResponseEntity.ok(response);
    }
}
