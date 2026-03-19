package org.example.porti.community;

import lombok.RequiredArgsConstructor;
import org.example.porti.community.comment.CommunityCommentRepository;
import org.example.porti.community.comment.model.CommunityComment;
import org.example.porti.community.comment.model.CommunityCommentDto;
import org.example.porti.community.favorite.CommunityFavoriteRepository;
import org.example.porti.community.favorite.model.CommunityFavorite;
import org.example.porti.community.model.Community;
import org.example.porti.community.model.CommunityDto;
import org.example.porti.namecard.NamecardRepository;
import org.example.porti.namecard.model.NamecardDto;
import org.example.porti.user.UserRepository;
import org.example.porti.user.model.AuthUserDetails;
import org.example.porti.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final CommunityFavoriteRepository communityFavoriteRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final UserRepository userRepository;
    private final NamecardRepository namecardRepository;

    @Transactional
    public CommunityDto.RegRes register(AuthUserDetails user, CommunityDto.RegReq dto) {
        User loginUser = getRequiredUser(user);
        Community entity = communityRepository.save(dto.toEntity(AuthUserDetails.from(loginUser)));
        return CommunityDto.RegRes.from(entity);
    }

    @Transactional(readOnly = true)
    public CommunityDto.PageRes list(AuthUserDetails user, int page, int size, String scope) {
        Long loginUserIdx = user != null ? user.getIdx() : null;
        Set<Long> favoriteIds = getFavoriteIds(user);
        PageRequest pageRequest = PageRequest.of(page, size);

        if ("MY_POST".equalsIgnoreCase(scope)) {
            User loginUser = getRequiredUser(user);
            Page<Community> result = communityRepository.findByUserIdxOrderByIdxDesc(loginUser.getIdx(), pageRequest);
            return CommunityDto.PageRes.from(result, loginUserIdx, favoriteIds);
        }

        if ("MY_COMMENT".equalsIgnoreCase(scope)) {
            User loginUser = getRequiredUser(user);
            List<Community> result = communityCommentRepository.findDistinctCommunitiesByUserIdx(loginUser.getIdx());
            return CommunityDto.PageRes.from(result, page, size, loginUserIdx, favoriteIds);
        }

        Page<Community> result = communityRepository.findAllByOrderByIdxDesc(pageRequest);
        return CommunityDto.PageRes.from(result, loginUserIdx, favoriteIds);
    }

    @Transactional
    public CommunityDto.ReadRes read(AuthUserDetails user, Long idx) {
        Community community = communityRepository.findById(idx)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        community.increaseViewCount();
        Long loginUserIdx = user != null ? user.getIdx() : null;
        boolean favorite = isFavorite(user, idx);
        List<CommunityCommentDto.CommentRes> comments = communityCommentRepository.findByCommunityIdxOrderByIdxAsc(idx)
                .stream()
                .map(comment -> CommunityCommentDto.CommentRes.from(comment, loginUserIdx))
                .toList();

        return CommunityDto.ReadRes.from(community, loginUserIdx, favorite, comments);
    }

    @Transactional
    public CommunityDto.RegRes update(AuthUserDetails user, Long idx, CommunityDto.RegReq dto) {
        Community community = findOwnedCommunity(user, idx);
        community.update(dto);
        return CommunityDto.RegRes.from(community);
    }

    @Transactional
    public void delete(AuthUserDetails user, Long idx) {
        Community community = findOwnedCommunity(user, idx);
        communityCommentRepository.deleteByCommunityIdx(idx);
        communityFavoriteRepository.deleteByCommunityIdx(idx);
        communityRepository.delete(community);
    }

    @Transactional
    public CommunityDto.FavoriteToggleRes toggleFavorite(AuthUserDetails user, Long idx) {
        User loginUser = getRequiredUser(user);
        Community community = communityRepository.findById(idx)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        return communityFavoriteRepository.findByCommunityIdxAndUserIdx(idx, loginUser.getIdx())
                .map(current -> {
                    communityFavoriteRepository.delete(current);
                    community.decreaseLikesCount();
                    return CommunityDto.FavoriteToggleRes.of(idx, false, community.getLikesCount());
                })
                .orElseGet(() -> {
                    communityFavoriteRepository.save(CommunityFavorite.builder()
                            .community(community)
                            .user(loginUser)
                            .build());
                    community.increaseLikesCount();
                    return CommunityDto.FavoriteToggleRes.of(idx, true, community.getLikesCount());
                });
    }

    @Transactional(readOnly = true)
    public CommunityDto.SummaryRes summary(AuthUserDetails user) {
        User loginUser = getRequiredUser(user);
        Long loginUserIdx = loginUser.getIdx();
        Set<Long> favoriteIds = getFavoriteIds(user);

        CommunityDto.ProfileRes profile = CommunityDto.ProfileRes.from(loginUser);
        NamecardDto.NamecardRes namecard = namecardRepository.findByUserIdx(loginUserIdx)
                .map(NamecardDto.NamecardRes::toDto)
                .orElse(null);

        List<CommunityDto.ListRes> popularPosts = communityRepository.findTop5ByOrderByLikesCountDescCommentCountDescViewCountDescUpdatedAtDesc()
                .stream()
                .map(entity -> CommunityDto.ListRes.from(entity, loginUserIdx, favoriteIds.contains(entity.getIdx())))
                .toList();

        List<CommunityDto.ListRes> myPosts = communityRepository.findTop10ByUserIdxOrderByIdxDesc(loginUserIdx)
                .stream()
                .map(entity -> CommunityDto.ListRes.from(entity, loginUserIdx, favoriteIds.contains(entity.getIdx())))
                .toList();

        List<CommunityCommentDto.CommentRes> myComments = communityCommentRepository.findAll().stream()
                .filter(comment -> comment.getUser() != null && loginUserIdx.equals(comment.getUser().getIdx()))
                .sorted((a, b) -> Long.compare(b.getIdx(), a.getIdx()))
                .limit(10)
                .map(comment -> CommunityCommentDto.CommentRes.from(comment, loginUserIdx))
                .toList();

        return CommunityDto.SummaryRes.of(profile, namecard, popularPosts, myPosts, myComments);
    }

    @Transactional(readOnly = true)
    public List<CommunityCommentDto.CommentRes> listComments(AuthUserDetails user, Long communityIdx) {
        Long loginUserIdx = user != null ? user.getIdx() : null;
        return communityCommentRepository.findByCommunityIdxOrderByIdxAsc(communityIdx)
                .stream()
                .map(comment -> CommunityCommentDto.CommentRes.from(comment, loginUserIdx))
                .toList();
    }

    @Transactional
    public CommunityCommentDto.CommentRes registerComment(AuthUserDetails user, Long communityIdx, CommunityCommentDto.RegReq dto) {
        User loginUser = getRequiredUser(user);
        Community community = communityRepository.findById(communityIdx)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        CommunityComment comment = communityCommentRepository.save(CommunityComment.builder()
                .contents(dto.getContents())
                .community(community)
                .user(loginUser)
                .build());

        community.increaseCommentCount();
        if ("QNA".equalsIgnoreCase(community.getCategory()) && community.getUser() != null && !community.getUser().getIdx().equals(loginUser.getIdx())) {
            community.completeSolved();
        }

        return CommunityCommentDto.CommentRes.from(comment, loginUser.getIdx());
    }

    @Transactional
    public void deleteComment(AuthUserDetails user, Long commentIdx) {
        User loginUser = getRequiredUser(user);
        CommunityComment comment = communityCommentRepository.findById(commentIdx)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 없습니다."));

        if (comment.getUser() == null || !loginUser.getIdx().equals(comment.getUser().getIdx())) {
            throw new IllegalArgumentException("본인 댓글만 삭제할 수 있습니다.");
        }

        Community community = comment.getCommunity();
        communityCommentRepository.delete(comment);
        if (community != null) {
            community.decreaseCommentCount();
        }
    }

    private Community findOwnedCommunity(AuthUserDetails user, Long idx) {
        User loginUser = getRequiredUser(user);
        Community community = communityRepository.findById(idx)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        if (community.getUser() == null || !loginUser.getIdx().equals(community.getUser().getIdx())) {
            throw new IllegalArgumentException("본인 글만 수정/삭제할 수 있습니다.");
        }

        return community;
    }

    private User getRequiredUser(AuthUserDetails user) {
        if (user == null || user.getIdx() == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        return userRepository.findById(user.getIdx())
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 없습니다."));
    }

    private Set<Long> getFavoriteIds(AuthUserDetails user) {
        if (user == null || user.getIdx() == null) {
            return Set.of();
        }
        return communityFavoriteRepository.findByUserIdx(user.getIdx())
                .stream()
                .map(favorite -> favorite.getCommunity().getIdx())
                .collect(Collectors.toSet());
    }

    private boolean isFavorite(AuthUserDetails user, Long communityIdx) {
        return user != null
                && user.getIdx() != null
                && communityFavoriteRepository.existsByCommunityIdxAndUserIdx(communityIdx, user.getIdx());
    }
}