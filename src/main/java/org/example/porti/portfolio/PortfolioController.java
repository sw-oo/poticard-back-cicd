package org.example.porti.portfolio;

import lombok.RequiredArgsConstructor;
import org.example.porti.common.model.BaseResponse;
import org.example.porti.common.model.BaseResponseStatus;
import org.example.porti.portfolio.model.PortfolioDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/portfolio")
@RestController
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;

    // 포트폴리오 생성
    @PostMapping("/create")
    public ResponseEntity create(@RequestBody PortfolioDto.Req dto) {
        portfolioService.create(dto);
        return ResponseEntity.ok(BaseResponse.success(BaseResponseStatus.SUCCESS));
    }

    // 포트폴리오 단일 조회
    @GetMapping("/{idx}")
    public ResponseEntity read(@PathVariable Long idx) {
        return ResponseEntity.ok(BaseResponse.success(portfolioService.read(idx)));
    }

    // 포트폴리오 목록 조회(페이징 처리)
    @GetMapping("/list")
    public ResponseEntity list(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {
        List<PortfolioDto.portRes> dto = portfolioService.list(page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("isSuccess", true);
        response.put("result", dto);
        return ResponseEntity.ok(BaseResponse.success(response));
    }
}
