package org.siggebig.controllers;


import org.siggebig.exceptions.UserNotFoundException;
import org.siggebig.models.User;
import org.siggebig.repositorys.UserRepository;
import org.siggebig.services.AccessService;
import org.siggebig.services.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AccessService accessService;


    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("{userId}")
    public ResponseEntity<User> getUserById(@PathVariable("userId") Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        return ResponseEntity.ok(user);
    }


    @PutMapping("/update/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable("userId") long userId, @RequestBody User user, @RequestHeader("Authorization") String token) {
        User _user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Not found Driver with id = " + userId));

        accessService.verifyUserToken(_user, token);

        //now user can only update picture and username ---- password should have its own call i think+

        _user.setUsername(user.getUsername());
        _user.setPictureId(user.getPictureId());

        userRepository.save(_user);

        return new ResponseEntity<>(_user, HttpStatus.OK);
    }


}
