package org.example.porti.chat.room;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.porti.chat.message.ChatMessageService;
import org.example.porti.chat.message.model.ChatMessageDto;
import org.example.porti.chat.message.model.ContentsType;
import org.example.porti.chat.room.model.ChatRoomDto;
import org.example.porti.common.model.BaseResponse;
import org.example.porti.user.model.AuthUserDetails;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/room")
@Tag(name = "채팅방 관련 기능", description = "채팅방 생성, 조회, 나가기 및 파일 업로드 API")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @Operation(summary = "채팅방 생성", description = "상대방의 이메일을 이용해 채팅방을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 생성 성공", content = @Content(schema = @Schema(implementation = ChatRoomDto.CreateRes.class))),
            @ApiResponse(responseCode = "404", description = "상대방 이메일을 찾을 수 없음", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/create/{guestUserEmail}")
    public ResponseEntity create(@AuthenticationPrincipal AuthUserDetails hostUser, @PathVariable String guestUserEmail) {
        return ResponseEntity.ok(BaseResponse.success(chatRoomService.save(hostUser.getIdx(), guestUserEmail)));
    }

    @Operation(summary = "채팅방 나가기", description = "사용자가 특정 채팅방에서 나갑니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "퇴장 처리 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "403", description = "해당 채팅방에 권한이 없는 사용자", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PatchMapping("/{roomIdx}/leave")
    public ResponseEntity leave(@AuthenticationPrincipal AuthUserDetails currentUser, @PathVariable Long roomIdx) {
        return ResponseEntity.ok(BaseResponse.success(chatRoomService.leave(currentUser.getIdx(), roomIdx)));
    }

    @Operation(summary = "채팅방 목록 조회", description = "현재 로그인한 사용자의 채팅방 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ChatRoomDto.ListRes.class)))
    })
    @Parameters({
            @Parameter(name = "page", example = "0"),
            @Parameter(name = "size", example = "20"),
            @Parameter(name = "sort", hidden = true)
    })
    @GetMapping("/list")
    public ResponseEntity list(
            @AuthenticationPrincipal AuthUserDetails currentUser,
            @org.springdoc.core.annotations.ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)  Pageable pageable) {
        Slice<ChatRoomDto.ListRes> responses = chatRoomService.list(currentUser.getIdx(), pageable);
        return ResponseEntity.ok(BaseResponse.success(responses));
    }

    @GetMapping("/test/list")
    public ResponseEntity testList(
            @RequestParam(name = "testUserIdx") Long testUserIdx,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Slice<ChatRoomDto.ListRes> responses = chatRoomService.list(testUserIdx, pageable);
        return ResponseEntity.ok(BaseResponse.success(responses));
    }

    @Operation(summary = "메시지 내역 조회", description = "특정 채팅방의 이전 메시지들을 페이징하여 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메시지 내역 조회 성공", content = @Content(schema = @Schema(implementation = ChatMessageDto.Res.class))),
            @ApiResponse(responseCode = "404", description = "채팅방을 찾을 수 없음", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @Parameters({
            @Parameter(name = "page", example = "0"),
            @Parameter(name = "size", example = "30"),
            @Parameter(name = "sort", hidden = true)
    })
    @GetMapping("/{roomIdx}/messages")
    public ResponseEntity getMessages(
            @PathVariable Long roomIdx,
            @AuthenticationPrincipal AuthUserDetails currentUser,
            @org.springdoc.core.annotations.ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        chatMessageService.markMessagesAsRead(roomIdx, currentUser.getIdx());
        chatMessageService.sendReadReceipt(roomIdx);

        Slice<ChatMessageDto.Res> messages = chatMessageService.getMessagesPage(roomIdx, pageable);
        return ResponseEntity.ok(BaseResponse.success(messages));
    }

    @GetMapping("/{roomIdx}/messages/test")
    public ResponseEntity getMessages(
            @PathVariable Long roomIdx,
            @RequestParam Long testUserIdx,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        chatMessageService.markMessagesAsRead(roomIdx, testUserIdx);
        chatMessageService.sendReadReceipt(roomIdx);

        Slice<ChatMessageDto.Res> messages = chatMessageService.getMessagesPage(roomIdx, pageable);
        return ResponseEntity.ok(BaseResponse.success(messages));
    }


    @MessageMapping("/{roomIdx}/webrtc")
    @SendTo("/sub/webrtc")
    public Map<String, Object> webRtc(Map<String, Object> message) {
        return message;
    }

    @Operation(summary = "채팅 파일 업로드", description = "채팅 중 이미지나 파일을 전송하기 위해 업로드합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "파일 업로드 및 메시지 전송 성공", content = @Content(schema = @Schema(implementation = ChatMessageDto.Res.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 파일 형식 에러", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping(value = "/{roomIdx}/upload/{type}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity uploadFiles(
            @PathVariable Long roomIdx,
            @Parameter(
                    description = "파일 타입",
                    schema = @Schema(allowableValues = {"IMAGE", "DOCS"}) // 드롭다운 생성
            )
            @PathVariable String type,
            @Parameter(
                    description = "업로드할 파일 리스트",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestPart("files") List<MultipartFile> files,
            @AuthenticationPrincipal AuthUserDetails user) {

        ContentsType contentsType = ContentsType.valueOf(type.toUpperCase());
        ChatMessageDto.Res result = chatMessageService.uploadFiles(files, roomIdx, user.getIdx(), contentsType);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

}
