package org.example.porti.chat.chatroom;

import lombok.RequiredArgsConstructor;
import org.example.porti.chat.chatroom.model.ChatRoomDto;
import org.example.porti.common.model.BaseResponse;
import org.example.porti.user.model.AuthUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/room")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

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
}
