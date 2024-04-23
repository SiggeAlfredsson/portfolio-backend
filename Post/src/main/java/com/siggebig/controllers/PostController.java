package com.siggebig.controllers;

import com.siggebig.repositorys.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    //get post for infinity scroll || maybe use "nexttoken" ?

    //get post from id

    //get list of posts from username

    //create new post

    //edit a existing post

    // add comment

    // edit existing comment

    // delete comment

    // star post

    // un star post

    // like post

    // unlike post

}
