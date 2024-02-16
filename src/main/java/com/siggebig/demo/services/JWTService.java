package com.siggebig.demo.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.siggebig.demo.exceptions.UnauthorizedException;
import com.siggebig.demo.models.LoginDto;
import com.siggebig.demo.models.User;
import com.siggebig.demo.repositorys.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JWTService {

    // add a variable for the secret for easy change

    @Autowired(required = false)
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    public String getToken(LoginDto loginDto) {

        if(authService.authenticate(loginDto)) {
            return JWT.create()
                    .withClaim("email", loginDto.getEmail())
                    .sign(Algorithm.HMAC256("hellofriend"));
        } else {
            throw new UnauthorizedException("User not found from token");
        }

    }

    public String getEmailFromToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256("hellofriend")).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("email").asString();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    public User getUserFromToken(String token) {
        String email = getEmailFromToken(token);
        User user = userRepository.findByEmail(email);

        if(user == null) {
            throw new UnauthorizedException("User not found from token");
        }

        return user;
    }

}
