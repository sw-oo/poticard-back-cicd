package org.example.porti.chat.room;

import org.example.porti.chat.room.model.ChatRoom;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Slice<ChatRoom> findAllByHostUserIdxOrGuestUserIdx(Long hostUserIdx, Long guestUserIdx, Pageable pageable);
    ChatRoom findByHostUserIdxAndGuestUserIdx(Long hostUserIdx, Long guestUserIdx);
}
