package org.siggebig.services;

import org.siggebig.models.LoginDto;

public interface AuthService {
    boolean authenticate(LoginDto loginDto);

}
