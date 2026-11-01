package com.AL_Tayep.BookStore.Security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.AL_Tayep.BookStore.Repositories.UserRepository;
import com.AL_Tayep.BookStore.models.User;
import com.AL_Tayep.BookStore.models.Enums.Role;
import com.AL_Tayep.BookStore.models.Enums.UserStatus;



@Component
public class AdminInitializer {

    @Value("${app.admin.email:admin@bookstore.com}")
    private String adminEmail;

    @Value("${app.admin.password:DefaultSecurePassword123!}")
    private String adminPassword;

    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                User admin = new User();
                admin.setName("SuperAdmin");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword)); // 🔒 Always encode!
                admin.setRole(Role.ADMIN);
                admin.setStatus(UserStatus.Active);
                
                userRepository.save(admin);
                System.out.println("Default Admin account seeded into database successfully.");
            }
        };
    }
}