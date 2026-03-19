package org.example.porti.notification.model;

import lombok.Builder;
import lombok.Getter;
import org.example.porti.chat.message.model.ChatMessage;
import org.example.porti.chat.room.model.ChatRoom;
import org.example.porti.user.model.User;

import java.util.Date;
import java.util.Map;

public class NotificationDto {

    @Getter
    public static class Subscribe {
        private Long userIdx;
        private String endpoint;
        private Map<String, String> keys;

        public NotificationEntity toEntity() {
            return NotificationEntity.builder()
                    .userIdx(this.userIdx)
                    .endpoint(this.endpoint)
                    .p256dh(this.keys.get("p256dh"))
                    .auth(this.keys.get("auth"))
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Payload {
        private Long roomIdx;
        private Long senderIdx;
        private String senderEmail;
        private String senderName;
        private String contents;
        private Date contentsCreatedAt;

        public static Payload from(ChatRoom room, User sender, ChatMessage msg) {
            return Payload.builder()
                    .roomIdx(room.getIdx())
                    .senderIdx(sender.getIdx())
                    .senderEmail(sender.getEmail())
                    .senderName(sender.getName())
                    .contents(msg.getContents())
                    .contentsCreatedAt(msg.getCreatedAt())
                    .build();
        }

        @Override
        public String toString() {
            // contents의 줄바꿈과 따옴표 처리 (JSON 파싱 에러 방지)
            String safeContents = this.contents != null ?
                    this.contents.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") : "";

            // contentsTime을 ISO-8601 형식과 유사하게 전송하거나 타임스탬프로 전송
            long timeMillis = (this.contentsCreatedAt != null) ? this.contentsCreatedAt.getTime() : System.currentTimeMillis();

            return String.format(
                    "{\"roomIdx\": %d, \"senderIdx\": %d, \"senderEmail\": \"%s\", \"senderName\": \"%s\", \"contents\": \"%s\", \"contentsTime\": %d}",
                    this.roomIdx,
                    this.senderIdx,
                    this.senderEmail,
                    this.senderName,
                    safeContents,
                    timeMillis
            );
        }
    }
}
