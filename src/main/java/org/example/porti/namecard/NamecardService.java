package org.example.porti.namecard;

import lombok.RequiredArgsConstructor;
import org.example.porti.namecard.model.Namecard;
import org.example.porti.namecard.model.NamecardDto;
import org.example.porti.upload.CloudUploadService;
import org.example.porti.user.UserRepository;
import org.example.porti.user.model.AuthUserDetails;
import org.example.porti.user.model.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class NamecardService {
    private final NamecardRepository namecardRepository;
    private final UserRepository userRepository;
    private final CloudUploadService cloudUploadService;

    public NamecardDto.SliceRes list(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Slice<Namecard> result = namecardRepository.findAll(pageRequest);

        return NamecardDto.SliceRes.toDto(result);
    }

//    public List<NamecardDto.NamecardRes> list(){
//        List<Namecard> namecardList = namecardRepository.findAll();
//        return namecardList.stream().map(NamecardDto.NamecardRes::toDto).toList();
//    }

    @Transactional
    public void reg(NamecardDto.Register dto, AuthUserDetails user) {
        User userEntity = userRepository.findById(user.getIdx()).orElseThrow();
        namecardRepository.findByUserIdx(user.getIdx())
                .ifPresentOrElse(
                        currentNamecard ->{
                            currentNamecard.update(dto);
//                            currentNamecard.setUser(userEntity);
                        },
                        () -> {
                            Namecard newNamecard = new Namecard();
                            newNamecard.update(dto);
                            newNamecard.setUser(userEntity);
                            namecardRepository.save(newNamecard);
                            userEntity.assignNamecard(newNamecard);
                        }
                );
    }

    @Transactional(readOnly = true)
    public NamecardDto.NamecardRes singleUser(Long userId) {
        Optional<Namecard> result = namecardRepository.findByUserIdx(userId);
        if (result.isEmpty()) {
            return null;
        }
        return NamecardDto.NamecardRes.toDto(result.get());
    }

    public Long amount() {
        return namecardRepository.count();
    }

    public void upload(MultipartFile file, AuthUserDetails user) throws SQLException, IOException {
        String filename = cloudUploadService.saveFile(file);
        User entity = userRepository.findById(user.getIdx()).orElseThrow();
        entity.setProfileImage(filename);
        userRepository.save(entity);
    }
}
