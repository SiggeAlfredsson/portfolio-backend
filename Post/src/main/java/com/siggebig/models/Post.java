package com.siggebig.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private boolean isPrivate;

    @ElementCollection
    @CollectionTable(
            name = "post_pictures",
            joinColumns = @JoinColumn(name = "post_id")
    )
    @Column(name = "picture_id")
    private List<Long> pictureIds = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private Set<Comment> comments = new HashSet<>();

    @ManyToMany(mappedBy = "likedPosts")
    private Set<User> likes = new HashSet<>();

    @ManyToMany(mappedBy = "starredPosts")
    private Set<User> stars = new HashSet<>();
}
