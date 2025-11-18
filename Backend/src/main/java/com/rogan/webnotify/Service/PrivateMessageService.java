package com.rogan.webnotify.Service;

import com.rogan.webnotify.Entity.PrivateMessage;
import com.rogan.webnotify.Repository.PrivateMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PrivateMessageService {
    @Autowired
    private PrivateMessageRepository repo;

    public PrivateMessage save(PrivateMessage msg) {
        msg.setTimestamp(LocalDateTime.now());
        return repo.save(msg);
    }
    public List<PrivateMessage> getChatHistory(String user1, String user2) {
        return repo.findBySenderAndReceiverOrReceiverAndSender(user1, user2, user1, user2);

    }

    public List<PrivateMessage> getUndeliveredMessages(String user) {
        return repo.findByReceiverAndDelivered(user, false);
    }
}
