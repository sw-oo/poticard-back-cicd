package org.example.porti.chat.room.model;

import lombok.Builder;
import lombok.Getter;
import org.example.porti.user.model.User;
import java.util.Date;

public class ChatRoomDto {
    public static ChatRoom toEntity(User hostUser, User guestUser) {
        return ChatRoom.builder()
                .hostUser(hostUser)
                .guestUser(guestUser)
                .build();
    }

    @Getter
    @Builder
    public static class CreateRes {
        private Long idx;
        private Long hostUserIdx;
        private Long guestUserIdx;
        private String message;
        private Date createdAt;
        private Date updatedAt;

        public static CreateRes from(ChatRoom entity) {
            return CreateRes.builder()
                    .idx(entity.getIdx())
                    .hostUserIdx(entity.getHostUser() != null ? entity.getHostUser().getIdx() : null)
                    .guestUserIdx(entity.getGuestUser() != null ? entity.getGuestUser().getIdx() : null)
                    .message(null)
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        }

        public static CreateRes from(ChatRoom entity, String msg) {
            return CreateRes.builder()
                    .idx(entity.getIdx())
                    .hostUserIdx(entity.getHostUser() != null ? entity.getHostUser().getIdx() : null)
                    .guestUserIdx(entity.getGuestUser() != null ? entity.getGuestUser().getIdx() : null)
                    .message(msg)
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ListRes {
        private Long idx;
        private Long opponentUserIdx;
        private String opponentUserName;
        private String opponentUserProfileImage;
        private String opponentUserCareer;
        private String lastContents;
        private Date lastContentsTime;
        private long unreadCount;

        public static ListRes from(ChatRoom entity, Long currentUserIdx, String lastMessage, Date lastTime, long unreadCount) {
            User opponent = entity.getOpponent(currentUserIdx);

            String name = (opponent != null) ? opponent.getName() : "알 수 없는 사용자";
            String profile = (opponent != null) ? opponent.getProfileImage() : null;
            String career = (opponent != null) ? opponent.getCareer() : "정보 없음";
            Long opponentIdx = (opponent != null) ? opponent.getIdx() : null;

            return ListRes.builder()
                    .idx(entity.getIdx())
                    .opponentUserIdx(opponentIdx)
                    .opponentUserName(name)
                    .opponentUserProfileImage(profile)
                    .opponentUserCareer(career)
                    .lastContents(lastMessage)
                    .lastContentsTime(lastTime)
                    .unreadCount(unreadCount)
                    .build();
        }
    }


}
