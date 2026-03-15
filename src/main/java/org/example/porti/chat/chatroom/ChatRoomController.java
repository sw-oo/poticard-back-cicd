package org.example.porti.chat.chatroom;

import lombok.RequiredArgsConstructor;
import org.example.porti.chat.chatmessage.ChatMessageService;
import org.example.porti.chat.chatmessage.model.ChatMessageDto;
import org.example.porti.chat.chatroom.model.ChatRoomDto;
import org.example.porti.common.model.BaseResponse;
import org.example.porti.user.model.AuthUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/room")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @PostMapping("/create/{guestUserIdx}")
    public ResponseEntity create(@AuthenticationPrincipal AuthUserDetails hostUser, @PathVariable Long guestUserIdx) {
        return ResponseEntity.ok(BaseResponse.success(chatRoomService.save(hostUser.getIdx(), guestUserIdx)));
    }

    @GetMapping("/list")
    public ResponseEntity list(
            @AuthenticationPrincipal AuthUserDetails currentUser) {
        List<ChatRoomDto.ListRes> chatRoomList = chatRoomService.list(currentUser.getIdx());
        return ResponseEntity.ok().body(BaseResponse.success(chatRoomList));
    }

    @GetMapping("/{roomIdx}/messages")
    public ResponseEntity getMessages(@PathVariable Long roomIdx, @AuthenticationPrincipal AuthUserDetails currentUser) {
        chatMessageService.markMessagesAsRead(roomIdx, currentUser.getIdx());
        chatMessageService.sendReadReceipt(roomIdx);
        List<ChatMessageDto.Res> messages = chatMessageService.messages(roomIdx);
        return ResponseEntity.ok(BaseResponse.success(messages));
    }

    @MessageMapping("/{roomIdx}/webrtc")
    @SendTo("/sub/webrtc")
    public Map<String, Object> webRtc(Map<String, Object> message) {
        return message;
    }
}
