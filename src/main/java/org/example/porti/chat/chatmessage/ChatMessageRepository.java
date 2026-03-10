package org.example.porti.chat.chatmessage;

import org.example.porti.chat.chatmessage.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
