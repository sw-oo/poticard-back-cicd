package org.example.porti.portfolio.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.porti.section.model.Section;
import org.example.porti.utils.StringListConverter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String title; // 프로젝트 제목
    private String period; // 진행 기간
    private String role; // 프로젝트에서 맡은 역할
    @Convert(converter = StringListConverter.class)
    private List<String> keywords; // 프로젝트의 키워드

    private String accentColor; // 글씨 강조 색상
    private String fontFamily;  // 글꼴
    private String layoutType;  // 레이아웃

    @OneToMany(mappedBy = "portfolio", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Section> sectionList = new ArrayList<>();

}
