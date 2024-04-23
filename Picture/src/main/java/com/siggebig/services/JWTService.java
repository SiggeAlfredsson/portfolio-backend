package com.siggebig.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.siggebig.models.User;
import org.springframework.stereotype.Service;

@Service
public class JWTService {

    public String getUsernameFromToken(String token) {

        // remove the "Bearer " prefix if it exists in the token
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256("hellofriend")).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("username").asString();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

}
