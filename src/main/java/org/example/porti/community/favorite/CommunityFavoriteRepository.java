package org.example.porti.community.favorite;

import org.example.porti.community.favorite.model.CommunityFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityFavoriteRepository extends JpaRepository<CommunityFavorite, Long> {
    Optional<CommunityFavorite> findByCommunityIdxAndUserIdx(Long communityIdx, Long userIdx);
    boolean existsByCommunityIdxAndUserIdx(Long communityIdx, Long userIdx);
    List<CommunityFavorite> findByUserIdx(Long userIdx);
    void deleteByCommunityIdx(Long communityIdx);
}