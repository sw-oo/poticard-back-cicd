package org.example.porti.community.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.example.porti.community.comment.model.CommunityCommentDto;
import org.example.porti.namecard.model.NamecardDto;
import org.example.porti.user.model.AuthUserDetails;
import org.springframework.data.domain.Page;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
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

        public static PageRes from(Page<Community> result, Long loginUserIdx, Set<Long> favoriteIds) {
            return PageRes.builder()
                    .communityList(result.get().map(entity -> CommunityDto.ListRes.from(entity, loginUserIdx, favoriteIds.contains(entity.getIdx()))).toList())
                    .totalPage(result.getTotalPages())
                    .totalCount(result.getTotalElements())
                    .currentPage(result.getPageable().getPageNumber())
                    .currentSize(result.getPageable().getPageSize())
                    .build();
        }

        public static PageRes from(List<Community> result, int page, int size, Long loginUserIdx, Set<Long> favoriteIds) {
            return PageRes.builder()
                    .communityList(result.stream().map(entity -> CommunityDto.ListRes.from(entity, loginUserIdx, favoriteIds.contains(entity.getIdx()))).toList())
                    .totalPage(1)
                    .totalCount(result.size())
                    .currentPage(page)
                    .currentSize(size)
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
                    .tags(CommunityDto.normalizeTags(this.tags))
                    .anonymous(this.anonymous)
                    .solved(false)
                    .likesCount(0)
                    .commentCount(0)
                    .viewCount(0)
                    .user(user.toEntity())
                    .build();
        }

        public String normalizeTags() {
            return CommunityDto.normalizeTags(this.tags);
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
        private Long writerIdx;
        private List<String> tags;
        private int likesCount;
        private int commentCount;
        private int viewCount;
        private boolean favorite;
        private boolean owner;
        private String contents;
        private String createdAt;

        public static ListRes from(Community entity, Long loginUserIdx, boolean favorite) {
            Long writerIdx = entity.getUser() != null ? entity.getUser().getIdx() : null;
            return ListRes.builder()
                    .idx(entity.getIdx())
                    .category(entity.getCategory())
                    .solved(entity.isSolved())
                    .title(entity.getTitle())
                    .writer(entity.isAnonymous() ? "익명" : entity.getUser().getName())
                    .writerIdx(writerIdx)
                    .tags(splitTags(entity.getTags()))
                    .likesCount(entity.getLikesCount())
                    .commentCount(entity.getCommentCount())
                    .viewCount(entity.getViewCount())
                    .favorite(favorite)
                    .owner(loginUserIdx != null && loginUserIdx.equals(writerIdx))
                    .contents(entity.getContents())
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
        private Long writerIdx;
        private List<String> tags;
        private int likesCount;
        private int commentCount;
        private int viewCount;
        private boolean favorite;
        private boolean owner;
        private String createdAt;
        private List<CommunityCommentDto.CommentRes> comments;

        public static ReadRes from(Community entity,
                                   Long loginUserIdx,
                                   boolean favorite,
                                   List<CommunityCommentDto.CommentRes> comments) {
            Long writerIdx = entity.getUser() != null ? entity.getUser().getIdx() : null;
            return ReadRes.builder()
                    .idx(entity.getIdx())
                    .category(entity.getCategory())
                    .solved(entity.isSolved())
                    .title(entity.getTitle())
                    .contents(entity.getContents())
                    .writer(entity.isAnonymous() ? "익명" : entity.getUser().getName())
                    .writerIdx(writerIdx)
                    .tags(splitTags(entity.getTags()))
                    .likesCount(entity.getLikesCount())
                    .commentCount(entity.getCommentCount())
                    .viewCount(entity.getViewCount())
                    .favorite(favorite)
                    .owner(loginUserIdx != null && loginUserIdx.equals(writerIdx))
                    .createdAt(formatDateTime(entity.getCreatedAt()))
                    .comments(comments)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class FavoriteToggleRes {
        private Long postId;
        private boolean favorite;
        private int likesCount;

        public static FavoriteToggleRes of(Long postId, boolean favorite, int likesCount) {
            return FavoriteToggleRes.builder()
                    .postId(postId)
                    .favorite(favorite)
                    .likesCount(likesCount)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class ProfileRes {
        private Long idx;
        private String email;
        private String name;
        private String phone;
        private String role;
        private String affiliation;
        private String career;
        private String address;
        private String profileImage;

        public static ProfileRes from(org.example.porti.user.model.User entity) {
            return ProfileRes.builder()
                    .idx(entity.getIdx())
                    .email(entity.getEmail())
                    .name(entity.getName())
                    .phone(entity.getPhone())
                    .role(entity.getRole())
                    .affiliation(entity.getAffiliation())
                    .career(entity.getCareer())
                    .address(entity.getAddress())
                    .profileImage(entity.getProfileImage())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class SummaryRes {
        private ProfileRes profile;
        private NamecardDto.NamecardRes namecard;
        private List<ListRes> popularPosts;
        private List<ListRes> myPosts;
        private List<CommunityCommentDto.CommentRes> myComments;

        public static SummaryRes of(ProfileRes profile,
                                    NamecardDto.NamecardRes namecard,
                                    List<ListRes> popularPosts,
                                    List<ListRes> myPosts,
                                    List<CommunityCommentDto.CommentRes> myComments) {
            return SummaryRes.builder()
                    .profile(profile)
                    .namecard(namecard)
                    .popularPosts(popularPosts)
                    .myPosts(myPosts)
                    .myComments(myComments)
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