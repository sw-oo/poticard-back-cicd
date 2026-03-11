package org.example.porti.namecard.model;


import jakarta.persistence.*;
import lombok.*;
import org.example.porti.user.model.User;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Namecard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String title;
    private String layout;
    private String color;
    private String url;
    private List<String> keywords;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_idx")
    private User user;

    // Namecard 엔티티 내부
    public void update(NamecardDto.Register dto) {
        if (StringUtils.hasText(dto.getTitle())) {
            this.title = dto.getTitle();
        }
        if (StringUtils.hasText(dto.getColor())) {
            this.color = dto.getColor();
        }
        if (StringUtils.hasText(dto.getLayout())) { // layout이 Enum 타입이라면 dto.getLayout() != null 로 변경하세요
            this.layout = dto.getLayout();
        }
        if (StringUtils.hasText(dto.getUrl())) {
            this.url = dto.getUrl();
        }

        if (dto.getKeywords() != null && !dto.getKeywords().isEmpty()) {
            this.keywords = dto.getKeywords();
        }

    }

}
