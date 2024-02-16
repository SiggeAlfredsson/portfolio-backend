package com.siggebig.demo.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    private String username;

    // set so only one, cant remember the annotation on flight
    private String email;
    private String password;

    @ManyToMany
    private Set<User> followers = new HashSet<>();

    @ManyToMany
    private Set<User> following = new HashSet<>();


//    soon to impl
//    @OneToMany(mappedBy = "user")
//    @JsonIgnoreProperties("user")
//    private List<Project> projects = new ArrayList<>();



}
