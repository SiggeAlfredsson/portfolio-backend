package com.siggebig.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

    @Lob
    private String description;

    private LocalDateTime createdAt;
    private boolean isPrivate;

    private Long userId;

    @ElementCollection
    @Column(name = "picturesIds")
    private List<Long> picturesIds = new ArrayList<>();

    public void addPictureId(long id) {
        this.picturesIds.add(id);
    }

    public void removePictureId(long id) {
        this.picturesIds.remove(id);
    }



    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @ElementCollection
    @Column(name = "starsUserIds")
    private List<Long> stars = new ArrayList<>(); // use username instead?

    public void addStar(long userId) {
        this.stars.add(userId);
    }

    public void removeStar(long userId) {
        this.stars.remove(userId);
    }

    @ElementCollection
    @Column(name = "likesUserIds")
    private List<Long> likes = new ArrayList<>(); // use username instead?

    public void addLike(long userId) {
        this.likes.add(userId);
    }

    public void removeLike(long userId) {
        this.likes.remove(userId);
    }



}

