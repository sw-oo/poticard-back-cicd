package org.example.porti.community.comment;

import org.example.porti.community.comment.model.CommunityComment;
import org.example.porti.community.model.Community;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {
    @Query("SELECT cc FROM CommunityComment cc LEFT JOIN FETCH cc.user u LEFT JOIN FETCH cc.community c WHERE c.idx = :communityIdx ORDER BY cc.idx ASC")
    List<CommunityComment> findByCommunityIdxOrderByIdxAsc(@Param("communityIdx") Long communityIdx);

    @Query("SELECT DISTINCT cm FROM CommunityComment c JOIN c.community cm LEFT JOIN FETCH cm.user u WHERE c.user.idx = :userIdx ORDER BY cm.idx DESC")
    List<Community> findDistinctCommunitiesByUserIdx(@Param("userIdx") Long userIdx);

    @Query("SELECT cc FROM CommunityComment cc LEFT JOIN FETCH cc.user u LEFT JOIN FETCH cc.community c WHERE cc.user.idx = :userIdx ORDER BY cc.idx DESC")
    List<CommunityComment> findTop10ByUserIdxOrderByIdxDesc(@Param("userIdx") Long userIdx, Pageable pageable);

    void deleteByCommunityIdx(Long communityIdx);
}