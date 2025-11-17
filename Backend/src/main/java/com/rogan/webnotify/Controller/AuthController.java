package com.rogan.webnotify.Controller;

import com.rogan.webnotify.Entity.AppUser;
import com.rogan.webnotify.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // allow from frontend
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 🔹 Signup
    @PostMapping("/signup")
    public String registerUser(@RequestBody AppUser user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return "❌ Username already exists";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "✅ User registered successfully";
    }

    // 🔹 Signin
    @PostMapping("/signin")
    public String loginUser(@RequestBody AppUser user) {
        var found = userRepository.findByUsername(user.getUsername());
        if (found.isPresent() && passwordEncoder.matches(user.getPassword(), found.get().getPassword())) {
            return "✅ Login successful";
        }
        return "❌ Invalid username or password";
    }
}
