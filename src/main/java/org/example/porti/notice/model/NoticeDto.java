package org.example.porti.notice.model;

import lombok.*;
import java.util.Date;

public class NoticeDto {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Req {
        private String title;
        private String content;

        public Notice toEntity() {
            return Notice.builder()
                    .title(this.title)
                    .content(this.content)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListRes {
        private Long idx;
        private String title;
        private Date createdAt;

        public static ListRes from(Notice entity) {
            return ListRes.builder()
                    .idx(entity.getIdx())
                    .title(entity.getTitle())
                    .createdAt(entity.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Res {
        private Long idx;
        private String title;
        private String content;
        private Date createdAt;
        private Date updatedAt;
        public static Res from(Notice entity) {
            return Res.builder()
                    .idx(entity.getIdx())
                    .title(entity.getTitle())
                    .content(entity.getContent())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        }
    }
}