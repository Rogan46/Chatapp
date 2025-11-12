package com.rogan.webnotify.webnotify.Controller;

import com.rogan.webnotify.webnotify.Config.WebSocketEventListener;
import com.rogan.webnotify.webnotify.Entity.Chatmessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class Chatcontroller {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private WebSocketEventListener listener;

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
        System.out.printf("Private from %s → %s%n",
                message.getSender(), message.getReceiver());
        simpMessagingTemplate.convertAndSendToUser(
                message.getReceiver(),
                "/queue/messages",
                message
        );
    }
//    @GetMapping("/api/hostinfo")
//    public Map<String,String> hostInfo(HttpServletRequest req) {
//        Map<String,String> m = new HashMap<>();
//        m.put("host", req.getServerName()); // or compute InetAddress.getLocalHost().getHostAddress()
//        m.put("port", String.valueOf(req.getServerPort()));
//        return m;
//    }

    // ✅ Handle join and leave notifications


}
