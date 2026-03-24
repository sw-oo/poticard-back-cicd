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

    @Query(
            value = "SELECT c FROM Community c LEFT JOIN FETCH c.user u ORDER BY c.idx DESC",
            countQuery = "SELECT COUNT(c) FROM Community c"
    )
    Page<Community> findAllWithUserOrderByIdxDesc(Pageable pageable);

    @Query(
            value = "SELECT c FROM Community c LEFT JOIN FETCH c.user u WHERE c.user.idx = :userIdx ORDER BY c.idx DESC",
            countQuery = "SELECT COUNT(c) FROM Community c WHERE c.user.idx = :userIdx"
    )
    Page<Community> findByUserIdxWithUserOrderByIdxDesc(@Param("userIdx") Long userIdx, Pageable pageable);

    @Query("SELECT c FROM Community c LEFT JOIN FETCH c.user u WHERE c.idx = :idx")
    Optional<Community> findByIdWithUser(@Param("idx") Long idx);

    @Query("SELECT c FROM Community c LEFT JOIN FETCH c.user u ORDER BY c.likesCount DESC, c.commentCount DESC, c.viewCount DESC, c.updatedAt DESC")
    List<Community> findTop5WithUserOrderByHot();

    @Query("SELECT c FROM Community c LEFT JOIN FETCH c.user u WHERE c.user.idx = :userIdx ORDER BY c.idx DESC")
    List<Community> findTop10ByUserIdxWithUserOrderByIdxDesc(@Param("userIdx") Long userIdx, Pageable pageable);
}