package org.siggebig.controllers;



import org.siggebig.exceptions.UnauthorizedException;
import org.siggebig.models.Comment;
import org.siggebig.models.Post;
import org.siggebig.models.User;
import org.siggebig.exceptions.UserNotFoundException;
import org.siggebig.repositorys.UserRepository;
import org.siggebig.services.AccessService;
import org.siggebig.services.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AccessService accessService;


    // should everything be included? Like private posts?...
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // should everything be included? Like private posts?...
    @GetMapping("{userId}")
    public ResponseEntity<User> getUserById(@PathVariable("userId") Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        return ResponseEntity.ok(user);
    }

    @GetMapping("username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable("username") String username) {
        User user = userRepository.findByUsername(username);
        return ResponseEntity.ok(user);
    }


    @PutMapping("/update/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable("userId") long userId, @RequestBody User user, @RequestHeader("Authorization") String token) {
        User _user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Not found User with id = " + userId));

        accessService.verifyUserToken(_user, token);
        User apiCaller = jwtService.getUserFromToken(token);

        _user.setUsername(user.getUsername()); // username iis unique so this will fail if its already taken
        _user.setDescription(user.getDescription());

        userRepository.save(_user);
        if (apiCaller.equals(_user)) {
            String newToken = jwtService.getNewTokenFromUsername(_user.getUsername());
            Map<String, Object> response = new HashMap<>();
            response.put("user", _user);
            response.put("token", newToken);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(_user, HttpStatus.OK);
        }
    }

    @PostMapping("/follow/{followId}")
    public ResponseEntity<?> followUser(@PathVariable("followId") long followId, @RequestHeader("Authorization") String token) {
        try {
            User user = jwtService.getUserFromToken(token);
            User followUser = userRepository.findById(followId)
                    .orElseThrow(() -> new UserNotFoundException("Not found User with id = " + followId));

            if (followUser.getUsername().equals(user.getUsername())) {
                throw new UnauthorizedException("Cant follow urself");
            }


            if (user.getFollowingsIds().contains(followId)) {
                throw new UnauthorizedException("Cant follow twice");
            }

            user.addFollowing(followUser.getId());
            followUser.addFollower(user.getId());

            userRepository.save(user);
            userRepository.save(followUser);


            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to follow user: " + e.getMessage());
        }
    }

    @DeleteMapping("/unfollow/{followId}")
    public ResponseEntity<?> unfollowUser(@PathVariable("followId") long followId, @RequestHeader("Authorization") String token) {
        try {
            User user = jwtService.getUserFromToken(token);
            User followUser = userRepository.findById(followId)
                    .orElseThrow(() -> new UserNotFoundException("Not found User with id = " + followId));

            if (followUser.getUsername().equals(user.getUsername())) {
                throw new UnauthorizedException("Cant unfollow urself");
            }

            user.removeFollowing(followUser.getId());
            followUser.removeFollower(user.getId());
            userRepository.save(user);
            userRepository.save(followUser);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to unfollow user: " + e.getMessage());
        }
    }

    @GetMapping("/my-followings")
    public ResponseEntity<List<Long>> getMyFollowings(@RequestHeader("Authorization") String token) {
        User user = jwtService.getUserFromToken(token);
        return ResponseEntity.ok(new ArrayList<>(user.getFollowingsIds()));
    }

    @PostMapping("/convert-ids")
    public ResponseEntity<List<User>> convertUserIdsToUsers(@RequestBody List<Long> userIds) {
        List<User> users = userRepository.findByIdIn(userIds);
        return ResponseEntity.ok().body(users);
    }

    @DeleteMapping("/remove-post-interactions")
    public ResponseEntity<?> removePostInteractionsFromAllUsers(@RequestBody Post post, @RequestHeader("Authorization") String token) {

        User caller = jwtService.getUserFromToken(token);
        accessService.verifyUserAccessToPost(caller, post);

        for (long id : post.getLikes()) {
            User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Not found User with id = " + id));
            user.removeLike(post.getId());
            userRepository.save(user);
        }

        for (long id : post.getStars()) {
            User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Not found User with id = " + id));
            user.removeStar(post.getId());
            userRepository.save(user);
        }

        for ( Comment comment : post.getComments()) {
            User user = userRepository.findById(comment.getUserId()).orElseThrow(() -> new UserNotFoundException("Not found User with id = " + comment.getUserId()));
            user.removeComment(comment.getId());
            userRepository.save(user);
        }

        User owner = userRepository.findById(post.getUserId()).orElseThrow(() -> new UserNotFoundException("Not found User with id = " + post.getUserId()));
        owner.removePost(post.getId());
        userRepository.save(owner);
        return ResponseEntity.ok().build();
    }

}
