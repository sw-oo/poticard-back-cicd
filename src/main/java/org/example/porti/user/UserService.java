package org.example.porti.user;


import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.porti.common.exception.BaseException;
import org.example.porti.user.model.AuthUserDetails;
import org.example.porti.user.model.EmailVerify;
import org.example.porti.user.model.User;
import org.example.porti.user.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static org.example.porti.common.model.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailVerifyRepository emailVerifyRepository;

    public UserDto.SignupRes signup(UserDto.SignupReq dto) {

        // 이메일 중복 확인
        if(userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw BaseException.from(SIGNUP_DUPLICATE_EMAIL);
        }


        User user = dto.toEntity();
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);

        // 메일 전송
        String uuid = UUID.randomUUID().toString();
        emailService.sendWelcomeMail(uuid, dto.getEmail());

        // 메일 전송 내역 저장
        EmailVerify emailVerify = EmailVerify.builder().email(dto.getEmail()).uuid(uuid).build();
        emailVerifyRepository.save(emailVerify);

        return UserDto.SignupRes.from(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(
                () -> BaseException.from(LOGIN_INVALID_USERINFO)
        );

        return AuthUserDetails.from(user);
    }

    public UserDto.SignupRes companySignup(UserDto.SignupReq dto) {
        // 이메일 중복 확인
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw BaseException.from(SIGNUP_DUPLICATE_EMAIL);
        }
        User user = dto.toCompanyEntity();
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

    public void verify(String uuid) {
        EmailVerify emailVerify = emailVerifyRepository.findByUuid(uuid).orElseThrow(
                ()->BaseException.from(SIGNUP_INVALID_UUID));
        User user = userRepository.findByEmail(emailVerify.getEmail()).orElseThrow();
        user.setEnable(true);
        userRepository.save(user);
    }

    public UserDto.MyInfo me(Long idx) {
        return UserDto.MyInfo.from(userRepository.findById(idx).orElseThrow());
    }

    public Boolean exists(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()) {
            return true;
        }else {
            return false;
        }
    }
}