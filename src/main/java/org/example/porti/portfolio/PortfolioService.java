package org.example.porti.portfolio;

import lombok.RequiredArgsConstructor;
import org.example.porti.portfolio.model.Portfolio;
import org.example.porti.portfolio.model.PortfolioDto;
import org.example.porti.section.SectionRepository;
import org.example.porti.section.model.Section;
import org.example.porti.section.model.SectionDto;
import org.example.porti.upload.CloudUploadService;
import org.example.porti.user.UserRepository;
import org.example.porti.user.model.AuthUserDetails;
import org.example.porti.user.model.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final SectionRepository sectionRepository;
    private final CloudUploadService cloudUploadService;
    private final UserRepository userRepository;

    @Transactional
    public Long create(AuthUserDetails authUser, PortfolioDto.Req dto, MultipartFile image) {
        User user = userRepository.findById(authUser.getIdx())
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        String ImageUrl = null;

        try {
            ImageUrl = cloudUploadService.saveFile(image);
        } catch (Exception e) {
            throw new RuntimeException("이미지 저장에 실패했습니다.", e);
        }

        Portfolio portfolio = dto.toEntity(ImageUrl, user);

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

    public List<PortfolioDto.portRes> list(AuthUserDetails authUser, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Portfolio> portfolioList = portfolioRepository.findByUserIdx(authUser.getIdx(), pageable).getContent();
        return portfolioList.stream().map(PortfolioDto.portRes::from).toList();
    }

    public List<PortfolioDto.portRes> userList(Long userIdx, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Portfolio> portfolioList = portfolioRepository.findByUserIdx(userIdx, pageable).getContent();
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

        portfolio.setTheme(dto.getTheme());
        portfolio.setLayoutType(dto.getLayoutType());

        if (dto.getSectionList() != null) {
            for (SectionDto.Req sReq : dto.getSectionList()) {
                sectionRepository.findById(sReq.getIdx()).ifPresent(section -> {
                    section.setSectionOrder(sReq.getSectionOrder());
                });
            }
        }
    }


    @Transactional
    public void delete(Long portfolioIdx, AuthUserDetails authUser, String title) {
        Portfolio portfolio = portfolioRepository.findById(portfolioIdx)
                .orElseThrow(() -> new RuntimeException("포트폴리오를 찾을 수 없습니다."));

        if (!portfolio.getUser().getIdx().equals(authUser.getIdx())) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        if (!portfolio.getTitle().equals(title)) {
            throw new RuntimeException("입력한 제목이 포트폴리오 제목과 일치하지 않습니다.");
        }

        portfolioRepository.delete(portfolio);
    }

    @Transactional(readOnly = true)
    public List<String> getAllKeywords(AuthUserDetails authUser) {
        List<Portfolio> portfolioList = portfolioRepository.findAllByUserIdx(authUser.getIdx());

        return portfolioList.stream()
                .filter(p -> p.getKeywords() != null)
                .flatMap(p -> p.getKeywords().stream())
                .distinct()
                .toList();
    }
}