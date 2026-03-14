package org.example.porti.chat.chatmessage;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.porti.chat.chatmessage.model.ChatMessage;
import org.example.porti.chat.chatmessage.model.ChatMessageDto;
import org.example.porti.chat.chatroom.ChatRoomRepository;
import org.example.porti.chat.chatroom.model.ChatRoom;
import org.example.porti.notification.NotificationService;
import org.example.porti.user.UserRepository;
import org.example.porti.user.model.User;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final SimpUserRegistry userRegistry;

    public ChatMessageDto.Res saveMessage(ChatMessageDto.Send req, Long senderIdx) {
        ChatRoom room = chatRoomRepository.findById(req.getRoomIdx()).orElseThrow(() -> new MessageDeliveryException("Invalid ChatRoom"));
        User sender = userRepository.findById(senderIdx).orElseThrow(() -> new MessageDeliveryException("Invalid Sender"));
        User receiver = room.getOpponent(senderIdx);

        String destination = "/sub/chat/room/" + req.getRoomIdx();
        boolean isReceiverSubscribed = isUserSubscribed(receiver.getEmail(), destination);

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(room)
                .user(sender)
                .contents(req.getContents())
                .isRead(isReceiverSubscribed) // 구독 중이면 true(1), 아니면 false(0)
                .build();

        ChatMessage res = chatMessageRepository.save(chatMessage);
        if (!isReceiverSubscribed) {
            notificationService.sendToUser(room, sender, receiver, res);
        }
        return ChatMessageDto.Res.from(res);
    }

    private boolean isUserSubscribed(String username, String destination) {
        SimpUser simpUser = userRegistry.getUser(username);
        if (simpUser == null) return false;

        return simpUser.getSessions().stream()
                .flatMap(session -> session.getSubscriptions().stream())
                .anyMatch(sub -> sub.getDestination().equals(destination));
    }

    public List<ChatMessageDto.Res> messages(Long roomIdx) {
        List<ChatMessage> messages = chatMessageRepository.findAllByChatRoomIdxOrderByCreatedAtAsc(roomIdx);

        return messages.stream().map(ChatMessageDto.Res::from).toList();
    }

    @Transactional
    public void markMessagesAsRead(Long roomIdx, Long userIdx) {
        chatMessageRepository.markAsReadByRoomIdxAndNotUserIdx(roomIdx, userIdx);
    }
}
