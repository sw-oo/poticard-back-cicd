package org.example.porti.namecard;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.porti.namecard.model.Namecard;
import org.example.porti.namecard.model.NamecardDto;
import org.example.porti.user.UserRepository;
import org.example.porti.user.model.AuthUserDetails;
import org.example.porti.user.model.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class NamecardService {
    private final NamecardRepository namecardRepository;
    private final UserRepository userRepository;

    public NamecardDto.SliceRes list(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Slice<Namecard> result = namecardRepository.findAll(pageRequest);

        return NamecardDto.SliceRes.toDto(result);
    }

    @Transactional
    public void reg(NamecardDto.Register dto, AuthUserDetails user) {
        Long userIdx = user.getIdx();

        Namecard namecard = namecardRepository.findByUserIdx(userIdx).orElseGet(()->{
            User userEntity = userRepository.findById(userIdx)
                    .orElseThrow(()->new EntityNotFoundException("사용자를 찾을 수 없습니다."));
            return Namecard.builder().user(userEntity).build();
        });

        namecard.update(dto);

//        namecardRepository.save(namecard);
    }

    public NamecardDto.NamecardRes singleUser(Long userId) {
        Namecard result = namecardRepository.findByUserIdx(userId).orElseThrow(()->new EntityNotFoundException("지정한 사용자를 찾을 수 없습니다."));
        return NamecardDto.NamecardRes.toDto(result);
    }
}
