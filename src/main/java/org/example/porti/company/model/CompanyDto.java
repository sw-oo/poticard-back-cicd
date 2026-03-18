package org.example.porti.company.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.example.porti.user.model.AuthUserDetails;
import org.springframework.data.domain.Page;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CompanyDto {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd-HH:mm:ss";

    @Getter
    @Builder
    public static class PageRes {
        private List<ListRes> companyList;
        private int totalPage;
        private long totalCount;
        private int currentPage;
        private int currentSize;
        private long recruitingCount;
        private long totalApplicants;

        public static PageRes from(Page<Company> result, long totalCount, long recruitingCount, long totalApplicants) {
            return PageRes.builder()
                    .companyList(result.get().map(CompanyDto.ListRes::from).toList())
                    .totalPage(result.getTotalPages())
                    .totalCount(totalCount)
                    .currentPage(result.getPageable().getPageNumber())
                    .currentSize(result.getPageable().getPageSize())
                    .recruitingCount(recruitingCount)
                    .totalApplicants(totalApplicants)
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegReq {

        @Schema(description = "공고 제목", requiredMode = Schema.RequiredMode.REQUIRED, example = "백엔드 개발자 채용")
        private String title;

        @Schema(description = "직무 카테고리", requiredMode = Schema.RequiredMode.REQUIRED, example = "Backend")
        private String category;

        @Schema(description = "고용 형태", requiredMode = Schema.RequiredMode.REQUIRED, example = "FULL_TIME")
        private String employmentType;

        @Schema(description = "경력 조건", requiredMode = Schema.RequiredMode.REQUIRED, example = "NEW")
        private String experience;

        @Schema(description = "근무 지역", example = "Seoul")
        private String location;

        @Schema(description = "연봉 최소 금액", example = "3000")
        private Integer salaryMin;

        @Schema(description = "연봉 최대 금액", example = "5000")
        private Integer salaryMax;

        @Schema(description = "모집 인원", example = "1")
        private Integer headcount;

        @JsonFormat(pattern = "yyyy-MM-dd")
        @Schema(description = "마감일", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-03-31")
        private LocalDate deadline;

        @JsonFormat(pattern = "yyyy-MM-dd")
        @Schema(description = "업무 시작일", example = "2026-04-01")
        private LocalDate workStart;

        @Schema(description = "기술 스택을 쉼표로 구분해서 입력", example = "Java, Spring Boot, MySQL")
        private String skills;

        @Schema(description = "회사 소개", example = "회사 소개")
        private String intro;

        @JsonAlias("jobDescription")
        @Schema(description = "상세 업무 설명", example = "서비스 개발 및 운영")
        private String description;

        @Schema(description = "자격 요건", example = "Spring Boot 경험")
        private String requirements;

        @Schema(description = "우대 사항", example = "대용량 트래픽 경험")
        private String preferred;

        @Schema(description = "채용 절차", example = "서류 - 1차 면접 - 최종 합격")
        private String process;

        @Schema(description = "담당자 이메일", requiredMode = Schema.RequiredMode.REQUIRED, example = "hr@company.com")
        private String contactEmail;

        @Schema(description = "담당자 연락처", example = "010-1234-5678")
        private String contactPhone;

        @JsonAlias("isRemotePossible")
        @Schema(description = "원격 근무 가능 여부", example = "false")
        private boolean remotePossible;

        @JsonAlias("isPublic")
        @Schema(description = "즉시 공개 여부", example = "true")
        private boolean publicOpen;

        public Company toEntity(AuthUserDetails user) {
            return Company.builder()
                    .title(this.title)
                    .category(this.category)
                    .employmentType(this.employmentType)
                    .experience(this.experience)
                    .location(this.location)
                    .salaryMin(this.salaryMin)
                    .salaryMax(this.salaryMax)
                    .headcount(this.headcount)
                    .deadline(this.deadline)
                    .workStart(this.workStart)
                    .skills(normalizeSkills())
                    .intro(this.intro)
                    .description(this.description)
                    .requirements(this.requirements)
                    .preferred(this.preferred)
                    .process(this.process)
                    .contactEmail(this.contactEmail)
                    .contactPhone(this.contactPhone)
                    .remotePossible(this.remotePossible)
                    .publicOpen(this.publicOpen)
                    .status("RECRUITING")
                    .applicants(0)
                    .newApplicants(0)
                    .viewCount(0)
                    .favoriteCount(0)
                    .user(user.toEntity())
                    .build();
        }

        public String normalizeSkills() {
            return CompanyDto.normalizeSkills(this.skills);
        }
    }

    @Builder
    @Getter
    public static class RegRes {
        private Long idx;
        private String title;
        private String category;
        private String employmentType;
        private String experience;
        private String location;
        private Integer salaryMin;
        private Integer salaryMax;
        private Integer headcount;
        private LocalDate deadline;
        private LocalDate workStart;
        private List<String> skills;
        private String intro;
        private String description;
        private String requirements;
        private String preferred;
        private String process;
        private String contactEmail;
        private String contactPhone;
        private boolean remotePossible;
        private boolean publicOpen;
        private String status;
        private int applicants;
        private int newApplicants;

        public static RegRes from(Company entity) {
            return RegRes.builder()
                    .idx(entity.getIdx())
                    .title(entity.getTitle())
                    .category(entity.getCategory())
                    .employmentType(entity.getEmploymentType())
                    .experience(entity.getExperience())
                    .location(entity.getLocation())
                    .salaryMin(entity.getSalaryMin())
                    .salaryMax(entity.getSalaryMax())
                    .headcount(entity.getHeadcount())
                    .deadline(entity.getDeadline())
                    .workStart(entity.getWorkStart())
                    .skills(splitSkills(entity.getSkills()))
                    .intro(entity.getIntro())
                    .description(entity.getDescription())
                    .requirements(entity.getRequirements())
                    .preferred(entity.getPreferred())
                    .process(entity.getProcess())
                    .contactEmail(entity.getContactEmail())
                    .contactPhone(entity.getContactPhone())
                    .remotePossible(entity.isRemotePossible())
                    .publicOpen(entity.isPublicOpen())
                    .status(entity.getStatus())
                    .applicants(entity.getApplicants())
                    .newApplicants(entity.getNewApplicants())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class ListRes {
        private Long idx;
        private String title;
        private String category;
        private String employmentType;
        private int applicants;
        private int newApplicants;
        private LocalDate deadline;
        private String status;
        private String createdAt;

        public static ListRes from(Company entity) {
            return ListRes.builder()
                    .idx(entity.getIdx())
                    .title(entity.getTitle())
                    .category(entity.getCategory())
                    .employmentType(entity.getEmploymentType())
                    .applicants(entity.getApplicants())
                    .newApplicants(entity.getNewApplicants())
                    .deadline(entity.getDeadline())
                    .status(entity.getStatus())
                    .createdAt(formatDateTime(entity.getCreatedAt()))
                    .build();
        }
    }

    @Builder
    @Getter
    public static class ReadRes {
        private Long idx;
        private String title;
        private String category;
        private String employmentType;
        private String experience;
        private String location;
        private Integer salaryMin;
        private Integer salaryMax;
        private Integer headcount;
        private LocalDate deadline;
        private LocalDate workStart;
        private List<String> skills;
        private String intro;
        private String description;
        private String requirements;
        private String preferred;
        private String process;
        private String contactEmail;
        private String contactPhone;
        private boolean remotePossible;
        private boolean publicOpen;
        private String status;
        private int applicants;
        private int newApplicants;
        private String writer;
        private String createdAt;

        public static ReadRes from(Company entity) {
            return ReadRes.builder()
                    .idx(entity.getIdx())
                    .title(entity.getTitle())
                    .category(entity.getCategory())
                    .employmentType(entity.getEmploymentType())
                    .experience(entity.getExperience())
                    .location(entity.getLocation())
                    .salaryMin(entity.getSalaryMin())
                    .salaryMax(entity.getSalaryMax())
                    .headcount(entity.getHeadcount())
                    .deadline(entity.getDeadline())
                    .workStart(entity.getWorkStart())
                    .skills(splitSkills(entity.getSkills()))
                    .intro(entity.getIntro())
                    .description(entity.getDescription())
                    .requirements(entity.getRequirements())
                    .preferred(entity.getPreferred())
                    .process(entity.getProcess())
                    .contactEmail(entity.getContactEmail())
                    .contactPhone(entity.getContactPhone())
                    .remotePossible(entity.isRemotePossible())
                    .publicOpen(entity.isPublicOpen())
                    .status(entity.getStatus())
                    .applicants(entity.getApplicants())
                    .newApplicants(entity.getNewApplicants())
                    .writer(entity.getUser() != null ? entity.getUser().getName() : null)
                    .createdAt(formatDateTime(entity.getCreatedAt()))
                    .build();
        }
    }

    @Builder
    @Getter
    public static class PublicListRes {
        private Long idx;
        private String companyName;
        private String title;
        private String category;
        private String location;
        private String experience;
        private List<String> skills;
        private String updatedAt;
        private int favoriteCount;
        private int viewCount;
        private boolean favorite;
        private boolean remotePossible;

        public static PublicListRes from(Company entity, boolean favorite) {
            return PublicListRes.builder()
                    .idx(entity.getIdx())
                    .companyName(entity.getUser() != null ? entity.getUser().getName() : "-")
                    .title(entity.getTitle())
                    .category(entity.getCategory())
                    .location(entity.getLocation())
                    .experience(entity.getExperience())
                    .skills(splitSkills(entity.getSkills()))
                    .updatedAt(formatDate(entity.getUpdatedAt()))
                    .favoriteCount(entity.getFavoriteCount())
                    .viewCount(entity.getViewCount())
                    .favorite(favorite)
                    .remotePossible(entity.isRemotePossible())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class PublicDetailRes {
        private Long idx;
        private String companyName;
        private String title;
        private String category;
        private String employmentType;
        private String experience;
        private String location;
        private Integer salaryMin;
        private Integer salaryMax;
        private Integer headcount;
        private LocalDate deadline;
        private LocalDate workStart;
        private List<String> skills;
        private String intro;
        private String description;
        private String requirements;
        private String preferred;
        private String process;
        private String contactEmail;
        private String contactPhone;
        private boolean remotePossible;
        private int favoriteCount;
        private int viewCount;
        private boolean favorite;
        private String updatedAt;

        public static PublicDetailRes from(Company entity, boolean favorite) {
            return PublicDetailRes.builder()
                    .idx(entity.getIdx())
                    .companyName(entity.getUser() != null ? entity.getUser().getName() : "-")
                    .title(entity.getTitle())
                    .category(entity.getCategory())
                    .employmentType(entity.getEmploymentType())
                    .experience(entity.getExperience())
                    .location(entity.getLocation())
                    .salaryMin(entity.getSalaryMin())
                    .salaryMax(entity.getSalaryMax())
                    .headcount(entity.getHeadcount())
                    .deadline(entity.getDeadline())
                    .workStart(entity.getWorkStart())
                    .skills(splitSkills(entity.getSkills()))
                    .intro(entity.getIntro())
                    .description(entity.getDescription())
                    .requirements(entity.getRequirements())
                    .preferred(entity.getPreferred())
                    .process(entity.getProcess())
                    .contactEmail(entity.getContactEmail())
                    .contactPhone(entity.getContactPhone())
                    .remotePossible(entity.isRemotePossible())
                    .favoriteCount(entity.getFavoriteCount())
                    .viewCount(entity.getViewCount())
                    .favorite(favorite)
                    .updatedAt(formatDate(entity.getUpdatedAt()))
                    .build();
        }
    }

    @Builder
    @Getter
    public static class FavoriteToggleRes {
        private Long companyIdx;
        private boolean favorite;
        private int favoriteCount;

        public static FavoriteToggleRes of(Long companyIdx, boolean favorite, int favoriteCount) {
            return FavoriteToggleRes.builder()
                    .companyIdx(companyIdx)
                    .favorite(favorite)
                    .favoriteCount(favoriteCount)
                    .build();
        }
    }

    private static List<String> splitSkills(String skills) {
        if (skills == null || skills.isBlank()) {
            return Collections.emptyList();
        }

        return Arrays.stream(skills.split(","))
                .map(String::trim)
                .filter(skill -> !skill.isBlank())
                .toList();
    }

    private static String normalizeSkills(String skills) {
        if (skills == null || skills.isBlank()) {
            return null;
        }

        return Arrays.stream(skills.split(","))
                .map(String::trim)
                .filter(skill -> !skill.isBlank())
                .distinct()
                .collect(Collectors.joining(","));
    }

    private static String formatDateTime(Date dateTime) {
        if (dateTime == null) {
            return null;
        }
        return new SimpleDateFormat(DATE_TIME_PATTERN).format(dateTime);
    }

    private static String formatDate(Date dateTime) {
        if (dateTime == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(dateTime);
    }
}