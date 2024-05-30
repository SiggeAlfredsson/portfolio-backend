package com.siggebig.services;

import com.siggebig.exceptions.UnauthorizedException;
import com.siggebig.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccessService {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserService userService;

    public void verifyUserToken(User user, String token) {
        String username = jwtService.getUsernameFromToken(token);
        User tokenUser = userService.getUserByUsername(username);

        if (!tokenUser.getUsername().equals(user.getUsername()) && !tokenUser.isAdmin()) {
            throw new UnauthorizedException("Invalid Token");
        }
        return;
    }

}