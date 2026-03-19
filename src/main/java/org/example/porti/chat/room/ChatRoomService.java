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
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public ChatRoomDto.CreateRes save(Long hostUserIdx, String guestUserEmail) {
        User hostUser = userRepository.findById(hostUserIdx).orElseThrow();
        User guestUser = userRepository.findByEmail(guestUserEmail).orElseThrow();
        ChatRoom check1 = chatRoomRepository.findByHostUserIdxAndGuestUserIdx(hostUserIdx, guestUser.getIdx());
        ChatRoom check2 = chatRoomRepository.findByHostUserIdxAndGuestUserIdx(guestUser.getIdx(), hostUserIdx);
        if(check1 != null || check2 != null) {
            throw new RuntimeException("Room is already exists");
        }
        ChatRoom chatRoom = ChatRoomDto.toEntity(hostUser, guestUser);
        return ChatRoomDto.CreateRes.from(chatRoomRepository.save(chatRoom));
    }

    @Transactional
    public ChatRoomDto.CreateRes leave(Long currentUserIdx, Long roomIdx) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));

        boolean isHost = chatRoom.getHostUser() != null && Objects.equals(currentUserIdx, chatRoom.getHostUser().getIdx());
        boolean isGuest = chatRoom.getGuestUser() != null && Objects.equals(currentUserIdx, chatRoom.getGuestUser().getIdx());

        if (isHost) {
            chatRoom.removeHost();
        } else if (isGuest) {
            chatRoom.removeGuest();
        } else {
            throw new IllegalArgumentException("해당 채팅방의 참여자가 아닙니다.");
        }

        if (chatRoom.getHostUser() == null && chatRoom.getGuestUser() == null) {
            chatRoomRepository.delete(chatRoom); // CascadeType.ALL에 의해 메시지도 삭제됨

            return ChatRoomDto.CreateRes.builder()
                    .idx(roomIdx)
                    .message("모든 사용자가 퇴장하여 채팅방과 메시지가 삭제되었습니다.")
                    .build();
        }
        return ChatRoomDto.CreateRes.from(chatRoom, "채팅방에서 나갔습니다.");
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
