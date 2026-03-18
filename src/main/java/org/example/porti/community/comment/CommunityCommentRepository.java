package org.example.porti.community.comment;

import org.example.porti.community.comment.model.CommunityComment;
import org.example.porti.community.model.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {
    List<CommunityComment> findByCommunityIdxOrderByIdxAsc(Long communityIdx);

    @Query("SELECT DISTINCT c.community FROM CommunityComment c WHERE c.user.idx = :userIdx ORDER BY c.community.idx DESC")
    List<Community> findDistinctCommunitiesByUserIdx(@Param("userIdx") Long userIdx);

    void deleteByCommunityIdx(Long communityIdx);
}