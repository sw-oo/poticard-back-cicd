package org.example.porti.user.model;


import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.Map;

import static org.example.porti.common.Constants.DEFAULT_COMPANY_ROLE;
import static org.example.porti.common.Constants.DEFAULT_USER_ROLE;

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
                    .role(DEFAULT_USER_ROLE)
                    .build();
        }

        public User toCompanyEntity() {
            return User.builder()
                    .email(this.email)
                    .name(this.name)
                    .password(this.password)
                    .phone(this.phone)
                    .enable(true)
                    .role(DEFAULT_COMPANY_ROLE)
                    .build();
        }
    }


    @Builder
    @Getter
    public static class SignupRes {
        private Long idx;
        private String email;
        private String name;
        private String role;

        public static SignupRes from(User entity) {
            return SignupRes.builder()
                    .idx(entity.getIdx())
                    .email(entity.getEmail())
                    .name(entity.getName())
                    .role(entity.getRole())
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

        public static LoginRes from(AuthUserDetails entity) {
            return LoginRes.builder()
                    .idx(entity.getIdx())
                    .email(entity.getUsername())
                    .name(entity.getNickname())
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

    @Getter
    @Builder
    public static class OAuth {
        private String email;
        private String name;
        private String provider;
        private String phone;
        private boolean enable;
        private String role;

        public static OAuth from(Map<String, Object> attributes, String provider) {
            String providerId = null;
            String email = null;
            Map properties = null;
            String name = null;
            String phone = null;

            if (provider.equals("kakao")){
                providerId = ((Long) attributes.get("id")).toString();
                email = providerId + "@kakao.social";
                properties = (Map) attributes.get("properties");
                name = (String) properties.get("nickname");
                phone = (String) properties.get("phone");
            } else if (provider.equals("google")){
                email = (String) attributes.get("email");
                name = (String) attributes.get("name");
                phone = (String) attributes.get("phone");
            }
            return OAuth.builder()
                    .email(email)
                    .name(name)
                    .provider(provider)
                    .phone(phone)
                    .enable(true)
                    .role(DEFAULT_USER_ROLE)
                    .build();
        }

        public User toEntity() {
            return User.builder()
                    .email(this.email)
                    .name(this.name)
                    .phone(this.phone)
                    .enable(this.enable)
                    .role(this.role)
                    .build();
        }
    }
}