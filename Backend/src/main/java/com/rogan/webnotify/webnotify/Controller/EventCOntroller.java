package com.rogan.webnotify.webnotify.Controller;


import com.rogan.webnotify.webnotify.Entity.Event;
import com.rogan.webnotify.webnotify.Service.Eventservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/webhook")

public class EventCOntroller {

    @Autowired
    private Eventservice ser;

    @PostMapping("/post")
    public String save(@RequestBody  Event event){
        ser.save(event);
        return "Saved succesfully";
    }
    @GetMapping
    public List<Event>getall(){
        return ser.getall();
    }
    @GetMapping("{id}")
    public Optional<Event> getbyid(@RequestParam Long id){
        return ser.getbyid(id);
    }
}
