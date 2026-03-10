package org.example.porti.chat.chatroom.model;

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
        private Date createdAt;
        private Date updatedAt;

        public static CreateRes from(ChatRoom entity) {
            return CreateRes.builder()
                    .idx(entity.getIdx())
                    .hostUserIdx(entity.getHostUser().getIdx())
                    .guestUserIdx(entity.getGuestUser().getIdx())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ListRes {
        private Long idx;
        private String opponentUserName;
        private String opponentUserProfileImage;
        private String opponentUserCareer;
        private String lastContents;

        public static ListRes from(ChatRoom entity, Long currentUserIdx) {
            User opponent = entity.getOpponent(currentUserIdx);
            return ListRes.builder()
                    .idx(entity.getIdx())
                    .opponentUserName(opponent.getName())
                    .opponentUserProfileImage(opponent.getProfileImage())
                    .opponentUserCareer(opponent.getCareer())
                    .lastContents("test")
                    .build();
        }
    }


}
