package org.example.porti.portfolio;


import lombok.RequiredArgsConstructor;
import org.example.porti.portfolio.model.Portfolio;
import org.example.porti.portfolio.model.PortfolioDto;
import org.example.porti.section.model.Section;
import org.example.porti.section.model.SectionDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;

    @Transactional
    public void create(PortfolioDto.Req dto) {
        Portfolio portfolio = dto.toEntity();

        if (dto.getSectionList() != null) {
            for (SectionDto.Req sectionReq : dto.getSectionList()) {
                Section section = sectionReq.toEntity();

                section.setPortfolio(portfolio);
                portfolio.getSectionList().add(section);
            }
        }
        portfolioRepository.save(portfolio);
    }
    @Transactional(readOnly = true)
    public PortfolioDto.Res read(Long idx) {
        Portfolio portfolio = portfolioRepository.findById(idx)
                .orElseThrow(() -> new RuntimeException("포트폴리오를 찾을 수 없습니다."));
        return PortfolioDto.Res.from(portfolio);
    }

    public List<PortfolioDto.portRes> list(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Portfolio> portfolioList = portfolioRepository.findAll(pageable).getContent();
        return portfolioList.stream().map(PortfolioDto.portRes::from).toList();
    }
}
