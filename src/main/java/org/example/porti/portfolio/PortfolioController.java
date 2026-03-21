package org.example.porti.portfolio;

import lombok.RequiredArgsConstructor;
import org.example.porti.common.model.BaseResponse;
import org.example.porti.portfolio.model.PortfolioDto;
import org.example.porti.upload.CloudUploadService;
import org.example.porti.user.model.AuthUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.example.porti.common.model.BaseResponseStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/portfolio")
@RestController
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;
    private final AiService aiService;
    private final CloudUploadService cloudUploadService;

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
            @RequestParam(defaultValue = "6") int size) {
        List<PortfolioDto.portRes> dto = portfolioService.list(user, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("isSuccess", true);
        response.put("result", dto);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    // 특정 유저 포트폴리오 목록 조회(페이징 처리)
    @GetMapping("/user/{userIdx}/list")
    public ResponseEntity userList(
            @PathVariable Long userIdx,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        List<PortfolioDto.portRes> dto = portfolioService.userList(userIdx, page, size);

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

    // 모든 포트폴리오에 저장된 키워드 호출
    @GetMapping("/keywords")
    public ResponseEntity getAllKeywords(@AuthenticationPrincipal AuthUserDetails user) {
        List<String> keywords = portfolioService.getAllKeywords(user);
        return ResponseEntity.ok(BaseResponse.success(keywords));
    }

    // 포트폴리오 스타일 저장
    @PatchMapping("/{idx}/style")
    public ResponseEntity updateStyle(@PathVariable Long idx, @RequestBody PortfolioDto.Req dto) {
        portfolioService.updateStyle(idx, dto);
        return ResponseEntity.ok(BaseResponse.success("스타일 설정이 저장되었습니다."));
    }

    // 포트폴리오 ai 첨삭
    @PostMapping("/ai-review")
    public ResponseEntity aiReview(
            @AuthenticationPrincipal AuthUserDetails user, // ✨ 유저 정보 받기
            @RequestBody Map<String, String> request) {
        try {
            String contents = request.get("contents");
            String aiResult = aiService.getAiReview(user.getIdx(), contents); // ✨ userIdx 전달
            return ResponseEntity.ok(BaseResponse.success(aiResult));
        } catch (RuntimeException e) {
            // PRO 유저가 아니거나 오류 발생 시 실패 메시지 반환
            return ResponseEntity.ok(BaseResponse.fail(BaseResponseStatus.FAIL, e.getMessage()));
        }
    }

    // 포트폴리오 ai 키워드 추출
    @PostMapping("/ai-keywords")
    public ResponseEntity aiKeywords(@RequestBody Map<String, String> request) {
        String contents = request.get("contents");
        List<String> keywords = aiService.getAiKeywords(contents);
        return ResponseEntity.ok(BaseResponse.success(keywords));
    }

    // 포트폴리오 삭제
    @PostMapping("/delete/{idx}")
    public ResponseEntity delete(
            @PathVariable Long idx,
            @AuthenticationPrincipal AuthUserDetails user,
            @RequestBody Map<String, String> request) {
        try {
            String title = request.get("title");
            portfolioService.delete(idx, user, title);
            return ResponseEntity.ok(BaseResponse.success("포트폴리오가 삭제되었습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(BaseResponse.fail(BaseResponseStatus.FAIL, e.getMessage()));
        }
    }
    @PostMapping(value = "/image-upload", consumes = {"multipart/form-data"})
// RequestPart 대신 RequestParam으로 변경해 보세요.
    public ResponseEntity uploadEditorImage(@RequestParam("image") MultipartFile image) {
        try {
            System.out.println("📸 포트폴리오 에디터 이미지 도착! 파일명: " + image.getOriginalFilename());

            String imageUrl = cloudUploadService.saveFile(image);
            return ResponseEntity.ok(BaseResponse.success(imageUrl));

        } catch (Exception e) {
            System.err.println("🚨 에러 발생: " + e.getMessage());
            e.printStackTrace(); // <-- 이 부분에 찍히는 로그를 꼭 확인하세요!
            return ResponseEntity.internalServerError()
                    .body(BaseResponse.fail(BaseResponseStatus.FAIL, "업로드 실패: " + e.getMessage()));
        }
    }
}
