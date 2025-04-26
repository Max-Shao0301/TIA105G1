package com.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Test {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String admin = passwordEncoder.encode("11111111");
        System.out.println(admin);

    }
}
