package org.example.porti.company;

import jakarta.persistence.LockModeType;
import org.example.porti.company.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Company c WHERE c.idx = :companyIdx")
    Optional<Company> findByIdWithLock(@Param("companyIdx") Long companyIdx);

    @Lock(LockModeType.OPTIMISTIC)
    Optional<Company> findByIdx(Long companyIdx);

    Page<Company> findByUserIdxOrderByIdxDesc(Long userIdx, Pageable pageable);

    long countByUserIdx(Long userIdx);

    long countByUserIdxAndStatus(Long userIdx, String status);

    @Query("SELECT COALESCE(SUM(c.applicants), 0) FROM Company c WHERE c.user.idx = :userIdx")
    int sumApplicantsByUserIdx(@Param("userIdx") Long userIdx);

    List<Company> findByPublicOpenTrueAndStatusOrderByIdxDesc(String status);

    List<Company> findByUserIdxOrderByIdxDesc(Long userIdx);
}