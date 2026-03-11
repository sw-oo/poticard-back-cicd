package org.example.porti.community;

import org.example.porti.community.model.Community;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Community c WHERE c.idx = :communityIdx")
    Optional<Community> findByIdWithLock(@Param("communityIdx") Long communityIdx);

    @Lock(LockModeType.OPTIMISTIC)
    Optional<Community> findByIdx(Long communityIdx);
}