package com.rogan.webnotify.Config;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private final Set<String> onlineUsers = new HashSet<>();

    @Autowired
    private SimpMessagingTemplate template;

    // called from controller when someone joins
    public void addUser(String username) {
        onlineUsers.add(username);
        template.convertAndSend("/topic/users", onlineUsers);
    }
    public void removeUser(String username) {
        onlineUsers.remove(username);
        template.convertAndSend("/topic/users", onlineUsers);
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) accessor.getSessionAttributes().get("username");
        if (username != null) {
            onlineUsers.remove(username);
            template.convertAndSend("/topic/users", onlineUsers);
        }
    }

    public boolean isOnline(String username) {
        return onlineUsers.contains(username);
    }
}
