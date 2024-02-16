package com.siggebig.demo.controllers;

import com.siggebig.demo.models.LoginDto;
import com.siggebig.demo.models.User;
import com.siggebig.demo.repositorys.UserRepository;
import com.siggebig.demo.services.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
        return  ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> userReg(@RequestBody User userDTO) {
        User user = new User();
        user.setEmail(userDTO.getEmail()); // check if it already exists?
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encodedPassword);
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }


}
