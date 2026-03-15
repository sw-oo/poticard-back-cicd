package org.example.porti.chat.room;

import lombok.RequiredArgsConstructor;
import org.example.porti.chat.message.ChatMessageService;
import org.example.porti.chat.message.model.ChatMessageDto;
import org.example.porti.chat.message.model.ContentsType;
import org.example.porti.chat.room.model.ChatRoomDto;
import org.example.porti.common.model.BaseResponse;
import org.example.porti.user.model.AuthUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/{roomIdx}/upload/{type}")
    public ResponseEntity uploadFiles(
            @PathVariable Long roomIdx,
            @PathVariable String type,
            @RequestPart List<MultipartFile> files,
            @AuthenticationPrincipal AuthUserDetails user) {

        ContentsType contentsType = ContentsType.valueOf(type.toUpperCase());
        ChatMessageDto.Res result = chatMessageService.uploadFiles(files, roomIdx, user.getIdx(), contentsType);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

}
