package org.example.porti.section.model;


import jakarta.persistence.*;
import lombok.*;
import org.example.porti.portfolio.model.Portfolio;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private String sectionTitle; // 섹션 제목

    @Column(columnDefinition = "TEXT")
    private String contents; // 섹션 내용(text 타입)
    private Integer sectionOrder; // 섹션 순서
    @Builder.Default
    private boolean isVisible = true; // 섹션 노출 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_idx")
    private Portfolio portfolio;
}
