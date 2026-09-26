package lk.customsebid.userauth.service;

import lk.customsebid.userauth.entity.Role;
import lk.customsebid.userauth.entity.User;
import lk.customsebid.userauth.entity.UserStatus;
import lk.customsebid.userauth.repository.RoleRepository;
import lk.customsebid.userauth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(
            String firstName,
            String lastName,
            String email,
            String password,
            String phone
    ) {

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already registered");
        }

        Role bidderRole = roleRepository.findByName("BIDDER")
                .orElseThrow(() ->
                        new IllegalStateException("BIDDER role not found")
                );

        User user = new User();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setStatus(UserStatus.ACTIVE);
        user.getRoles().add(bidderRole);

        return userRepository.save(user);
    }
}