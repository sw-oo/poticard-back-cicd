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

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/company")
@RestController
@RequiredArgsConstructor
@Tag(name = "기업 공고 기능")
public class CompanyController {
    private final CompanyService companyService;

    @Operation(summary = "공고 등록", description = "기업 사용자가 공고를 등록하는 기능")
    @PostMapping("/reg")
    public ResponseEntity register(
            @AuthenticationPrincipal AuthUserDetails user,
            @RequestBody CompanyDto.RegReq dto) {

        CompanyDto.RegRes result = companyService.register(user, dto);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/list")
    public ResponseEntity list(
            @AuthenticationPrincipal AuthUserDetails user,
            @RequestParam(required = true, defaultValue = "0") int page,
            @RequestParam(required = true, defaultValue = "10") int size) {
        CompanyDto.PageRes dto = companyService.list(user, page, size);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }

    @GetMapping("/read/{idx}")
    public ResponseEntity read(@PathVariable Long idx) {
        CompanyDto.ReadRes dto = companyService.read(idx);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }

    @PutMapping("/update/{idx}")
    public ResponseEntity update(@PathVariable Long idx,
                                 @RequestBody CompanyDto.RegReq dto) {
        CompanyDto.RegRes result = companyService.update(idx, dto);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @DeleteMapping("/delete/{idx}")
    public ResponseEntity delete(@PathVariable Long idx) {
        companyService.delete(idx);
        return ResponseEntity.ok(BaseResponse.success("성공"));
    }

    @PatchMapping("/close/{idx}")
    public ResponseEntity close(@PathVariable Long idx) {
        CompanyDto.RegRes result = companyService.close(idx);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}