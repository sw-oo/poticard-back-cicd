package org.example.porti.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.porti.namecard.model.Namecard;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="namecard_idx")
    private Namecard namecard;


}