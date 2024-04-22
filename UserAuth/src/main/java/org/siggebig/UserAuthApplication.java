package org.siggebig;

import org.siggebig.models.User;
import org.siggebig.repositorys.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class UserAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserAuthApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        return args -> {
            // Check if there are already 20 users, if not, create them
            if (userRepository.count() < 20) {
                for (int i = 1; i <= 20; i++) {
                    User user = new User();
                    user.setUsername("user" + i); // Unique username for each user
                    user.setPassword(passwordEncoder.encode("password")); // Encode the password
                    userRepository.save(user); // Save the user to the repository
                }
            }
            System.out.println("-------Initialized database with 20 users-------");
        };
    }
}
