package org.example.porti.namecard.model;


import jakarta.persistence.*;
import lombok.*;
import org.example.porti.user.model.User;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
    public void update(String title, String color, String layout, String url, List<String> keywords) {
        this.title = title;
        this.color = color;
        this.layout = layout;
        this.url = url;
        this.keywords = keywords;
    }

}
