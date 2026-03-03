package org.example.porti.namecard.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

public class NamecardDto {
    @Getter
    @Builder
    public static class SliceRes{
        private List<ListRes> namecardList;

        public static SliceRes toDto(Slice<Namecard> result){
            return SliceRes.builder()
                    .namecardList(result.get().map(NamecardDto.ListRes::toDto).toList())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ListRes{
        private Long idx;
        private String title;
        private String layout;
        private String color;
        private Long user;

        public static NamecardDto.ListRes toDto(Namecard entity){
            return ListRes.builder()
                    .idx(entity.getIdx())
                    .color(entity.getColor())
                    .title(entity.getTitle())
                    .layout(entity.getLayout())
                    .user(entity.getUser().getIdx())
                    .build();
        }
    }


    @Getter
    @Builder
    public static class Register{
        private String title;
        private String layout;
        private String color;

        public Namecard toEntity(){
            return Namecard.builder()
                    .title(this.title)
                    .layout(this.layout)
                    .color(this.color)
                    .build();
        }
    }
}
