package org.example.porti.company;

import lombok.RequiredArgsConstructor;
import org.example.porti.company.application.CompanyApplicationRepository;
import org.example.porti.company.application.model.CompanyApplication;
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
    private final CompanyApplicationRepository companyApplicationRepository;
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
        List<Company> companies = companyRepository.findPublicOpenListOrderByIdxDesc();
        Set<Long> favoriteIds = getFavoriteCompanyIds(user);
        Set<Long> appliedIds = getAppliedCompanyIds(user);
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
        Long loginUserIdx = user != null ? user.getIdx() : null;

        return companies.stream()
                .filter(this::isVisiblePublicCompany)
                .filter(company -> matchKeyword(company, normalizedKeyword))
                .filter(company -> matchCategory(company, category))
                .filter(company -> !favoriteOnly || favoriteIds.contains(company.getIdx()))
                .sorted(resolvePublicComparator(sort, loginUserIdx))
                .map(company -> CompanyDto.PublicListRes.from(
                        company,
                        favoriteIds.contains(company.getIdx()),
                        appliedIds.contains(company.getIdx()),
                        isMine(company, loginUserIdx)
                ))
                .toList();
    }

    @Transactional
    public CompanyDto.PublicDetailRes publicRead(AuthUserDetails user, Long idx) {
        Company company = companyRepository.findByIdWithUser(idx)
                .orElseThrow(() -> new IllegalArgumentException("공고가 없습니다."));

        if (!isVisiblePublicCompany(company)) {
            throw new IllegalArgumentException("공개된 공고가 아닙니다.");
        }

        company.increaseViewCount();

        boolean favorite = isFavorite(user, company.getIdx());
        boolean applied = isApplied(user, company.getIdx());

        return CompanyDto.PublicDetailRes.from(company, favorite, applied, isMine(company, user != null ? user.getIdx() : null));
    }

    @Transactional
    public CompanyDto.FavoriteToggleRes toggleFavorite(AuthUserDetails user, Long idx) {
        User loginUser = getRequiredUser(user);
        Company company = companyRepository.findByIdWithUser(idx)
                .orElseThrow(() -> new IllegalArgumentException("공고가 없습니다."));

        if (!isVisiblePublicCompany(company)) {
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

    @Transactional
    public CompanyDto.ApplyRes apply(AuthUserDetails user, Long idx) {
        User loginUser = getRequiredUser(user);
        Company company = companyRepository.findByIdWithUser(idx)
                .orElseThrow(() -> new IllegalArgumentException("공고가 없습니다."));

        if (!isVisiblePublicCompany(company)) {
            throw new IllegalArgumentException("지원 가능한 공개 공고가 아닙니다.");
        }

        if (company.getUser() != null && company.getUser().getIdx().equals(loginUser.getIdx())) {
            throw new IllegalArgumentException("본인 공고에는 지원할 수 없습니다.");
        }

        if (companyApplicationRepository.existsByCompanyIdxAndUserIdx(idx, loginUser.getIdx())) {
            throw new IllegalArgumentException("이미 지원한 공고입니다.");
        }

        companyApplicationRepository.save(CompanyApplication.builder()
                .company(company)
                .user(loginUser)
                .build());

        company.increaseApplicants();
        company.increaseNewApplicants();

        return CompanyDto.ApplyRes.of(
                company.getIdx(),
                true,
                company.getApplicants(),
                company.getNewApplicants()
        );
    }

    @Transactional
    public CompanyDto.ApplyRes cancelApply(AuthUserDetails user, Long idx) {
        User loginUser = getRequiredUser(user);

        CompanyApplication application = companyApplicationRepository.findByCompanyIdxAndUserIdx(idx, loginUser.getIdx())
                .orElseThrow(() -> new IllegalArgumentException("지원한 공고가 없습니다."));

        Company company = application.getCompany();

        companyApplicationRepository.delete(application);

        if (company != null) {
            company.decreaseApplicants();
            company.decreaseNewApplicants();
        }

        return CompanyDto.ApplyRes.of(
                idx,
                false,
                company != null ? company.getApplicants() : 0,
                company != null ? company.getNewApplicants() : 0
        );
    }

    @Transactional(readOnly = true)
    public List<CompanyDto.PublicListRes> recommend(AuthUserDetails user, int size) {
        Set<Long> favoriteIds = getFavoriteCompanyIds(user);
        Set<Long> appliedIds = getAppliedCompanyIds(user);
        Long loginUserIdx = user != null ? user.getIdx() : null;

        return companyRepository.findPublicOpenListOrderByIdxDesc().stream()
                .filter(this::isVisiblePublicCompany)
                .sorted(Comparator
                        .comparingInt((Company company) -> company.getFavoriteCount() * 3 + company.getViewCount())
                        .reversed()
                        .thenComparing(Company::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(size)
                .map(company -> CompanyDto.PublicListRes.from(
                        company,
                        favoriteIds.contains(company.getIdx()),
                        appliedIds.contains(company.getIdx()),
                        isMine(company, loginUserIdx)
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public CompanyDto.ApplicantPageRes applicantList(AuthUserDetails user, Long companyIdx) {
        Company company = findOwnedCompany(user, companyIdx);
        List<CompanyApplication> applications = companyApplicationRepository.findApplicantsByCompanyIdx(companyIdx);

        return CompanyDto.ApplicantPageRes.of(
                company.getIdx(),
                company.getTitle(),
                company.getApplicants(),
                company.getNewApplicants(),
                applications.stream()
                        .map(CompanyDto.ApplicantListRes::from)
                        .toList()
        );
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

        return companyFavoriteRepository.findFavoriteCompanyIdsByUserIdx(user.getIdx())
                .stream()
                .collect(Collectors.toSet());
    }

    private Set<Long> getAppliedCompanyIds(AuthUserDetails user) {
        if (user == null || user.getIdx() == null) {
            return Set.of();
        }

        return companyApplicationRepository.findAppliedCompanyIdsByUserIdx(user.getIdx())
                .stream()
                .collect(Collectors.toSet());
    }

    private boolean isFavorite(AuthUserDetails user, Long companyIdx) {
        return user != null
                && user.getIdx() != null
                && companyFavoriteRepository.existsByCompanyIdxAndUserIdx(companyIdx, user.getIdx());
    }

    private boolean isApplied(AuthUserDetails user, Long companyIdx) {
        return user != null
                && user.getIdx() != null
                && companyApplicationRepository.existsByCompanyIdxAndUserIdx(companyIdx, user.getIdx());
    }

    private boolean isVisiblePublicCompany(Company company) {
        return company != null
                && company.isPublicOpen()
                && isOpenStatus(company.getStatus());
    }

    private boolean isOpenStatus(String status) {
        if (status == null || status.isBlank()) {
            return true;
        }

        return "RECRUITING".equalsIgnoreCase(status)
                || "OPEN".equalsIgnoreCase(status)
                || "HIRING".equalsIgnoreCase(status);
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

    private boolean isMine(Company company, Long loginUserIdx) {
        return loginUserIdx != null
                && company.getUser() != null
                && company.getUser().getIdx() != null
                && company.getUser().getIdx().equals(loginUserIdx);
    }

    private Comparator<Company> resolvePublicComparator(String sort, Long loginUserIdx) {
        Comparator<Company> baseComparator = resolveComparator(sort);

        if (loginUserIdx == null) {
            return baseComparator;
        }

        return Comparator.comparing((Company company) -> !isMine(company, loginUserIdx))
                .thenComparing(baseComparator);
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