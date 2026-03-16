package org.example.porti.user;

import lombok.RequiredArgsConstructor;
import org.example.porti.user.model.AuthUserDetails;
import org.example.porti.user.model.User;
import org.example.porti.user.model.UserDto;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2User oAuth2User = super.loadUser(userRequest);

        UserDto.OAuth dto = UserDto.OAuth.from(oAuth2User.getAttributes(), provider);

        Optional<User> result = userRepository.findByEmail(dto.getEmail());
        if (!result.isPresent()) {
            User user = userRepository.save(dto.toEntity());
            return AuthUserDetails.from(user);
        }
        else{
            User user = result.get();
            return AuthUserDetails.from(user);
        }

    }
}
