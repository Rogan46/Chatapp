package com.rogan.webnotify.Service;

import com.rogan.webnotify.Entity.AppUser;
import com.rogan.webnotify.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService{
    @Autowired
    private JWTservice jwTservice;

    @Autowired
    AuthenticationManager authmanager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    public String register(AppUser user){
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return "❌ Username already exists";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "✅ User registered successfully";

    }
    public  String login(AppUser user){
        var found = userRepository.findByUsername(user.getUsername());
        if (found.isPresent() && passwordEncoder.matches(user.getPassword(), found.get().getPassword())) {
            Authentication authentication=
                    authmanager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));
            if(authentication.isAuthenticated())
                return jwTservice.generateToken(user.getUsername());

            return "Failure";

        }
        return "❌ Invalid username or password";
    }
    }

