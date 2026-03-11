package org.example.porti.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.porti.namecard.model.Namecard;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String email; // required
    private String name; // required
    private String phone; // required for private, not for enterprise
    private String gender;
    private String address;
    private String profileImage;
    private String affiliation;
    private String career;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Setter
    private String password;
    @Setter
    private boolean enable;
    private String role;

    // 관계
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Namecard namecard;

    public void assignNamecard(Namecard namecard) {
        this.namecard = namecard;
    }

    public void updateNonEssential(UserDto.EditNonEssentialReq dto){
        if (StringUtils.hasText(dto.getAddress())) {
            this.address = dto.getAddress();
        }
        if (StringUtils.hasText(dto.getAffiliation())) {
            this.affiliation = dto.getAffiliation();
        }
        if (StringUtils.hasText(dto.getCareer())) {
            this.career = dto.getCareer();
        }
        if (StringUtils.hasText(dto.getProfile_image())) {
            this.profileImage = dto.getProfile_image();
        }
        if (StringUtils.hasText(dto.getGender())) {
            this.gender = dto.getGender();
        }
        if (StringUtils.hasText(dto.getName())) {
            this.name = dto.getName();
        }
        if (StringUtils.hasText(dto.getEmail())) {
            this.email = dto.getEmail();
        }
        if (StringUtils.hasText(dto.getPhone())) {
            this.phone = dto.getPhone();
        }
    }


}