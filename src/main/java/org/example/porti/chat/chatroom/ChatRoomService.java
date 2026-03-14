package org.example.porti.chat.chatroom;

import lombok.RequiredArgsConstructor;
import org.example.porti.chat.chatmessage.ChatMessageRepository;
import org.example.porti.chat.chatmessage.model.ChatMessage;
import org.example.porti.chat.chatroom.model.ChatRoom;
import org.example.porti.chat.chatroom.model.ChatRoomDto;
import org.example.porti.user.UserRepository;
import org.example.porti.user.model.User;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
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

    public List<ChatRoomDto.ListRes> list(Long idx) {
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByHostUserIdxOrGuestUserIdx(idx, idx);

        return chatRoomList.stream().map(room -> {
            Optional<ChatMessage> lastContents = chatMessageRepository.findFirstByChatRoomIdxOrderByCreatedAtDesc(room.getIdx());

            String lastMessage = lastContents.map(ChatMessage::getContents).orElse("채팅을 시작하세요");
            Date lastTime = lastContents.map(ChatMessage::getCreatedAt).orElse(null); // 메시지 없으면 null

            long unreadCount = chatMessageRepository.countByChatRoomIdxAndUserIdxNotAndIsReadFalse(room.getIdx(), idx);

            return ChatRoomDto.ListRes.from(room, idx, lastMessage, lastTime, unreadCount);
        }).toList();
    }

    @Transactional(readOnly = true)
    public boolean isParticipant(Long roomIdx, Long userIdx) {
        ChatRoom room = chatRoomRepository.findById(roomIdx).orElseThrow(() -> new MessageDeliveryException("Invalid ChatRoom"));
        return room.getHostUser().getIdx().equals(userIdx) || room.getGuestUser().getIdx().equals(userIdx);
    }
}
