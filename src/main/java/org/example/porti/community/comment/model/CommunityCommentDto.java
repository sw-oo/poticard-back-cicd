package org.example.porti.community.comment.model;

import lombok.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommunityCommentDto {
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd-HH:mm:ss";

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegReq {
        private String contents;
    }

    @Builder
    @Getter
    public static class CommentRes {
        private Long idx;
        private Long postId;
        private String contents;
        private Long writerIdx;
        private String writer;
        private boolean owner;
        private String createdAt;

        public static CommentRes from(CommunityComment entity, Long loginUserIdx) {
            Long writerIdx = entity.getUser() != null ? entity.getUser().getIdx() : null;
            return CommentRes.builder()
                    .idx(entity.getIdx())
                    .postId(entity.getCommunity() != null ? entity.getCommunity().getIdx() : null)
                    .contents(entity.getContents())
                    .writerIdx(writerIdx)
                    .writer(entity.getUser() != null ? entity.getUser().getName() : "익명")
                    .owner(loginUserIdx != null && loginUserIdx.equals(writerIdx))
                    .createdAt(formatDateTime(entity.getCreatedAt()))
                    .build();
        }
    }

    private static String formatDateTime(Date dateTime) {
        if (dateTime == null) {
            return null;
        }
        return new SimpleDateFormat(DATE_TIME_PATTERN).format(dateTime);
    }
}