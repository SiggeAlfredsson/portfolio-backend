package org.siggebig.services;

import org.hibernate.cfg.SchemaToolingSettings;
import org.siggebig.exceptions.UnauthorizedException;
import org.siggebig.models.User;
import org.siggebig.repositorys.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@Service
public class AccessService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTService jwtService;

    public void verifyUserToken(User user, String token) {
        String username = jwtService.getUsernameFromToken(token);

        if (!username.equals(user.getUsername()) && !user.isAdmin()) {
            throw new UnauthorizedException("Invalid Token");
        }
    }

}