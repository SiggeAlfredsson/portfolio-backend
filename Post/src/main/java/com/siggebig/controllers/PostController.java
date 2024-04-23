package com.siggebig.controllers;

import com.siggebig.repositorys.PostRepository;
import com.siggebig.services.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JWTService jwtService;

    //get post for infinity scroll || maybe use "nexttoken" ?

    //get post from id ---- if post is private -> jwt

    //get list of posts from username || "public list if no token, if owner, all list"

    //create new post - with jwt

    //edit a existing post - jwt

    // add comment -jwt

    // edit existing comment -jwt

    // delete comment - jwt owner of comment OR jwt owner of post

    // star post - jwt

    // un star post - jwt

    // like post - jwt

    // unlike post - jwt

}
