package org.example.porti.company.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
        private String skills;
        private String intro;
        private String description;
        private String requirements;
        private String preferred;
        private String process;
        private String contactEmail;
        private String contactPhone;

        @JsonAlias("isRemotePossible")
        private boolean remotePossible;

        @JsonAlias("isPublic")
        private boolean publicOpen;

        public Company toEntity(AuthUserDetails user) {
            return Company.builder()
                    .title(title)
                    .category(category)
                    .employmentType(employmentType)
                    .experience(experience)
                    .location(location)
                    .salaryMin(salaryMin)
                    .salaryMax(salaryMax)
                    .headcount(headcount)
                    .deadline(deadline)
                    .workStart(workStart)
                    .skills(normalizeSkills())
                    .intro(intro)
                    .description(description)
                    .requirements(requirements)
                    .preferred(preferred)
                    .process(process)
                    .contactEmail(contactEmail)
                    .contactPhone(contactPhone)
                    .remotePossible(remotePossible)
                    .publicOpen(publicOpen)
                    .status("RECRUITING")
                    .applicants(0)
                    .newApplicants(0)
                    .user(user.toEntity())
                    .build();
        }

        public String normalizeSkills() {
            return CompanyDto.normalizeSkills(skills);
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
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

        public static RegRes from(Company company) {
            return RegRes.builder()
                    .idx(company.getIdx())
                    .title(company.getTitle())
                    .category(company.getCategory())
                    .employmentType(company.getEmploymentType())
                    .experience(company.getExperience())
                    .location(company.getLocation())
                    .salaryMin(company.getSalaryMin())
                    .salaryMax(company.getSalaryMax())
                    .headcount(company.getHeadcount())
                    .deadline(company.getDeadline())
                    .workStart(company.getWorkStart())
                    .skills(splitSkills(company.getSkills()))
                    .intro(company.getIntro())
                    .description(company.getDescription())
                    .requirements(company.getRequirements())
                    .preferred(company.getPreferred())
                    .process(company.getProcess())
                    .contactEmail(company.getContactEmail())
                    .contactPhone(company.getContactPhone())
                    .remotePossible(company.isRemotePossible())
                    .publicOpen(company.isPublicOpen())
                    .status(company.getStatus())
                    .applicants(company.getApplicants())
                    .newApplicants(company.getNewApplicants())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
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

        public static ReadRes from(Company company) {
            return ReadRes.builder()
                    .idx(company.getIdx())
                    .title(company.getTitle())
                    .category(company.getCategory())
                    .employmentType(company.getEmploymentType())
                    .experience(company.getExperience())
                    .location(company.getLocation())
                    .salaryMin(company.getSalaryMin())
                    .salaryMax(company.getSalaryMax())
                    .headcount(company.getHeadcount())
                    .deadline(company.getDeadline())
                    .workStart(company.getWorkStart())
                    .skills(splitSkills(company.getSkills()))
                    .intro(company.getIntro())
                    .description(company.getDescription())
                    .requirements(company.getRequirements())
                    .preferred(company.getPreferred())
                    .process(company.getProcess())
                    .contactEmail(company.getContactEmail())
                    .contactPhone(company.getContactPhone())
                    .remotePossible(company.isRemotePossible())
                    .publicOpen(company.isPublicOpen())
                    .status(company.getStatus())
                    .applicants(company.getApplicants())
                    .newApplicants(company.getNewApplicants())
                    .writer(company.getUser() != null ? company.getUser().getName() : null)
                    .createdAt(formatDateTime(company.getCreatedAt()))
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
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

        public static ListRes from(Company company) {
            return ListRes.builder()
                    .idx(company.getIdx())
                    .title(company.getTitle())
                    .category(company.getCategory())
                    .employmentType(company.getEmploymentType())
                    .applicants(company.getApplicants())
                    .newApplicants(company.getNewApplicants())
                    .deadline(company.getDeadline())
                    .status(company.getStatus())
                    .createdAt(formatDateTime(company.getCreatedAt()))
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PageRes {

        private List<ListRes> companyList;
        private int totalPage;
        private long totalCount;
        private int currentPage;
        private int currentSize;
        private long recruitingCount;
        private long totalApplicants;

        public static PageRes from(Page<Company> page, long totalCount, long recruitingCount, long totalApplicants) {
            return PageRes.builder()
                    .companyList(page.getContent().stream()
                            .map(ListRes::from)
                            .toList())
                    .totalPage(page.getTotalPages())
                    .totalCount(totalCount)
                    .currentPage(page.getNumber())
                    .currentSize(page.getSize())
                    .recruitingCount(recruitingCount)
                    .totalApplicants(totalApplicants)
                    .build();
        }
    }
}