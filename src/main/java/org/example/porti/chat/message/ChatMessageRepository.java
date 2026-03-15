package org.example.porti.chat.message;

import org.example.porti.chat.message.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByChatRoomIdxOrderByCreatedAtAsc(Long roomIdx);

    long countByChatRoomIdxAndUserIdxNotAndIsReadFalse(Long roomIdx, Long userIdx);

    Optional<ChatMessage> findFirstByChatRoomIdxOrderByCreatedAtDesc(Long roomIdx);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ChatMessage m SET m.isRead = true WHERE m.chatRoom.idx = :roomIdx AND m.user.idx != :userIdx AND m.isRead = false")
    void markAsReadByRoomIdxAndNotUserIdx(@Param("roomIdx") Long roomIdx, @Param("userIdx") Long userIdx);
}
