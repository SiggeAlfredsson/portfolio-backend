package org.siggebig.models;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

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

    private String description;

    private LocalDateTime registeredAt;
    private LocalDateTime lastSeen;


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


//    soon to impl
//    @OneToMany(mappedBy = "user")
//    @JsonIgnoreProperties("user")
//    private List<Project> projects = new ArrayList<>();



}
