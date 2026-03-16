package org.example.porti.portfolio;

import lombok.RequiredArgsConstructor;
import org.example.porti.portfolio.model.Portfolio;
import org.example.porti.portfolio.model.PortfolioDto;
import org.example.porti.section.SectionRepository;
import org.example.porti.section.model.Section;
import org.example.porti.section.model.SectionDto;
import org.example.porti.upload.CloudUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final SectionRepository sectionRepository;
    private final CloudUploadService cloudUploadService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Transactional
    public Long create(PortfolioDto.Req dto, MultipartFile image) {
        String ImageUrl = null;

        try {
            ImageUrl = cloudUploadService.saveFile(image);
        } catch (Exception e) {
            throw new RuntimeException("이미지 저장에 실패했습니다.", e);
        }

        Portfolio portfolio = dto.toEntity(ImageUrl);

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
    public String getAiReview(String originalContent) {
        if (originalContent == null || originalContent.trim().isEmpty()) {
            throw new RuntimeException("첨삭할 내용이 비어 있습니다.");
        }

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + geminiApiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String prompt = "다음 작성한 포트폴리오 내용을 전문가가 교정한 것처럼 다듬어 줘.\n" +
                "[절대 규칙]\n" +
                "- 결과는 1개만 작성해.\n" +
                "- 인사말, 서론, 결론 등 불필요한 텍스트 없이 오직 완성된 본문만 출력해.\n" +
                "- 원문을 단순 교정하는 데 그치지 말고, 개발자의 기여도와 기술적 역량이 돋보이도록 문장을 구체적이고 설득력 있게 발전시켜.\n" +
                "- 평범한 표현은 실무적이고 전문적인 어휘(예: 설계, 최적화, 구축 등)로 교체해.\n" +
                "- [매우 중요] 글을 풍성하게 만들되, 원본에 없는 기술 스택이나 거짓 경험은 절대 지어내지 마.\n\n" +
                "[원본 내용]\n" + originalContent;

        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();
        List<Map<String, String>> parts = new ArrayList<>();
        Map<String, String> part = new HashMap<>();

        part.put("text", prompt);
        parts.add(part);
        content.put("parts", parts);
        contents.add(content);
        requestBody.put("contents", contents);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Gemini API 응답이 비정상적입니다.");
            }

            JsonNode root = objectMapper.readTree(response.getBody());

            JsonNode candidates = root.path("candidates");
            if (!candidates.isArray() || candidates.isEmpty()) {
                throw new RuntimeException("AI 응답 후보가 없습니다.");
            }

            JsonNode partsNode = candidates.get(0).path("content").path("parts");
            if (!partsNode.isArray() || partsNode.isEmpty()) {
                throw new RuntimeException("AI 응답 내용이 없습니다.");
            }

            String aiText = partsNode.get(0).path("text").asText();

            if (aiText == null || aiText.trim().isEmpty()) {
                throw new RuntimeException("AI 첨삭 결과가 비어 있습니다.");
            }

            return aiText.trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "AI 첨삭에 실패했습니다. 다시 시도해주세요.";
        }
    }
}