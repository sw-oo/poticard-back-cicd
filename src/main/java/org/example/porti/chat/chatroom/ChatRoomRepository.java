package org.example.porti.chat.chatroom;

import org.example.porti.chat.chatroom.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findAllByHostUserIdxOrGuestUserIdx(Long hostUserIdx, Long guestUserIdx);
}
