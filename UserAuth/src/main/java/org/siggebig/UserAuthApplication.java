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

            // if db is empty, creates a user that always exists
//            if (!userRepository.existsById(1L)) {
//                User user = new User();
//                user.setId(1L);
//                user.setUsername("sigge");
//                user.setPassword("password");
//                String encodedPasswordUser = passwordEncoder.encode(user.getPassword());
//                user.setPassword(encodedPasswordUser);
//                userRepository.save(user);
//            }

            System.out.println("-------initalized database-------");
        };
    }
}

