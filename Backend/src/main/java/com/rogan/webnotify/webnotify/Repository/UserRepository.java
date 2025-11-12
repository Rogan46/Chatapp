package com.rogan.webnotify.webnotify.Repository;

import com.rogan.webnotify.webnotify.Entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser,Long> {
    Optional<AppUser>findByUsername(String username);
}
