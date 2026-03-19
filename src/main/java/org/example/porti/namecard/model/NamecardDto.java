package org.example.porti.namecard.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

public class NamecardDto {
    @Getter
    @Builder
    public static class SliceRes{
        private List<NamecardRes> namecardList;

        public static SliceRes toDto(Slice<Namecard> result){
            return SliceRes.builder()
                    .namecardList(result.get().map(NamecardRes::toDto).toList())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class NamecardRes {
        private Long idx;
        private Long userIdx;
        private String email;
        private String title;
        private String layout;
        private String color;
        private String affiliation;
        private String name;
        private String profileImage;
        private String description;
        private String career;
        private String url;
        private String address;
        private String phone;
        private List<String> keywords;

        public static NamecardRes toDto(Namecard entity){
            return NamecardRes.builder()
                    .idx(entity.getIdx())
                    .userIdx(entity.getUser().getIdx())
                    .email(entity.getUser().getEmail())
                    .color(entity.getColor())
                    .title(entity.getTitle())
                    .layout(entity.getLayout())
                    .description(entity.getDescription())
                    .affiliation(entity.getUser().getAffiliation())
                    .name(entity.getUser().getName())
                    .profileImage(entity.getUser().getProfileImage())
                    .career(entity.getUser().getCareer())
                    .url(entity.getUrl())
                    .phone(entity.getUser().getPhone())
                    .address(entity.getUser().getAddress())
                    .keywords(entity.getKeywords())
                    .build();
        }
    }


    @Getter
    @Builder
    public static class Register{
        private String title;
        private String layout;
        private String color;
        private String url;
        private String description;
        private List<String> keywords;

        public Namecard toEntity(){
            return Namecard.builder()
                    .title(this.title)
                    .layout(this.layout)
                    .color(this.color)
                    .url(this.url)
                    .description(this.description)
                    .keywords(this.keywords)
                    .build();
        }
    }
}
