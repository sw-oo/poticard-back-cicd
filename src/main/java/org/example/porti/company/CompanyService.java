package org.example.porti.company;

import lombok.RequiredArgsConstructor;
import org.example.porti.company.favorite.CompanyFavoriteRepository;
import org.example.porti.company.favorite.model.CompanyFavorite;
import org.example.porti.company.model.Company;
import org.example.porti.company.model.CompanyDto;
import org.example.porti.user.UserRepository;
import org.example.porti.user.model.AuthUserDetails;
import org.example.porti.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyFavoriteRepository companyFavoriteRepository;
    private final UserRepository userRepository;

    @Transactional
    public CompanyDto.RegRes register(AuthUserDetails user, CompanyDto.RegReq dto) {
        User loginUser = getRequiredUser(user);
        Company entity = companyRepository.save(dto.toEntity(AuthUserDetails.from(loginUser)));
        return CompanyDto.RegRes.from(entity);
    }

    @Transactional(readOnly = true)
    public CompanyDto.PageRes list(AuthUserDetails user, int page, int size) {
        User loginUser = getRequiredUser(user);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "idx"));
        Page<Company> result = companyRepository.findByUserIdxOrderByIdxDesc(loginUser.getIdx(), pageRequest);

        long totalCount = companyRepository.countByUserIdx(loginUser.getIdx());
        long recruitingCount = companyRepository.countByUserIdxAndStatus(loginUser.getIdx(), "RECRUITING");
        int totalApplicants = companyRepository.sumApplicantsByUserIdx(loginUser.getIdx());

        return CompanyDto.PageRes.from(result, totalCount, recruitingCount, totalApplicants);
    }

    @Transactional(readOnly = true)
    public CompanyDto.ReadRes read(AuthUserDetails user, Long idx) {
        Company company = findOwnedCompany(user, idx);
        return CompanyDto.ReadRes.from(company);
    }

    @Transactional
    public CompanyDto.RegRes update(AuthUserDetails user, Long idx, CompanyDto.RegReq dto) {
        Company company = findOwnedCompany(user, idx);
        company.update(dto);
        return CompanyDto.RegRes.from(company);
    }

    @Transactional
    public void delete(AuthUserDetails user, Long idx) {
        Company company = findOwnedCompany(user, idx);
        companyFavoriteRepository.deleteByCompanyIdx(idx);
        companyRepository.delete(company);
    }

    @Transactional
    public CompanyDto.RegRes close(AuthUserDetails user, Long idx) {
        Company company = findOwnedCompany(user, idx);
        company.closeRecruitment();
        return CompanyDto.RegRes.from(company);
    }

    @Transactional(readOnly = true)
    public List<CompanyDto.PublicListRes> publicList(AuthUserDetails user, String keyword, String category, boolean favoriteOnly, String sort) {
        List<Company> companies = companyRepository.findByPublicOpenTrueAndStatusOrderByIdxDesc("RECRUITING");
        Set<Long> favoriteIds = getFavoriteCompanyIds(user);
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase();

        return companies.stream()
                .filter(company -> matchKeyword(company, normalizedKeyword))
                .filter(company -> matchCategory(company, category))
                .filter(company -> !favoriteOnly || favoriteIds.contains(company.getIdx()))
                .sorted(resolveComparator(sort))
                .map(company -> CompanyDto.PublicListRes.from(company, favoriteIds.contains(company.getIdx())))
                .toList();
    }

    @Transactional
    public CompanyDto.PublicDetailRes publicRead(AuthUserDetails user, Long idx) {
        Company company = companyRepository.findById(idx)
                .orElseThrow(() -> new IllegalArgumentException("공고가 없습니다."));

        if (!company.isPublicOpen() || !"RECRUITING".equals(company.getStatus())) {
            throw new IllegalArgumentException("공개된 공고가 아닙니다.");
        }

        company.increaseViewCount();
        boolean favorite = isFavorite(user, company.getIdx());
        return CompanyDto.PublicDetailRes.from(company, favorite);
    }

    @Transactional
    public CompanyDto.FavoriteToggleRes toggleFavorite(AuthUserDetails user, Long idx) {
        User loginUser = getRequiredUser(user);
        Company company = companyRepository.findById(idx)
                .orElseThrow(() -> new IllegalArgumentException("공고가 없습니다."));

        if (!company.isPublicOpen() || !"RECRUITING".equals(company.getStatus())) {
            throw new IllegalArgumentException("공개된 공고만 즐겨찾기할 수 있습니다.");
        }

        return companyFavoriteRepository.findByCompanyIdxAndUserIdx(idx, loginUser.getIdx())
                .map(current -> {
                    companyFavoriteRepository.delete(current);
                    company.decreaseFavoriteCount();
                    return CompanyDto.FavoriteToggleRes.of(idx, false, company.getFavoriteCount());
                })
                .orElseGet(() -> {
                    companyFavoriteRepository.save(CompanyFavorite.builder()
                            .company(company)
                            .user(loginUser)
                            .build());
                    company.increaseFavoriteCount();
                    return CompanyDto.FavoriteToggleRes.of(idx, true, company.getFavoriteCount());
                });
    }

    @Transactional(readOnly = true)
    public List<CompanyDto.PublicListRes> recommend(AuthUserDetails user, int size) {
        Set<Long> favoriteIds = getFavoriteCompanyIds(user);

        return companyRepository.findByPublicOpenTrueAndStatusOrderByIdxDesc("RECRUITING").stream()
                .sorted(Comparator
                        .comparingInt((Company company) -> company.getFavoriteCount() * 3 + company.getViewCount())
                        .reversed()
                        .thenComparing(Company::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(size)
                .map(company -> CompanyDto.PublicListRes.from(company, favoriteIds.contains(company.getIdx())))
                .toList();
    }

    private Company findOwnedCompany(AuthUserDetails user, Long idx) {
        User loginUser = getRequiredUser(user);
        Company company = companyRepository.findById(idx)
                .orElseThrow(() -> new IllegalArgumentException("공고가 없습니다."));

        if (company.getUser() == null || !company.getUser().getIdx().equals(loginUser.getIdx())) {
            throw new IllegalArgumentException("본인 공고만 접근할 수 있습니다.");
        }

        return company;
    }

    private User getRequiredUser(AuthUserDetails user) {
        if (user == null || user.getIdx() == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        return userRepository.findById(user.getIdx())
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 없습니다."));
    }

    private Set<Long> getFavoriteCompanyIds(AuthUserDetails user) {
        if (user == null || user.getIdx() == null) {
            return Set.of();
        }
        return companyFavoriteRepository.findByUserIdxAndCompanyStatusAndCompanyPublicOpenTrue(user.getIdx(), "RECRUITING")
                .stream()
                .map(favorite -> favorite.getCompany().getIdx())
                .collect(Collectors.toSet());
    }

    private boolean isFavorite(AuthUserDetails user, Long companyIdx) {
        return user != null
                && user.getIdx() != null
                && companyFavoriteRepository.existsByCompanyIdxAndUserIdx(companyIdx, user.getIdx());
    }

    private boolean matchKeyword(Company company, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }

        return contains(company.getTitle(), keyword)
                || contains(company.getCategory(), keyword)
                || contains(company.getLocation(), keyword)
                || contains(company.getExperience(), keyword)
                || contains(company.getSkills(), keyword)
                || contains(company.getUser() != null ? company.getUser().getName() : null, keyword);
    }

    private boolean matchCategory(Company company, String category) {
        if (category == null || category.isBlank() || "ALL".equalsIgnoreCase(category)) {
            return true;
        }
        return category.equalsIgnoreCase(company.getCategory());
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private Comparator<Company> resolveComparator(String sort) {
        if ("views".equalsIgnoreCase(sort)) {
            return Comparator.comparingInt(Company::getViewCount).reversed()
                    .thenComparing(Company::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
        }
        if ("newest".equalsIgnoreCase(sort)) {
            return Comparator.comparing(Company::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
        }
        return Comparator.comparingInt((Company company) -> company.getFavoriteCount() * 2 + company.getViewCount())
                .reversed()
                .thenComparing(Company::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
    }
}