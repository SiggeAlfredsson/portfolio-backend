package com.siggebig.controllers;

import com.siggebig.exceptions.CommentNotFoundException;
import com.siggebig.exceptions.PostNotFoundException;
import com.siggebig.exceptions.UnauthorizedException;
import com.siggebig.models.Comment;
import com.siggebig.models.Post;
import com.siggebig.models.User;
import com.siggebig.repositorys.CommentRepository;
import com.siggebig.repositorys.PostRepository;
import com.siggebig.services.AccessService;
import com.siggebig.services.JWTService;
import com.siggebig.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private AccessService accessService;
    @Autowired
    private CommentRepository commentRepository;

    //get post for infinity scroll || maybe use "nexttoken" ?
    @GetMapping
    public ResponseEntity<?> getPostsForScroll(@RequestParam(required = false) String nextToken) {
        // Implement logic for infinite scrolling, possibly using a pagination token or simple offset

        //filter out private posts , implement ai posts ?

        return ResponseEntity.ok().body(postRepository.findAll()); // Simplified example
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false, defaultValue = "") String token) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Post not found"));

        if (post.isPrivate() && !token.isEmpty()) { // if post is private, check if the user owns the post or the user isAdmin

            User user = jwtService.getUserFromToken(token);
            accessService.verifyUserAccessToPost(user, post);

            return ResponseEntity.ok().body(post);
        }

        throw new UnauthorizedException("private post, you do not have access");
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getPostsByUsername(@PathVariable String username, @RequestHeader(value = "Authorization", required = false, defaultValue = "") String token) {
        List<Post> posts = postRepository.findByUserUsername(username);

        if (!token.isEmpty()) {
            User user = jwtService.getUserFromToken(token);
            if (user.isAdmin() || user.getUsername() == username) {
                return ResponseEntity.ok().body(posts);
            }
        }

        posts.removeIf(Post::isPrivate);
        return ResponseEntity.ok().body(posts);
    }

    //create new post - with jwt
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody Post post, @RequestHeader ("Authorization") String token) {
        User user = jwtService.getUserFromToken(token);
        // new post object or post object? how do with pictures
        post.setCreatedAt(LocalDateTime.now());
        post.setUser(user);
        postRepository.save(post);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> editPost(@PathVariable Long id, @RequestBody Post updatedPost, @RequestHeader ("Authorization") String token) { //
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Post not found"));
        User user = jwtService.getUserFromToken(token);
        accessService.verifyUserAccessToPost(user, post);

        // how should pictures be updated??? !!!!

        post.setName(updatedPost.getName());
        post.setDescription(updatedPost.getDescription());
        post.setPrivate(updatedPost.isPrivate());

        postRepository.save(post);

        return ResponseEntity.ok().build();
    }


    @PostMapping("/{postId}/comment")
    public ResponseEntity<?> addComment(@PathVariable Long postId, @RequestBody String commentText, @RequestHeader ("Authorization") String token) {
        User user = jwtService.getUserFromToken(token);
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found"));

        if (post.isPrivate()) { // for now i'll just have so private posts cant get new comments
            throw new UnauthorizedException("private post, cant add comments");
        }

        Comment comment = new Comment();
        comment.setText(commentText);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(user);

        comment.setPost(post); // this yes or no

        comment = commentRepository.save(comment);

        post.getComments().add(comment);
        postRepository.save(post);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/comment/{commentId}")
    public ResponseEntity<?> editComment(@PathVariable Long commentId, @RequestBody String newCommentText, @RequestHeader ("Authorization") String token) {
        User user = jwtService.getUserFromToken(token);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found"));
        accessService.verifyUserAccessToComment(user, comment); // the idea is not that a admin should edit other comments but a admin will pass this check

        comment.setText(newCommentText);
        commentRepository.save(comment);§
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, @RequestHeader ("Authorization") String token) {
        User user = jwtService.getUserFromToken(token);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found"));
        accessService.verifyUserAccessToComment(user, comment); // owner and admin pass this
        commentRepository.delete(comment);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/star")
    public ResponseEntity<?> starPost(@PathVariable Long postId, @RequestHeader ("Authorization") String token) {
        User user = jwtService.getUserFromToken(token);
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found"));
        if (post.isPrivate()) { // for now i'll just have so cant star private
            throw new UnauthorizedException("private post, cant star");
        }

        // no idea if this is correct? werid because the user repo does not exist in this microservice
        user.getStarredPosts().add(post); // can i skip this? The userRepo is in another microservice so is kinda a pain to save the user object but it can be done if needed.

        post.getStars().add(user);
        postRepository.save(post);
        // enought to save post?

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/unstar")
    public ResponseEntity<?> unStarPost(@PathVariable Long postId, @RequestHeader ("Authorization") String token) {
        User user = jwtService.getUserFromToken(token);
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found"));
        if (post.isPrivate()) { // for now i'll just have so private posts cant get new comments
            throw new UnauthorizedException("private post, cant un star"); // actule maybe should be able to
        }
        // no idea if this is correct? werid because the user repo does not exist in this microservice
        user.getStarredPosts().remove(post); // can i skip this? The userRepo is in another microservice so is kinda a pain to save the user object but it can be done if needed.

        post.getStars().remove(user);
        postRepository.save(post);
        // enought to save post?

        return ResponseEntity.ok().build();
    }

    // like post - jwt
    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable Long postId, @RequestHeader ("Authorization") String token) {
        User user = jwtService.getUserFromToken(token);
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found"));
        if (post.isPrivate()) { // for now
            throw new UnauthorizedException("private post, cant like");
        }

        // no idea if this is correct? werid because the user repo does not exist in this microservice
        user.getLikedPosts().add(post); // can i skip this? The userRepo is in another microservice so is kinda a pain to save the user object but it can be done if needed.

        post.getLikes().add(user);
        postRepository.save(post);
        // enought to save post?

        return ResponseEntity.ok().build();
    }

    // unlike post - jwt
    @DeleteMapping("/{postId}/unlike")
    public ResponseEntity<?> unlikePost(@PathVariable Long postId, @RequestHeader ("Authorization") String token) {
        User user = jwtService.getUserFromToken(token);
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found"));
        if (post.isPrivate()) { // for now
            throw new UnauthorizedException("private post, cant like");
        }

        // no idea if this is correct? werid because the user repo does not exist in this microservice
        user.getLikedPosts().remove(post); // can i skip this? The userRepo is in another microservice so is kinda a pain to save the user object but it can be done if needed.

        post.getLikes().remove(user);
        postRepository.save(post);
        // enought to save post?

        return ResponseEntity.ok().build();
    }

}
