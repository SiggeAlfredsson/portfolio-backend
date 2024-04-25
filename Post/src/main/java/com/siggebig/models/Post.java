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
    private Set<Comment> comments = new HashSet<>();

    @ManyToMany()
    private Set<User> likes = new HashSet<>();

    @ManyToMany()
    private Set<User> stars = new HashSet<>();
}

