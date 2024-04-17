package org.siggebig.models;


import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
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

    // private Long pictureId;

    @ManyToMany //stackoverflow?
    private Set<User> followers = new HashSet<>();

    @ManyToMany //stackoverflow?
    private Set<User> following = new HashSet<>();

//    soon to impl
//    @OneToMany(mappedBy = "user")
//    @JsonIgnoreProperties("user")
//    private List<Project> projects = new ArrayList<>();



}
