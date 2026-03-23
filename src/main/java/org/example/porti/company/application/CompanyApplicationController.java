package org.example.porti.company.application;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.porti.common.model.BaseResponse;
import org.example.porti.company.CompanyService;
import org.example.porti.company.model.CompanyDto;
import org.example.porti.user.model.AuthUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/company/application")
@RestController
@RequiredArgsConstructor
@Tag(name = "기업 지원자 기능")
public class CompanyApplicationController {
    private final CompanyService companyService;

    @GetMapping("/list/{companyIdx}")
    public ResponseEntity<?> applicantList(@AuthenticationPrincipal AuthUserDetails user,
                                           @PathVariable Long companyIdx) {
        CompanyDto.ApplicantPageRes result = companyService.applicantList(user, companyIdx);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}