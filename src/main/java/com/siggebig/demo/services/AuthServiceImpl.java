package com.siggebig.demo.services;
import com.siggebig.demo.models.User;
import com.siggebig.demo.models.LoginDto;
import com.siggebig.demo.repositorys.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("authService")
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean authenticate(LoginDto loginDto) {

        var email = loginDto.getEmail();
        var password = loginDto.getPassword();

        User auth = userRepository.findByEmail(email);

        return auth != null && passwordEncoder.matches(password, auth.getPassword());

    }

}
