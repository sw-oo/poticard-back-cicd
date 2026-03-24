package org.example.porti.community.favorite;

import org.example.porti.community.favorite.model.CommunityFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommunityFavoriteRepository extends JpaRepository<CommunityFavorite, Long> {
    Optional<CommunityFavorite> findByCommunityIdxAndUserIdx(Long communityIdx, Long userIdx);
    boolean existsByCommunityIdxAndUserIdx(Long communityIdx, Long userIdx);
    List<CommunityFavorite> findByUserIdx(Long userIdx);

    @Query("SELECT cf.community.idx FROM CommunityFavorite cf WHERE cf.user.idx = :userIdx AND cf.community IS NOT NULL")
    List<Long> findFavoriteCommunityIdsByUserIdx(@Param("userIdx") Long userIdx);

    void deleteByCommunityIdx(Long communityIdx);
}