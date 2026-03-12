package org.example.porti.portfolio;


import lombok.RequiredArgsConstructor;
import org.example.porti.portfolio.model.Portfolio;
import org.example.porti.portfolio.model.PortfolioDto;
import org.example.porti.section.SectionRepository;
import org.example.porti.section.model.Section;
import org.example.porti.section.model.SectionDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final SectionRepository sectionRepository;

    @Transactional
    public Long create(PortfolioDto.Req dto) {
        Portfolio portfolio = dto.toEntity();

        if (portfolio.getSectionList() == null) {
            portfolio.setSectionList(new ArrayList<>());
        }
        if (dto.getSectionList() != null) {
            for (SectionDto.Req sectionReq : dto.getSectionList()) {
                Section section = sectionReq.toEntity();

                section.setPortfolio(portfolio);
                portfolio.getSectionList().add(section);
            }
        }
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);
        return savedPortfolio.getIdx();
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

    @Transactional
    public void updateKeywords(Long portfolioIdx, List<String> keywords) {
        Portfolio portfolio = portfolioRepository.findById(portfolioIdx)
                .orElseThrow(() -> new RuntimeException("포트폴리오를 찾을 수 없습니다."));
        portfolio.setKeywords(keywords);
    }
    @Transactional
    public void updateStyle(Long portfolioIdx, PortfolioDto.Req dto) {
        Portfolio portfolio = portfolioRepository.findById(portfolioIdx)
                .orElseThrow(() -> new RuntimeException("포트폴리오 없음"));

        portfolio.setAccentColor(dto.getAccentColor());

        for (SectionDto.Req sReq : dto.getSectionList()) {
            Section section = sectionRepository.findById(sReq.getIdx()).get();
            section.setSectionOrder(sReq.getSectionOrder());
        }
    }
}
