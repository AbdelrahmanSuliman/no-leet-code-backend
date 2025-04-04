package com.example.noleetcode.config;

import com.example.noleetcode.models.User;
import com.example.noleetcode.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetails implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch user from the repository
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Create a UserDetails object and return it
        UserBuilder userBuilder = org.springframework.security.core.userdetails.User.withUsername(user.getUsername());
        userBuilder.password(user.getPassword()); // Add the encrypted password here

        String role = user.getRole().name(); // Convert Enum to String (e.g., "USER" or "ADMIN")
        userBuilder.roles(role); // Assign the role as a string

        return userBuilder.build();
    }
}
