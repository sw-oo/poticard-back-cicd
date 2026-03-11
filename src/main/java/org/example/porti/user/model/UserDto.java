package org.example.porti.user.model;


import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import javax.swing.text.html.parser.Entity;

public class UserDto {

    @Getter
    public static class SignupReq {
        private String email;
        private String name;
        private String phone;
        private String password;

        public User toEntity() {
            return User.builder()
                    .email(this.email)
                    .name(this.name)
                    .password(this.password)
                    .phone(this.phone)
                    .enable(false)
                    .role("ROLE_USER")
                    .build();
        }

        public User toEnterpriseEntity() {
            return User.builder()
                    .email(this.email)
                    .name(this.name)
                    .password(this.password)
                    .phone(this.phone)
                    .enable(true)
                    .role("ROLE_ENTERPRISE")
                    .build();
        }
    }


    @Builder
    @Getter
    public static class SignupRes {
        private Long idx;
        private String email;
        private String name;

        public static SignupRes from(User entity) {
            return SignupRes.builder()
                    .idx(entity.getIdx())
                    .email(entity.getEmail())
                    .name(entity.getName())
                    .build();
        }
    }

    @Getter
    public static class LoginReq {
        private String email;
        private String password;
    }

    @Builder
    @Getter
    public static class LoginRes {
        private Long idx;
        private String email;
        private String name;

        public static LoginRes from(User entity) {
            return LoginRes.builder()
                    .idx(entity.getIdx())
                    .email(entity.getEmail())
                    .name(entity.getName())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class EditNonEssentialReq {
        private String email;
        private String name;
        private String phone;
        private String address;
        private String affiliation;
        private String career;
        private String gender;
        private String profile_image;
    }

}