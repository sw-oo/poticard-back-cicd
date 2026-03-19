package org.example.porti.company.favorite;

import org.example.porti.company.favorite.model.CompanyFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyFavoriteRepository extends JpaRepository<CompanyFavorite, Long> {
    Optional<CompanyFavorite> findByCompanyIdxAndUserIdx(Long companyIdx, Long userIdx);
    boolean existsByCompanyIdxAndUserIdx(Long companyIdx, Long userIdx);
    List<CompanyFavorite> findByUserIdxAndCompanyStatusAndCompanyPublicOpenTrue(Long userIdx, String status);
    void deleteByCompanyIdx(Long companyIdx);
}