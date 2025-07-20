package com.sushiShop.onlineSushiShop.service;

import com.sushiShop.onlineSushiShop.component.JwtUtil;
import com.sushiShop.onlineSushiShop.enums.Role;
import com.sushiShop.onlineSushiShop.model.User;
import com.sushiShop.onlineSushiShop.model.UserBuilder;
import com.sushiShop.onlineSushiShop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String login(String username, String password) {
        Optional<User> user = userRepository.findByUserName(username);
        try {
            if (user.isPresent() && passwordEncoder.matches(password, user.get().getUserPassword())) {
                String generatedToken = jwtUtil.generateToken(username, user.get().getUserRole());
                return generatedToken;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("Invalid credentials");
    }

    public void register(String username, String password, String email, Role userRole) {
        if (userRepository.findByUserName(username).isPresent())
            throw new RuntimeException("User already exists");

        User newUser = new UserBuilder()
            .setUserId(null)
            .setUserName(username)
            .setUserEmail(email)
            .setUserPassword(passwordEncoder.encode(password))
            .setUserRole(userRole)
            .setUserIsActive(true)
            .setUserCreatedAt(null)
            .setUserUpdatedAt(null)
            .createUser();

        userRepository.save(newUser);
    }
}

//TODO: Check later
