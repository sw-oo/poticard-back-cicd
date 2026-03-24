package org.example.porti.company.application;

import org.example.porti.company.application.model.CompanyApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompanyApplicationRepository extends JpaRepository<CompanyApplication, Long> {
    boolean existsByCompanyIdxAndUserIdx(Long companyIdx, Long userIdx);
    List<CompanyApplication> findByUserIdx(Long userIdx);
    Optional<CompanyApplication> findByCompanyIdxAndUserIdx(Long companyIdx, Long userIdx);

    @Query("""
            SELECT ca.company.idx
            FROM CompanyApplication ca
            JOIN ca.company c
            WHERE ca.user.idx = :userIdx
            """)
    List<Long> findAppliedCompanyIdsByUserIdx(@Param("userIdx") Long userIdx);

    @Query("""
            SELECT ca
            FROM CompanyApplication ca
            JOIN FETCH ca.user u
            LEFT JOIN FETCH u.namecard
            WHERE ca.company.idx = :companyIdx
            ORDER BY ca.createdAt DESC
            """)
    List<CompanyApplication> findApplicantsByCompanyIdx(@Param("companyIdx") Long companyIdx);
}