package lk.customsebid.userauth.controller;

import jakarta.validation.Valid;
import lk.customsebid.userauth.dto.RegisterRequest;
import lk.customsebid.userauth.dto.UserResponse;
import lk.customsebid.userauth.entity.User;
import lk.customsebid.userauth.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        User user = userService.registerUser(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                request.getPhone()
        );

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setStatus(user.getStatus());
        response.setCreatedAt(user.getCreatedAt());

        response.setRoles(
                user.getRoles()
                        .stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toSet())
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}