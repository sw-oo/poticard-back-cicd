package org.example.porti.company;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.porti.common.model.BaseResponse;
import org.example.porti.company.model.CompanyDto;
import org.example.porti.user.model.AuthUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/company")
@RestController
@RequiredArgsConstructor
@Tag(name = "기업 공고 기능")
public class CompanyController {
    private final CompanyService companyService;

    @Operation(summary = "공고 등록", description = "기업 사용자가 공고를 등록하는 기능")
    @PostMapping("/reg")
    public ResponseEntity<?> register(
            @AuthenticationPrincipal AuthUserDetails user,
            @RequestBody CompanyDto.RegReq dto) {

        CompanyDto.RegRes result = companyService.register(user, dto);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(
            @AuthenticationPrincipal AuthUserDetails user,
            @RequestParam(required = true, defaultValue = "0") int page,
            @RequestParam(required = true, defaultValue = "10") int size) {
        CompanyDto.PageRes dto = companyService.list(user, page, size);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }

    @GetMapping("/read/{idx}")
    public ResponseEntity<?> read(@AuthenticationPrincipal AuthUserDetails user,
                                  @PathVariable Long idx) {
        CompanyDto.ReadRes dto = companyService.read(user, idx);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }

    @PutMapping("/update/{idx}")
    public ResponseEntity<?> update(@AuthenticationPrincipal AuthUserDetails user,
                                    @PathVariable Long idx,
                                    @RequestBody CompanyDto.RegReq dto) {
        CompanyDto.RegRes result = companyService.update(user, idx, dto);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @DeleteMapping("/delete/{idx}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal AuthUserDetails user,
                                    @PathVariable Long idx) {
        companyService.delete(user, idx);
        return ResponseEntity.ok(BaseResponse.success("성공"));
    }

    @PatchMapping("/close/{idx}")
    public ResponseEntity<?> close(@AuthenticationPrincipal AuthUserDetails user,
                                   @PathVariable Long idx) {
        CompanyDto.RegRes result = companyService.close(user, idx);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/public/list")
    public ResponseEntity<?> publicList(@AuthenticationPrincipal AuthUserDetails user,
                                        @RequestParam(required = false, defaultValue = "") String keyword,
                                        @RequestParam(required = false, defaultValue = "ALL") String category,
                                        @RequestParam(required = false, defaultValue = "false") boolean favoriteOnly,
                                        @RequestParam(required = false, defaultValue = "popular") String sort) {
        List<CompanyDto.PublicListRes> result = companyService.publicList(user, keyword, category, favoriteOnly, sort);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/public/read/{idx}")
    public ResponseEntity<?> publicRead(@AuthenticationPrincipal AuthUserDetails user,
                                        @PathVariable Long idx) {
        CompanyDto.PublicDetailRes result = companyService.publicRead(user, idx);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @PatchMapping("/public/favorite/{idx}")
    public ResponseEntity<?> toggleFavorite(@AuthenticationPrincipal AuthUserDetails user,
                                            @PathVariable Long idx) {
        CompanyDto.FavoriteToggleRes result = companyService.toggleFavorite(user, idx);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/public/recommend")
    public ResponseEntity<?> recommend(@AuthenticationPrincipal AuthUserDetails user,
                                       @RequestParam(required = false, defaultValue = "4") int size) {
        List<CompanyDto.PublicListRes> result = companyService.recommend(user, size);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}