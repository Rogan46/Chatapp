package com.rogan.webnotify.Controller;

import com.rogan.webnotify.Config.WebSocketEventListener;
import com.rogan.webnotify.Entity.Chatmessage;
import com.rogan.webnotify.Entity.PrivateMessage;
import com.rogan.webnotify.Service.PrivateMessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class Chatcontroller {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private WebSocketEventListener listener;
    @Autowired
    private PrivateMessageService ser;

    @MessageMapping("/user-status")
    @SendTo("/topic/public")
    public Chatmessage userStatus(Chatmessage message) {
        if ("JOIN".equals(message.getType())) {
            listener.addUser(message.getSender());
        }
        else if ("LEAVE".equals(message.getType())) {
            listener.removeUser(message.getSender());
        }
        return message;
    }

    // Public broadcast
    @MessageMapping("/public-message")
    @SendTo("/topic/public")
    public Chatmessage sendPublicMessage(Chatmessage message) {

        System.out.println("Public: " + message.getContent());
        return message;
    }

    @MessageMapping("/private-message")
    public void sendPrivateMessage(Chatmessage message) {
        PrivateMessage pm = new PrivateMessage();
        pm.setSender(message.getSender());
        pm.setReceiver(message.getReceiver());
        pm.setContent(message.getContent());
        ser.save(pm);

        System.out.printf("Private from %s → %s%n",
                message.getSender(), message.getReceiver());
        simpMessagingTemplate.convertAndSendToUser(
                message.getReceiver(),
                "/queue/messages",
                message
        );
    }
    @GetMapping("/messages/{user1}/{user2}")
    public List<PrivateMessage> getMessages(@PathVariable("user1") String user1,
                                            @PathVariable("user2") String user2) {
        return ser.getChatHistory(user1, user2);
    }

}
