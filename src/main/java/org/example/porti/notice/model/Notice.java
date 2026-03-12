package org.example.porti.notice.model;


import jakarta.persistence.*;
import lombok.*;
import org.example.porti.common.model.BaseEntity;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Notice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String title; // 공지사항 제목
    @Column(columnDefinition = "TEXT")
    private String content; // 공지사항 내용
}
