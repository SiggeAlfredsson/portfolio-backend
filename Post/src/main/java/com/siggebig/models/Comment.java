package com.siggebig.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;

    private LocalDateTime createdAt;

    private Long userId;


    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}

