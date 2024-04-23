package com.siggebig.controllers;

import com.siggebig.models.Post;
import com.siggebig.repositorys.PostRepository;
import com.siggebig.services.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JWTService jwtService;

    //get post for infinity scroll || maybe use "nexttoken" ?
    @GetMapping
    public ResponseEntity<?> getPostsForScroll(@RequestParam(required = false) String nextToken) {
        // Implement logic for infinite scrolling, possibly using a pagination token or simple offset
        return ResponseEntity.ok().body(postRepository.findAll()); // Simplified example
    }

    //get post from id ---- if post is private -> jwt
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        Post post = postRepository.findById(id).orElse(null); // throw post not found

        if (post != null && (post.isPrivate() && jwtService.isValidToken())) {
            return ResponseEntity.ok(post);
        }
        return ResponseEntity.notFound().build();
    }

    //get list of posts from username || "public list if no token, if owner, all list"
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getPostsByUsername(@PathVariable String username) {
        // Implement fetching posts by username
        return ResponseEntity.ok().body(postRepository.findByUsername(username)); // Assuming you have a method to find by username
    }

    //create new post - with jwt
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody Post post) {
        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);
        return ResponseEntity.ok().build();
    }

    //edit a existing post - jwt
    @PutMapping("/{id}")
    public ResponseEntity<?> editPost(@PathVariable Long id, @RequestBody Post updatedPost) {
        // Implement post update logic, checking if the updater is the post owner
        return ResponseEntity.ok().build();
    }


    // add comment -jwt
    @PostMapping("/{postId}/comment")
    public ResponseEntity<?> addComment(@PathVariable Long postId, @RequestBody String commentText) {
        // Implement comment addition logic
        return ResponseEntity.ok().build();
    }

    // edit existing comment -jwt
    @PutMapping("/comment/{commentId}")
    public ResponseEntity<?> editComment(@PathVariable Long commentId, @RequestBody String newCommentText) {
        // Implement comment editing logic
        return ResponseEntity.ok().build();
    }

    // delete comment - jwt owner of comment OR jwt owner of post
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        // Implement comment deletion logic
        return ResponseEntity.ok().build();
    }

    // star post - jwt
    @PostMapping("/{postId}/star")
    public ResponseEntity<?> starPost(@PathVariable Long postId) {
        // Implement post starring logic
        return ResponseEntity.ok().build();
    }

    // un star post - jwt
    @DeleteMapping("/{postId}/unstar")
    public ResponseEntity<?> unStarPost(@PathVariable Long postId) {
        // Implement post unstarring logic
        return ResponseEntity.ok().build();
    }

    // like post - jwt
    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable Long postId) {
        // Implement post liking logic
        return ResponseEntity.ok().build();
    }

    // unlike post - jwt
    @DeleteMapping("/{postId}/unlike")
    public ResponseEntity<?> unlikePost(@PathVariable Long postId) {
        // Implement post unliking logic
        return ResponseEntity.ok().build();
    }

}
