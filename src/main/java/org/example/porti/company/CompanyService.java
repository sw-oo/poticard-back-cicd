package org.example.porti.company;

import lombok.RequiredArgsConstructor;
import org.example.porti.company.model.Company;
import org.example.porti.company.model.CompanyDto;
import org.example.porti.user.model.AuthUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    @Transactional
    public CompanyDto.RegRes register(AuthUserDetails user, CompanyDto.RegReq dto) {
        Company entity = companyRepository.save(dto.toEntity(user));
        return CompanyDto.RegRes.from(entity);
    }

    @Transactional(readOnly = true)
    public CompanyDto.PageRes list(AuthUserDetails user, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "idx"));
        Page<Company> result = companyRepository.findByUserIdxOrderByIdxDesc(user.getIdx(), pageRequest);

        long totalCount = companyRepository.countByUserIdx(user.getIdx());
        long recruitingCount = companyRepository.countByUserIdxAndStatus(user.getIdx(), "RECRUITING");
        int totalApplicants = companyRepository.sumApplicantsByUserIdx(user.getIdx());

        return CompanyDto.PageRes.from(result, totalCount, recruitingCount, totalApplicants);
    }

    @Transactional(readOnly = true)
    public CompanyDto.ReadRes read(Long idx) {
        Company company = companyRepository.findById(idx)
                .orElseThrow(() -> new IllegalArgumentException("공고가 없습니다."));

        return CompanyDto.ReadRes.from(company);
    }

    @Transactional
    public CompanyDto.RegRes update(Long idx, CompanyDto.RegReq dto) {
        Company company = companyRepository.findById(idx)
                .orElseThrow(() -> new IllegalArgumentException("공고가 없습니다."));

        company.update(dto);
        companyRepository.save(company);

        return CompanyDto.RegRes.from(company);
    }

    @Transactional
    public void delete(Long idx) {
        companyRepository.deleteById(idx);
    }

    @Transactional
    public CompanyDto.RegRes close(Long idx) {
        Company company = companyRepository.findById(idx)
                .orElseThrow(() -> new IllegalArgumentException("공고가 없습니다."));

        company.closeRecruitment();
        companyRepository.save(company);

        return CompanyDto.RegRes.from(company);
    }
}