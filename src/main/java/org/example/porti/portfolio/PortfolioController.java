package org.example.porti.portfolio;

import lombok.RequiredArgsConstructor;
import org.example.porti.common.model.BaseResponse;
import org.example.porti.portfolio.model.PortfolioDto;
import org.example.porti.user.model.AuthUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/portfolio")
@RestController
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;
    private final AiService aiService;

    // 포트폴리오 생성
    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity create(
            @AuthenticationPrincipal AuthUserDetails user,
            @RequestPart("data") PortfolioDto.Req dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        Long newIdx = portfolioService.create(user, dto, image);
        return ResponseEntity.ok(BaseResponse.success(newIdx));
    }

    // 포트폴리오 단일 조회
    @GetMapping("/{idx}")
    public ResponseEntity read(@PathVariable Long idx) {
        return ResponseEntity.ok(BaseResponse.success(portfolioService.read(idx)));
    }

    // 포트폴리오 목록 조회(페이징 처리)
    @GetMapping("/list")
    public ResponseEntity list(
            @AuthenticationPrincipal AuthUserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<PortfolioDto.portRes> dto = portfolioService.list(user, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("isSuccess", true);
        response.put("result", dto);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    // 포트폴리오 키워드 저장
    @PatchMapping("/{idx}/keywords")
    public ResponseEntity updateKeywords(
            @PathVariable Long idx,
            @RequestBody List<String> keywords) {

        portfolioService.updateKeywords(idx, keywords);
        return ResponseEntity.ok(BaseResponse.success("키워드가 저장되었습니다."));
    }

    // 포트폴리오 스타일 저장
    @PatchMapping("/{idx}/style")
    public ResponseEntity updateStyle(@PathVariable Long idx, @RequestBody PortfolioDto.Req dto) {
        portfolioService.updateStyle(idx, dto);
        return ResponseEntity.ok(BaseResponse.success("스타일 설정이 저장되었습니다."));
    }

    // 포트폴리오 ai 첨삭
    @PostMapping("/ai-review")
    public ResponseEntity aiReview(@RequestBody Map<String, String> request) {
        String contents = request.get("contents");
        String aiResult = aiService.getAiReview(contents);
        return ResponseEntity.ok(BaseResponse.success(aiResult));
    }

    // 포트폴리오 ai 키워드 추출
    @PostMapping("/ai-keywords")
    public ResponseEntity aiKeywords(@RequestBody Map<String, String> request) {
        String contents = request.get("contents");
        List<String> keywords = aiService.getAiKeywords(contents);
        return ResponseEntity.ok(BaseResponse.success(keywords));
    }
}
