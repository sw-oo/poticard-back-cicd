package org.example.porti.company.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.porti.common.model.BaseEntity;
import org.example.porti.user.model.User;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Company extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 30)
    private String employmentType;

    @Column(nullable = false, length = 30)
    private String experience;

    @Column(length = 50)
    private String location;

    private Integer salaryMin;

    private Integer salaryMax;

    private Integer headcount;

    private LocalDate deadline;

    private LocalDate workStart;

    @Column(length = 500)
    private String skills;

    @Column(columnDefinition = "TEXT")
    private String intro;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(columnDefinition = "TEXT")
    private String preferred;

    @Column(columnDefinition = "TEXT")
    private String process;

    @Column(length = 150)
    private String contactEmail;

    @Column(length = 30)
    private String contactPhone;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean remotePossible;

    @Column(nullable = false)
    @ColumnDefault("true")
    private boolean publicOpen;

    @Column(nullable = false, length = 30)
    @ColumnDefault("'RECRUITING'")
    private String status;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int applicants;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int newApplicants;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int viewCount;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int favoriteCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

    @Version
    @ColumnDefault("0")
    private Long version;

    public void update(CompanyDto.RegReq dto) {
        this.title = dto.getTitle();
        this.category = dto.getCategory();
        this.employmentType = dto.getEmploymentType();
        this.experience = dto.getExperience();
        this.location = dto.getLocation();
        this.salaryMin = dto.getSalaryMin();
        this.salaryMax = dto.getSalaryMax();
        this.headcount = dto.getHeadcount();
        this.deadline = dto.getDeadline();
        this.workStart = dto.getWorkStart();
        this.skills = dto.normalizeSkills();
        this.intro = dto.getIntro();
        this.description = dto.getDescription();
        this.requirements = dto.getRequirements();
        this.preferred = dto.getPreferred();
        this.process = dto.getProcess();
        this.contactEmail = dto.getContactEmail();
        this.contactPhone = dto.getContactPhone();
        this.remotePossible = dto.isRemotePossible();
        this.publicOpen = dto.isPublicOpen();
    }

    public void closeRecruitment() {
        this.status = "CLOSED";
        this.newApplicants = 0;
    }

    public void openRecruitment() {
        this.status = "RECRUITING";
    }

    public void increaseApplicants() {
        this.applicants = this.applicants + 1;
    }

    public void increaseNewApplicants() {
        this.newApplicants = this.newApplicants + 1;
    }

    public void clearNewApplicants() {
        this.newApplicants = 0;
    }

    public void increaseViewCount() {
        this.viewCount = this.viewCount + 1;
    }

    public void increaseFavoriteCount() {
        this.favoriteCount = this.favoriteCount + 1;
    }

    public void decreaseFavoriteCount() {
        if (this.favoriteCount > 0) {
            this.favoriteCount = this.favoriteCount - 1;
        }
    }
}