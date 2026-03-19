package org.example.porti.chat.message.model;

import lombok.Builder;
import lombok.Getter;
import org.example.porti.chat.attachment.model.ChatAttachmentsDto;
import org.example.porti.chat.room.model.ChatRoom;
import org.example.porti.user.model.User;

import java.util.Date;
import java.util.List;

import static org.example.porti.chat.message.model.ContentsType.TEXT;
import static org.example.porti.chat.message.model.ContentsType.USER_LEFT;

public class ChatMessageDto {
    @Getter
    public static class Send {
        private ContentsType type;
        private Long roomIdx;
        private String contents;

        public ChatMessage toEntity(ChatRoom room, User user, String contents, boolean isReceiverSubscribed) {
            return ChatMessage.builder()
                    .chatRoom(room)
                    .user(user)
                    .contentsType(type)
                    .contents(contents)
                    .isRead(isReceiverSubscribed)
                    .build();

        }
    }

    @Getter
    @Builder
    public static class Res {
        private Long idx;
        private Long roomIdx;
        private Long senderIdx;
        private String senderName;
        private String contents;
        private ContentsType contentsType;
        private List<ChatAttachmentsDto.Res> attachments;
        private boolean isRead;
        private Date createdAt;
        private Date updatedAt;

        public static Res from(ChatMessage entity) {
            return Res.builder()
                    .idx(entity.getIdx())
                    .roomIdx(entity.getChatRoom().getIdx())
                    .senderIdx(entity.getUser().getIdx())
                    .senderName(entity.getUser().getName())
                    .contents(entity.getContents())
                    .contentsType(entity.getContentsType())
                    .attachments(entity.getAttachments() != null ?
                            entity.getAttachments().stream().map(ChatAttachmentsDto.Res::from).toList() : null)
                    .isRead(entity.isRead())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        }
    }
}
