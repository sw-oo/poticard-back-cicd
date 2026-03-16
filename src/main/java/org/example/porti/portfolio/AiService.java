package org.example.porti.portfolio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    // AI 첨삭
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

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(createRequestBody(prompt), headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            return extractTextFromResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
            return "AI 첨삭에 실패했습니다. 다시 시도해주세요.";
        }
    }

    // AI 키워드 추출
    public List<String> getAiKeywords(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("분석할 내용이 비어 있습니다.");
        }

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + geminiApiKey;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String prompt = "다음 포트폴리오 내용을 분석해서 이 포트폴리오를 작성한 사용자가 실제로 사용한 '기술 스택(프로그래밍 언어, 프레임워크, 라이브러리, 데이터베이스, 인프라 도구 등)'만 추출해 줘.\n" +
                "문맥을 파악했을 때 사용자가 직접 다루지 않았거나, 단순 개념 설명인 경우 추출하지 마.\n" +
                "[절대 규칙]\n" +
                "- 일반적인 역량 키워드(예: 문제 해결, 최적화, 아키텍처, 커뮤니케이션 등)는 절대 포함하지 마.\n" +
                "- 부가적인 설명, 인사말, 기호(``` 등)는 절대 포함하지 마.\n" +
                "- 오직 추출된 기술 스택 이름만 쉼표(,)로 구분해서 한 줄로 출력해 (가능한 고유의 영문 명칭 사용).\n" +
                "- 최대 8개까지만 추출해.\n\n" +
                "[포트폴리오 내용]\n" + content;

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(createRequestBody(prompt), headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            String aiText = extractTextFromResponse(response);

            if (aiText == null || aiText.trim().isEmpty()) {
                return new ArrayList<>();
            }

            return Arrays.stream(aiText.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AI 키워드 추출에 실패했습니다.");
        }
    }

    // Gemini API 요청 바디 생성 공통 메서드
    private Map<String, Object> createRequestBody(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> contentMap = new HashMap<>();
        List<Map<String, String>> parts = new ArrayList<>();
        Map<String, String> part = new HashMap<>();

        part.put("text", prompt);
        parts.add(part);
        contentMap.put("parts", parts);
        contents.add(contentMap);
        requestBody.put("contents", contents);

        return requestBody;
    }

    // Gemini API 응답 파싱 공통 메서드
    private String extractTextFromResponse(ResponseEntity<String> response) throws Exception {
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

        return partsNode.get(0).path("text").asText();
    }
}