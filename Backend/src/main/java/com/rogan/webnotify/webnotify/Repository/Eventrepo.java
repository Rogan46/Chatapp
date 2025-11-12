package com.rogan.webnotify.webnotify.Repository;

import com.rogan.webnotify.webnotify.Entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Eventrepo extends JpaRepository<Event,Long> {
}
