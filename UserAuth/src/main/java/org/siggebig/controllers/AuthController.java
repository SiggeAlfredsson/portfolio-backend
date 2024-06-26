package org.siggebig.controllers;

import org.siggebig.models.LoginDto;
import org.siggebig.models.User;
import org.siggebig.repositorys.UserRepository;
import org.siggebig.services.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody LoginDto loginDto) {
        String token = jwtService.getToken(loginDto); // returns http.UNAUTH if auth fails
        User user = jwtService.getUserFromToken(token);
        return ResponseEntity.ok(Map.of("token", token, "user", user));
    }

    @PostMapping("/register")
    public ResponseEntity<?> userReg(@RequestBody User userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername()); // check if it already exists?
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encodedPassword);
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PostMapping("/token")
    public ResponseEntity<?> validateToken(@RequestBody String token) {
        User user = jwtService.getUserFromToken(token);
        return ResponseEntity.ok(user);
    }


}
