package com.siggebig.demo.services;

import com.siggebig.demo.models.LoginDto;

public interface AuthService {
    boolean authenticate(LoginDto loginDto);

}
