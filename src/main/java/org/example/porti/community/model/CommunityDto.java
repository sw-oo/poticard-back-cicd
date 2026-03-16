package org.example.porti.community.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.porti.user.model.AuthUserDetails;
import org.springframework.data.domain.Page;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CommunityDto {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd-HH:mm:ss";

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

    private static String formatDateTime(Date date) {
        if (date == null) {
            return null;
        }

        return new SimpleDateFormat(DATE_TIME_PATTERN).format(date);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegReq {

        private String category;
        private String title;
        private String tags;

        @JsonAlias("body")
        private String contents;

        private boolean anonymous;

        public Community toEntity(AuthUserDetails user) {
            return Community.builder()
                    .category(category)
                    .title(title)
                    .contents(contents)
                    .tags(normalizeTags(tags))
                    .anonymous(anonymous)
                    .solved(false)
                    .likesCount(0)
                    .commentCount(0)
                    .user(user.toEntity())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RegRes {

        private Long idx;
        private String category;
        private String title;
        private String contents;
        private List<String> tags;
        private boolean anonymous;
        private boolean solved;

        public static RegRes from(Community community) {
            return RegRes.builder()
                    .idx(community.getIdx())
                    .category(community.getCategory())
                    .title(community.getTitle())
                    .contents(community.getContents())
                    .tags(splitTags(community.getTags()))
                    .anonymous(community.isAnonymous())
                    .solved(community.isSolved())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
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

        public static ReadRes from(Community community) {
            return ReadRes.builder()
                    .idx(community.getIdx())
                    .category(community.getCategory())
                    .solved(community.isSolved())
                    .title(community.getTitle())
                    .contents(community.getContents())
                    .writer(community.isAnonymous()
                            ? "익명"
                            : community.getUser().getName())
                    .tags(splitTags(community.getTags()))
                    .likesCount(community.getLikesCount())
                    .commentCount(community.getCommentCount())
                    .createdAt(formatDateTime(community.getCreatedAt()))
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
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

        public static ListRes from(Community community) {
            return ListRes.builder()
                    .idx(community.getIdx())
                    .category(community.getCategory())
                    .solved(community.isSolved())
                    .title(community.getTitle())
                    .writer(community.isAnonymous()
                            ? "익명"
                            : community.getUser().getName())
                    .tags(splitTags(community.getTags()))
                    .likesCount(community.getLikesCount())
                    .commentCount(community.getCommentCount())
                    .createdAt(formatDateTime(community.getCreatedAt()))
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PageRes {

        private List<ListRes> communityList;
        private int totalPage;
        private long totalCount;
        private int currentPage;
        private int currentSize;

        public static PageRes from(Page<Community> page) {
            return PageRes.builder()
                    .communityList(page.getContent().stream()
                            .map(ListRes::from)
                            .toList())
                    .totalPage(page.getTotalPages())
                    .totalCount(page.getTotalElements())
                    .currentPage(page.getNumber())
                    .currentSize(page.getSize())
                    .build();
        }
    }
}