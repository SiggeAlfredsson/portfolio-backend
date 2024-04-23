package org.siggebig.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String username;
    private String password;

    private boolean admin;

    private Long pictureId;


    @ElementCollection
    @Column(name = "followersIds")
    private List<Long> followersIds = new ArrayList<>();

    @ElementCollection
    @Column(name = "followingsIds")
    private List<Long> followingsIds = new ArrayList<>();

    public void addFollower(long userId) {
        this.followersIds.add(userId);
    }

    public void removeFollower(long userId) {
        this.followersIds.remove(userId);
    }

    public void addFollowing(long userId) {
        this.followingsIds.add(userId);
    }

    public void removeFollowing(long userId) {
        this.followingsIds.remove(userId);
    }

    @ElementCollection
    @Column(name = "postsIds")
    private List<Long> postsIds = new ArrayList<>();

    public void addPost(long postId) {
        this.postsIds.add(postId);
    }

    public void removePost(long postId) {
        this.postsIds.remove(postId);
    }

    @ElementCollection
    @Column(name = "commentsIds")
    private List<Long> commentsIds = new ArrayList<>();

    public void addComment(long commentId) {
        this.commentsIds.add(commentId);
    }

    public void removeComment(long commentId) {
        this.commentsIds.remove(commentId);
    }

    @ElementCollection
    @Column(name = "likedPostsIds")
    private List<Long> likedPostsIds = new ArrayList<>();

    public void addLike(long postId) {
        this.likedPostsIds.add(postId);
    }

    public void removeLike(long postId) {
        this.likedPostsIds.remove(postId);
    }

    @ElementCollection
    @Column(name = "starredPostsIds")
    private List<Long> starredPostsIds = new ArrayList<>();

    public void addStar(long postId) {
        this.starredPostsIds.add(postId);
    }

    public void removeStar(long postId) {
        this.starredPostsIds.remove(postId);
    }

}
