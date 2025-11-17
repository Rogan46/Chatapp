package com.rogan.webnotify.Service;


import com.rogan.webnotify.Entity.Event;
import com.rogan.webnotify.Repository.Eventrepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Eventservice {
    @Autowired
    private Eventrepo repo;

    public Event save(Event event){
        return repo.save(event);

    }
    public List<Event> getall(){
        return repo.findAll();
    }

    public Optional<Event> getbyid(Long id){
        return repo.findById(id);
    }

}
