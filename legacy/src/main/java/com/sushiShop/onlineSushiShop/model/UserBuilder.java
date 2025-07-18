package com.sushiShop.onlineSushiShop.model;

import com.sushiShop.onlineSushiShop.enums.Role;

import java.time.LocalDateTime;

public class UserBuilder {
    private Long userId = null;
    private String userName;
    private String userEmail;
    private String userPassword;
    private Role userRole;
    private Boolean userIsActive;
    private LocalDateTime userCreatedAt = null;
    private LocalDateTime userUpdatedAt = null;

    public UserBuilder setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public UserBuilder setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public UserBuilder setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }

    public UserBuilder setUserPassword(String userPassword) {
        this.userPassword = userPassword;
        return this;
    }

    public UserBuilder setUserRole(Role userRole) {
        this.userRole = userRole;
        return this;
    }

    public UserBuilder setUserIsActive(Boolean userIsActive) {
        this.userIsActive = userIsActive;
        return this;
    }

    public UserBuilder setUserCreatedAt(LocalDateTime userCreatedAt) {
        this.userCreatedAt = userCreatedAt;
        return this;
    }

    public UserBuilder setUserUpdatedAt(LocalDateTime userUpdatedAt) {
        this.userUpdatedAt = userUpdatedAt;
        return this;
    }

    public User createUser() {
        return new User(userId, userName, userEmail, userPassword, userRole, userIsActive, userCreatedAt, userUpdatedAt);
    }
}
