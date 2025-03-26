package com.sushiShop.onlineSushiShop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class OnlineSushiShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineSushiShopApplication.class, args);

//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        var rawPassowrd = "admin123";
//        var encodePadd = encoder.encode(rawPassowrd);
//        System.out.println("tetetete " + encodePadd);
    }

}

//TODO: Check later
