package com.rogan.webnotify.Controller;

import com.rogan.webnotify.Entity.AppUser;
import com.rogan.webnotify.Repository.UserRepository;
import com.rogan.webnotify.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // allow from frontend
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;


    @Autowired
    private PasswordEncoder passwordEncoder;

    // 🔹 Signup
    @PostMapping("/signup")
    public String registerUser(@RequestBody AppUser user) {
       return userService.register(user);
    }
    @GetMapping("/users")
    public List<String> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(AppUser::getUsername)
                .toList();
    }


    // 🔹 Signin
    @PostMapping("/signin")
    public String loginUser(@RequestBody AppUser user) {
       return userService.login(user);
}}
