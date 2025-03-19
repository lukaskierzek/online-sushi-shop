package com.sushiShop.onlineSushiShop;

import com.sushiShop.onlineSushiShop.model.User;
import com.sushiShop.onlineSushiShop.repository.UserRepository;
import com.sushiShop.onlineSushiShop.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class UserTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    void getAllUser_shouldReturnAllUser() {
        List<User> allUsers = userService.getAllUser();

        assertEquals(3, allUsers.size());

        assertEquals(true, allUsers.get(0).getUserIsActive());
        assertEquals(true, allUsers.get(1).getUserIsActive());
        assertEquals(false, allUsers.get(2).getUserIsActive());

        assertEquals("ADMIN", allUsers.get(0).getUserRole().toString());
        assertEquals("USER", allUsers.get(1).getUserRole().toString());
        assertEquals("USER", allUsers.get(2).getUserRole().toString());

        assertEquals("janek@example.com", allUsers.get(0).getUserEmail());
        assertEquals("maciek@example.com", allUsers.get(1).getUserEmail());
        assertEquals("andrzej@example.com", allUsers.get(2).getUserEmail());

        assertEquals("janek", allUsers.get(0).getUserName());
        assertEquals("maciek", allUsers.get(1).getUserName());
        assertEquals("andrzej", allUsers.get(2).getUserName());
    }
}
