package org.siggebig.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.siggebig.exceptions.UnauthorizedException;
import org.siggebig.models.LoginDto;
import org.siggebig.models.User;
import org.siggebig.repositorys.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
                    .withClaim("username", loginDto.getUsername())
                    .sign(Algorithm.HMAC256("hellofriend"));
        } else {
            throw new UnauthorizedException("User not found from token");
        }

    }

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

    public User getUserFromToken(String token) {
        // remove the "Bearer " prefix if it exists in the token
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String username = getUsernameFromToken(token);
        User user = userRepository.findByUsername(username);

        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);

        if(user == null) {
            throw new UnauthorizedException("User not found from token");
        }

        return user;
    }

}
