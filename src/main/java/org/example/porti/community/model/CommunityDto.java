package org.example.porti.community.model;

import org.example.porti.user.model.AuthUserDetails;
import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.domain.Page;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CommunityDto {
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd-HH:mm:ss";

    @Getter
    @Builder
    public static class PageRes {
        private List<ListRes> communityList;
        private int totalPage;
        private long totalCount;
        private int currentPage;
        private int currentSize;

        public static PageRes from(Page<Community> result) {
            return PageRes.builder()
                    .communityList(result.get().map(CommunityDto.ListRes::from).toList())
                    .totalPage(result.getTotalPages())
                    .totalCount(result.getTotalElements())
                    .currentPage(result.getPageable().getPageNumber())
                    .currentSize(result.getPageable().getPageSize())
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegReq {
        @Schema(description = "카테고리", requiredMode = Schema.RequiredMode.REQUIRED, example = "QNA")
        private String category;

        @Schema(description = "제목", requiredMode = Schema.RequiredMode.REQUIRED, example = "제목01")
        private String title;

        @Schema(description = "태그를 쉼표로 구분해서 입력", example = "Spring, JPA, MySQL")
        private String tags;

        @JsonAlias("body")
        @Schema(description = "내용", requiredMode = Schema.RequiredMode.REQUIRED, example = "내용01")
        private String contents;

        @Schema(description = "익명 여부", example = "false")
        private boolean anonymous;

        public Community toEntity(AuthUserDetails user) {
            return Community.builder()
                    .category(this.category)
                    .title(this.title)
                    .contents(this.contents)
                    .tags(normalizeTags(this.tags))
                    .anonymous(this.anonymous)
                    .solved(false)
                    .likesCount(0)
                    .commentCount(0)
                    .user(user.toEntity())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class RegRes {
        private Long idx;
        private String category;
        private String title;
        private String contents;
        private List<String> tags;
        private boolean anonymous;
        private boolean solved;

        public static RegRes from(Community entity) {
            return RegRes.builder()
                    .idx(entity.getIdx())
                    .category(entity.getCategory())
                    .title(entity.getTitle())
                    .contents(entity.getContents())
                    .tags(splitTags(entity.getTags()))
                    .anonymous(entity.isAnonymous())
                    .solved(entity.isSolved())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class ListRes {
        private Long idx;
        private String category;
        private boolean solved;
        private String title;
        private String writer;
        private List<String> tags;
        private int likesCount;
        private int commentCount;
        private String createdAt;

        public static ListRes from(Community entity) {
            return ListRes.builder()
                    .idx(entity.getIdx())
                    .category(entity.getCategory())
                    .solved(entity.isSolved())
                    .title(entity.getTitle())
                    .writer(entity.isAnonymous() ? "익명" : entity.getUser().getName())
                    .tags(splitTags(entity.getTags()))
                    .likesCount(entity.getLikesCount())
                    .commentCount(entity.getCommentCount())
                    .createdAt(formatDateTime(entity.getCreatedAt()))
                    .build();
        }
    }

    @Builder
    @Getter
    public static class ReadRes {
        private Long idx;
        private String category;
        private boolean solved;
        private String title;
        private String contents;
        private String writer;
        private List<String> tags;
        private int likesCount;
        private int commentCount;
        private String createdAt;

        public static ReadRes from(Community entity) {
            return ReadRes.builder()
                    .idx(entity.getIdx())
                    .category(entity.getCategory())
                    .solved(entity.isSolved())
                    .title(entity.getTitle())
                    .contents(entity.getContents())
                    .writer(entity.isAnonymous() ? "익명" : entity.getUser().getName())
                    .tags(splitTags(entity.getTags()))
                    .likesCount(entity.getLikesCount())
                    .commentCount(entity.getCommentCount())
                    .createdAt(formatDateTime(entity.getCreatedAt()))
                    .build();
        }
    }

    private static List<String> splitTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return Collections.emptyList();
        }

        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isBlank())
                .toList();
    }

    private static String normalizeTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return null;
        }

        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isBlank())
                .distinct()
                .collect(Collectors.joining(","));
    }

    private static String formatDateTime(Date dateTime) {
        if (dateTime == null) {
            return null;
        }
        return new SimpleDateFormat(DATE_TIME_PATTERN).format(dateTime);
    }
}