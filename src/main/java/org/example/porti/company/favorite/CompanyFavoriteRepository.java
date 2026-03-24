package org.example.porti.company.favorite;

import org.example.porti.company.favorite.model.CompanyFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompanyFavoriteRepository extends JpaRepository<CompanyFavorite, Long> {
    Optional<CompanyFavorite> findByCompanyIdxAndUserIdx(Long companyIdx, Long userIdx);
    boolean existsByCompanyIdxAndUserIdx(Long companyIdx, Long userIdx);

    @Query("""
            SELECT c.idx
            FROM CompanyFavorite cf
            JOIN cf.company c
            WHERE cf.user.idx = :userIdx
              AND c.publicOpen = true
            """)
    List<Long> findFavoriteCompanyIdsByUserIdx(@Param("userIdx") Long userIdx);

    void deleteByCompanyIdx(Long companyIdx);
}