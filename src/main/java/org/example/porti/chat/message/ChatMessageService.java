package org.example.porti.chat.message;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.porti.chat.attachment.ChatAttachmentsRepository;
import org.example.porti.chat.attachment.model.ChatAttachments;
import org.example.porti.chat.attachment.model.ChatAttachmentsDto;
import org.example.porti.chat.message.model.ChatMessage;
import org.example.porti.chat.message.model.ChatMessageDto;
import org.example.porti.chat.message.model.ContentsType;
import org.example.porti.chat.room.ChatRoomRepository;
import org.example.porti.chat.room.model.ChatRoom;
import org.example.porti.notification.NotificationService;
import org.example.porti.notification.SseController;
import org.example.porti.notification.model.NotificationDto;
import org.example.porti.upload.CloudUploadService;
import org.example.porti.user.UserRepository;
import org.example.porti.user.model.User;
import org.springframework.data.domain.Slice;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatAttachmentsRepository chatAttachmentsRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final SimpUserRegistry userRegistry;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final CloudUploadService cloudUploadService;
    private final SseController sseController;

    @Transactional
    public ChatMessageDto.Res saveMessage(ChatMessageDto.Send req, Long senderIdx) {
        ChatRoom room = chatRoomRepository.findById(req.getRoomIdx()).orElseThrow(() -> new MessageDeliveryException("Invalid ChatRoom"));
        User sender = userRepository.findById(senderIdx).orElseThrow(() -> new MessageDeliveryException("Invalid Sender"));
        User receiver = room.getOpponent(senderIdx);
        String contents = req.getType().getTypeMsg(sender.getName(), req.getContents());

        String destination = "/sub/chat/room/" + req.getRoomIdx();
        boolean isReceiverSubscribed = isUserSubscribed(receiver.getEmail(), destination);

        ChatMessage chatMessage = req.toEntity(room, sender, contents, isReceiverSubscribed);
        ChatMessage res = chatMessageRepository.save(chatMessage);

        NotificationDto.Payload payload = NotificationDto.Payload.from(room, sender, res);

        if (!isReceiverSubscribed) {
            if (sseController.hasConnection(receiver.getIdx())) {
                sseController.sendNotification(receiver.getIdx(), payload);
            }
            notificationService.sendToUser(room, sender, receiver, res);
        }

        ChatMessageDto.Res resDto = ChatMessageDto.Res.from(res);
        messagingTemplate.convertAndSend("/sub/chat/room/" + req.getRoomIdx(), resDto);

        return resDto;
    }

    @Transactional
    public ChatMessageDto.Res uploadFiles(List<MultipartFile> files, Long roomIdx, Long senderIdx, ContentsType contentsType) {
        for (MultipartFile file : files) {
            String mimeType = file.getContentType();
            if (contentsType == ContentsType.IMAGE && !mimeType.startsWith("image/")) {
                throw new RuntimeException("이미지 형식의 파일이 아닙니다.");
            }
            if (contentsType == ContentsType.DOC && mimeType.startsWith("image/")) {
                throw new RuntimeException("문서 형식의 파일이 아닙니다.");
            }
        }

        ChatRoom room = chatRoomRepository.findById(roomIdx).orElseThrow(() -> new MessageDeliveryException("Invalid ChatRoom"));
        User sender = userRepository.findById(senderIdx).orElseThrow(() -> new MessageDeliveryException("Invalid Sender"));
        User receiver = room.getOpponent(senderIdx);
        String contents = contentsType.getTypeMsg(sender.getName());


        String destination = "/sub/chat/room/" + roomIdx;
        boolean isReceiverSubscribed = isUserSubscribed(receiver.getEmail(), destination);

        ChatMessageDto.Send sendDto = new ChatMessageDto.Send();
        ChatMessage chatMessage = sendDto.toEntity(room, sender, contents, isReceiverSubscribed);
        ChatMessage res = chatMessageRepository.save(chatMessage);

        List<ChatAttachments> savedAttachments = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                String url = cloudUploadService.saveFile(file);
                ChatAttachments attachment = ChatAttachmentsDto.toEntity(file, url, res);
                savedAttachments.add(chatAttachmentsRepository.save(attachment));
            } catch (Exception e) {
                throw new RuntimeException("파일 업로드 실패: " + e.getMessage());
            }
        }
        res.setAttachments(savedAttachments);

        if (!isReceiverSubscribed) {
            notificationService.sendToUser(room, sender, receiver, res);
        }

        ChatMessageDto.Res resDto = ChatMessageDto.Res.from(res);
        messagingTemplate.convertAndSend("/sub/chat/room/" + roomIdx, resDto);
        return resDto;
    }

    private boolean isUserSubscribed(String userEmail, String destination) {
        SimpUser simpUser = userRegistry.getUser(userEmail);
        if (simpUser == null) return false;

        return simpUser.getSessions().stream()
                .flatMap(session -> session.getSubscriptions().stream())
                .anyMatch(sub -> sub.getDestination().equals(destination));
    }

    @Transactional
    public Slice<ChatMessageDto.Res> getMessagesPage(Long roomIdx, Pageable pageable) {
        // 최신순으로 가져와서 DTO로 변환
        return chatMessageRepository.findAllByChatRoomIdxOrderByCreatedAtDesc(roomIdx, pageable)
                .map(ChatMessageDto.Res::from);
    }

    @Transactional
    public void markMessagesAsRead(Long roomIdx, Long userIdx) {
        chatMessageRepository.markAsReadByRoomIdxAndNotUserIdx(roomIdx, userIdx);
    }

    public void sendReadReceipt(Long roomIdx) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "READ_RECEIPT");
        payload.put("roomIdx", roomIdx);
        messagingTemplate.convertAndSend("/sub/chat/room/" + roomIdx, payload);
    }
}
