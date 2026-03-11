package org.example.porti.community;

import org.example.porti.community.model.Community;
import org.example.porti.community.model.CommunityDto;
import org.example.porti.user.model.AuthUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CommunityService {
    private final CommunityRepository communityRepository;

    @Transactional
    public CommunityDto.RegRes register(AuthUserDetails user, CommunityDto.RegReq dto) {
        Community entity = communityRepository.save(dto.toEntity(user));
        return CommunityDto.RegRes.from(entity);
    }

    @Transactional(readOnly = true)
    public CommunityDto.PageRes list(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Community> result = communityRepository.findAll(pageRequest);

        return CommunityDto.PageRes.from(result);
    }

    @Transactional(readOnly = true)
    public CommunityDto.ReadRes read(Long idx) {
        Community community = communityRepository.findById(idx)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        return CommunityDto.ReadRes.from(community);
    }

    @Transactional
    public CommunityDto.RegRes update(Long idx, CommunityDto.RegReq dto) {
        Community community = communityRepository.findById(idx)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        community.update(dto);
        communityRepository.save(community);

        return CommunityDto.RegRes.from(community);
    }

    @Transactional
    public void delete(Long idx) {
        communityRepository.deleteById(idx);
    }
}