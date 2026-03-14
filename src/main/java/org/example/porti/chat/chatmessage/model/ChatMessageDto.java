package org.example.porti.chat.chatmessage.model;

import lombok.Builder;
import lombok.Getter;
import org.example.porti.chat.chatroom.model.ChatRoom;
import org.example.porti.user.model.User;

import java.util.Date;

public class ChatMessageDto {
    @Getter
    public static class Send {
        private Long roomIdx;
        private String contents;

        public ChatMessage toEntity(ChatRoom room, User user) {
            return ChatMessage.builder()
                    .chatRoom(room)
                    .user(user)
                    .contents(this.contents)
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
                    .isRead(entity.isRead())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        }
    }
}
