package org.example.porti.user;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.porti.common.exception.BaseException;
import org.example.porti.namecard.model.Namecard;
import org.example.porti.user.model.AuthUserDetails;
import org.example.porti.user.model.User;
import org.example.porti.user.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import static org.example.porti.common.model.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto.SignupRes signup(UserDto.SignupReq dto) {

        // 이메일 중복 확인
        if(userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw BaseException.from(SIGNUP_DUPLICATE_EMAIL);
        }


        User user = dto.toEntity();
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);


        return UserDto.SignupRes.from(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(
                () -> BaseException.from(LOGIN_INVALID_USERINFO)
        );

        return AuthUserDetails.from(user);
    }

    public UserDto.SignupRes enterpriseSignup(UserDto.SignupReq dto) {
        // 이메일 중복 확인
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw BaseException.from(SIGNUP_DUPLICATE_EMAIL);
        }
        User user = dto.toEnterpriseEntity();
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
        return UserDto.SignupRes.from(user);
    }

    @Transactional
    public void editNonEssential(UserDto.EditNonEssentialReq dto, AuthUserDetails user) {
        User update = userRepository.findById(user.getIdx()).orElseThrow();
        update.updateNonEssential(dto);
//        log.debug(update.getIdx().toString());
//        log.debug(update.getAddress());
//        userRepository.save(update);
    }

}