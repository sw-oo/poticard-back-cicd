package org.example.porti.chat.room;

import lombok.RequiredArgsConstructor;
import org.example.porti.chat.message.ChatMessageRepository;
import org.example.porti.chat.message.model.ChatMessage;
import org.example.porti.chat.room.model.ChatRoom;
import org.example.porti.chat.room.model.ChatRoomDto;
import org.example.porti.user.UserRepository;
import org.example.porti.user.model.User;
import org.springframework.data.domain.Slice;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public ChatRoomDto.CreateRes save(Long hostUserIdx, Long guestUserIdx) {
        User hostUser = userRepository.findById(hostUserIdx).orElseThrow();
        User guestUser = userRepository.findById(guestUserIdx).orElseThrow();
        ChatRoom check1 = chatRoomRepository.findByHostUserIdxAndGuestUserIdx(hostUserIdx, guestUserIdx);
        ChatRoom check2 = chatRoomRepository.findByHostUserIdxAndGuestUserIdx(guestUserIdx, hostUserIdx);
        if(check1 != null || check2 != null) {
            throw new RuntimeException("Room is already exists");
        }
        ChatRoom chatRoom = ChatRoomDto.toEntity(hostUser, guestUser);
        return ChatRoomDto.CreateRes.from(chatRoomRepository.save(chatRoom));
    }

    @Transactional(readOnly = true)
    public Slice<ChatRoomDto.ListRes> list(Long userIdx, Pageable pageable) {
        Slice<ChatRoom> chatRoomSlice = chatRoomRepository.findAllByHostUserIdxOrGuestUserIdx(userIdx, userIdx, pageable);

        return chatRoomSlice.map(room -> {
            Optional<ChatMessage> lastContents = chatMessageRepository.findFirstByChatRoomIdxOrderByCreatedAtDesc(room.getIdx());

            String lastMessage = lastContents.map(ChatMessage::getContents).orElse("채팅을 시작하세요");
            Date lastTime = lastContents.map(ChatMessage::getCreatedAt).orElse(null);

            long unreadCount = chatMessageRepository.countByChatRoomIdxAndUserIdxNotAndIsReadFalse(room.getIdx(), userIdx);
            return ChatRoomDto.ListRes.from(room, userIdx, lastMessage, lastTime, unreadCount);
        });
    }

    @Transactional(readOnly = true)
    public boolean isParticipant(Long roomIdx, Long userIdx) {
        ChatRoom room = chatRoomRepository.findById(roomIdx).orElseThrow(() -> new MessageDeliveryException("Invalid ChatRoom"));
        return room.getHostUser().getIdx().equals(userIdx) || room.getGuestUser().getIdx().equals(userIdx);
    }
}
