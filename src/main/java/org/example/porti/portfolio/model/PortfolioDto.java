package org.example.porti.portfolio.model;

import lombok.*;
import org.example.porti.section.model.Section;
import org.example.porti.section.model.SectionDto;

import java.util.ArrayList;
import java.util.List;

public class PortfolioDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Req {
        private String title;
        private String period;
        private String role;
        private List<SectionDto.Req> sectionList;
        private String accentColor;
        private String fontFamily;
        private String layoutType;

        public Portfolio toEntity(String imageUrl) {
            return Portfolio.builder()
                    .title(this.title)
                    .period(this.period)
                    .role(this.role)
                    .accentColor(this.accentColor)
                    .fontFamily(this.fontFamily)
                    .layoutType(this.layoutType)
                    .Image(imageUrl)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Res {
        private Long idx;
        private String title;
        private String period;
        private String role;
        private List<String> keywords;

        private String accentColor;
        private String fontFamily;
        private String layoutType;
        private String Image;

        private List<SectionDto.Res> sectionList;

        public static Res from(Portfolio entity) {
            List<SectionDto.Res> sectionDto = new ArrayList<>();

            return Res.builder()
                    .idx(entity.getIdx())
                    .title(entity.getTitle())
                    .period(entity.getPeriod())
                    .role(entity.getRole())
                    .keywords(entity.getKeywords())
                    .accentColor(entity.getAccentColor())
                    .fontFamily(entity.getFontFamily())
                    .layoutType(entity.getLayoutType())
                    .Image(entity.getImage())
                    .sectionList(entity.getSectionList().stream().map(SectionDto.Res::from).toList())
                    .build();
        }
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class portRes {
        private Long idx;
        private String title;
        private String Image;

        public static portRes from(Portfolio entity) {
            return portRes.builder()
                    .idx(entity.getIdx())
                    .title(entity.getTitle())
                    .Image(entity.getImage())
                    .build();
        }
    }
}
