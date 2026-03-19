package org.example.porti.portfolio.model;

import lombok.*;
import org.example.porti.section.model.Section;
import org.example.porti.section.model.SectionDto;
import org.example.porti.user.model.User;

import java.text.SimpleDateFormat;
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
        private String theme;
        private String layoutType;

        public Portfolio toEntity(String imageUrl, User user) {
            return Portfolio.builder()
                    .title(this.title)
                    .period(this.period)
                    .role(this.role)
                    .theme(this.theme)
                    .layoutType(this.layoutType)
                    .Image(imageUrl)
                    .user(user)
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

        private String theme;
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
                    .theme(entity.getTheme())
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
        private String role;
        private String Image;
        private String createdAt;

        public static portRes from(Portfolio entity) {
            String formattedDate = entity.getCreatedAt() != null ?
                    new SimpleDateFormat("yyyy.MM.dd").format(entity.getCreatedAt()) : "";
            return portRes.builder()
                    .idx(entity.getIdx())
                    .title(entity.getTitle())
                    .role(entity.getRole())
                    .Image(entity.getImage())
                    .createdAt(formattedDate)
                    .build();
        }
    }
}
