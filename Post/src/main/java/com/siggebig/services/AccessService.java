package com.siggebig.services;

import com.siggebig.exceptions.UnauthorizedException;
import com.siggebig.models.Comment;
import com.siggebig.models.Post;
import com.siggebig.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccessService {

    @Autowired
    private JWTService jwtService;

    public void verifyUserToken(User user, String token) {
        String username = jwtService.getUsernameFromToken(token);
        if (!username.equals(user.getUsername()) && !user.isAdmin()) {
            throw new UnauthorizedException("Invalid Token");
        }
        return;
    }

    public void verifyUserAccessToPost(User user, Post post) {
        if (!(post.getUserId().equals(user.getId())) && !user.isAdmin()) {
            throw new UnauthorizedException("You do not have access to this post");
        }
        return;
    }

    public void verifyUserAccessToComment(User user, Comment comment) {
        if (!(comment.getUsername().equals(user.getUsername())) && !user.isAdmin()) {
            throw new UnauthorizedException("You do not have access to this comment");
        }
        return;
    }

}