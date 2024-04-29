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
import com.siggebig.services.PictureService;
import com.siggebig.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private PictureService pictureService;

    @Autowired
    private AccessService accessService;

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping("/all") // for dev
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @GetMapping("/allcomments") // for dev
    public List<Comment> getAllComment() {
        return commentRepository.findAll();
    }

    @GetMapping("/scroll/public")
    public ResponseEntity<Page<Post>> getPublicPosts(@RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findByIsPrivateFalseOrderByCreatedAtDesc(pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable("id") Long id, @RequestHeader(value = "Authorization", required = false, defaultValue = "") String token) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Post not found"));

        if (post.isPrivate()) {
            if(token.isEmpty()) {
                throw new UnauthorizedException("private post, you do not have access");
            }
            User user = jwtService.getUserFromToken(token);
            accessService.verifyUserAccessToPost(user, post);

            return ResponseEntity.ok().body(post);
        }
        return ResponseEntity.ok().body(post);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getPostsByUsername(@PathVariable("username") String username, @RequestHeader(value = "Authorization", required = false) String token) {
        List<Post> posts = postRepository.findByUsername(username);

        if (!(token == null)) {
            User jwtUser = jwtService.getUserFromToken(token);
            if (jwtUser.isAdmin() || jwtUser.getUsername().equals(username)) {
                return ResponseEntity.ok().body(posts);
            }
        }
        posts.removeIf(Post::isPrivate);
        return ResponseEntity.ok().body(posts);
    }

    //create new post - with jwt
    @PostMapping
    public ResponseEntity<?> createPost(@RequestPart("post") Post post,
                                        @RequestPart(value = "files", required = false) MultipartFile[] files,
                                        @RequestHeader("Authorization") String token) throws IOException {
        User user = jwtService.getUserFromToken(token);
        Post newPost = new Post();
        newPost.setCreatedAt(LocalDateTime.now());
        newPost.setUsername(user.getUsername());
        newPost.setTitle(post.getTitle());
        newPost.setDescription(post.getDescription());
        newPost.setPrivate(post.isPrivate());
        newPost = postRepository.save(newPost);
        user.addPost(newPost.getId());
        userService.updateUser(user, token);

        if(files != null) {
            List<Long> picIds = pictureService.uploadPictures(files);
            picIds.forEach(newPost::addPictureId);
            postRepository.save(newPost);
        }

        return ResponseEntity.ok().body(newPost);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> editPost(@PathVariable("id") Long id,
                                      @RequestBody Post updatedPost,
                                      @RequestHeader ("Authorization") String token) {

        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Post not found"));
        User user = jwtService.getUserFromToken(token);
        accessService.verifyUserAccessToPost(user, post);

        post.setTitle(updatedPost.getTitle());
        post.setDescription(updatedPost.getDescription());
        post.setPrivate(updatedPost.isPrivate());

        postRepository.save(post);


        return ResponseEntity.ok().build();
    }


    @PostMapping("/{postId}/comment")
    public ResponseEntity<?> addComment(@PathVariable("postId") Long postId, @RequestBody String commentText, @RequestHeader ("Authorization") String token) {
        User user = jwtService.getUserFromToken(token);
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found"));

        if (post.isPrivate()) {
            throw new UnauthorizedException("Private post, cannot add comments");
        }

        Comment comment = new Comment();
        comment.setText(commentText);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUsername(user.getUsername());
        comment.setPost(post);

        comment = commentRepository.save(comment);

        user.addComment(comment.getId());
        userService.updateUser(user, token);


        return ResponseEntity.ok().build();
    }

    @PutMapping("/comment/{commentId}")
    public ResponseEntity<?> editComment(@PathVariable("commentId") Long commentId, @RequestBody String newCommentText, @RequestHeader ("Authorization") String token) {
        User user = jwtService.getUserFromToken(token);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found"));
        accessService.verifyUserAccessToComment(user, comment); // the idea is not that a admin should edit other comments but a admin will pass this check

        comment.setText(newCommentText);
        commentRepository.save(comment);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable("postId") Long postId, @RequestHeader ("Authorization") String token) {
        User user = jwtService.getUserFromToken(token);
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found"));
        accessService.verifyUserAccessToPost(user, post); // owner and admin pass this

        postRepository.delete(post);

        user.removePost(post.getId());
        userService.updateUser(user, token);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long commentId, @RequestHeader ("Authorization") String token) {
        User user = jwtService.getUserFromToken(token);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found"));
        accessService.verifyUserAccessToComment(user, comment); // owner and admin pass this
        commentRepository.delete(comment);

        user.removeComment(comment.getId());
        userService.updateUser(user, token);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/star")
    public ResponseEntity<?> starPost(@PathVariable("postId") Long postId, @RequestHeader ("Authorization") String token) {
        User user = jwtService.getUserFromToken(token);
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found"));
        if (post.isPrivate()) { // for now i'll just have so cant star private
            throw new UnauthorizedException("private post, cant star");
        }

        if(post.getStars().contains(user.getId())) {
            user.removeStar(post.getId());
            post.removeStar(user.getId());
        } else {
            user.addStar(post.getId());
            post.addStar(user.getId());
        }

        postRepository.save(post);
        userService.updateUser(user, token);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable("postId") Long postId, @RequestHeader ("Authorization") String token) {
        User user = jwtService.getUserFromToken(token);
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found"));
        if (post.isPrivate()) { // for now
            throw new UnauthorizedException("private post, cant like");
        }

        if(post.getLikes().contains(user.getId())) {
            post.removeLike(user.getId());
            user.removeLike(post.getId());
        } else {
            post.addLike(user.getId());
            user.addLike(post.getId());
        }

        postRepository.save(post);
        userService.updateUser(user, token);

        return ResponseEntity.ok().build();
    }

}
