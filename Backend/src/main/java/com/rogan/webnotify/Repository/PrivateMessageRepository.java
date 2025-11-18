package com.rogan.webnotify.Repository;

import com.rogan.webnotify.Entity.PrivateMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrivateMessageRepository extends JpaRepository< PrivateMessage,Long> {

    List<PrivateMessage> findBySenderAndReceiverOrReceiverAndSender(String sender,String receiver,String receiver2,String sender2);

    List<PrivateMessage> findByReceiverAndDelivered(String receiver, boolean delivered);
}
