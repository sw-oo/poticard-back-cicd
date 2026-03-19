package org.example.porti.community;

import jakarta.persistence.LockModeType;
import org.example.porti.community.model.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Community c WHERE c.idx = :communityIdx")
    Optional<Community> findByIdWithLock(@Param("communityIdx") Long communityIdx);

    @Lock(LockModeType.OPTIMISTIC)
    Optional<Community> findByIdx(Long communityIdx);

    Page<Community> findAllByOrderByIdxDesc(Pageable pageable);

    Page<Community> findByUserIdxOrderByIdxDesc(Long userIdx, Pageable pageable);

    List<Community> findTop5ByOrderByLikesCountDescCommentCountDescViewCountDescUpdatedAtDesc();

    List<Community> findTop10ByUserIdxOrderByIdxDesc(Long userIdx);
}