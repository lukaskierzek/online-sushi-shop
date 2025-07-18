package com.sushiShop.onlineSushiShop.service;

import com.sushiShop.onlineSushiShop.model.User;
import com.sushiShop.onlineSushiShop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUser() {
        List<User> allUser = userRepository.findAll();
        return allUser;
    }
}

//TODO: Check later
