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


//    soon to impl
//    @OneToMany(mappedBy = "user")
//    @JsonIgnoreProperties("user")
//    private List<Project> projects = new ArrayList<>();



}
