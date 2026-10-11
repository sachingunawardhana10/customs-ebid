
package lk.customsebid.userauth.service;

import lk.customsebid.userauth.entity.Role;
import lk.customsebid.userauth.entity.User;
import lk.customsebid.userauth.repository.RoleRepository;
import lk.customsebid.userauth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private Role bidderRole;

    @BeforeEach
    void setUp() {
        bidderRole = new Role();
        bidderRole.setName("BIDDER");
    }

    @Test
    void registerUserShouldAssignOnlyBidderRole() {
        when(userRepository.existsByEmail("bidder@example.com"))
                .thenReturn(false);

        when(roleRepository.findByName("BIDDER"))
                .thenReturn(Optional.of(bidderRole));

        when(passwordEncoder.encode("Password123!"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.registerUser(
                "Test",
                "Bidder",
                "bidder@example.com",
                "Password123!",
                "0771234567"
        );

        assertEquals("bidder@example.com", savedUser.getEmail());
        assertEquals("encoded-password", savedUser.getPasswordHash());
        assertEquals(1, savedUser.getRoles().size());
        assertTrue(savedUser.getRoles().stream()
                .anyMatch(role -> "BIDDER".equals(role.getName())));

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("Password123!");
    }

    @Test
    void registerUserShouldRejectDuplicateEmail() {
        when(userRepository.existsByEmail("bidder@example.com"))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser(
                        "Test",
                        "Bidder",
                        "bidder@example.com",
                        "Password123!",
                        "0771234567"
                )
        );

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(roleRepository, passwordEncoder);
    }

    @Test
    void registerUserShouldFailWhenBidderRoleIsMissing() {
        when(userRepository.existsByEmail("bidder@example.com"))
                .thenReturn(false);

        when(roleRepository.findByName("BIDDER"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () ->
                userService.registerUser(
                        "Test",
                        "Bidder",
                        "bidder@example.com",
                        "Password123!",
                        "0771234567"
                )
        );

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder);
    }
}
