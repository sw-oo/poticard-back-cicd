package org.example.porti.section;

import lombok.RequiredArgsConstructor;
import org.example.porti.common.model.BaseResponse;
import org.example.porti.section.model.SectionDto;
import org.example.porti.upload.CloudUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/section")
@RequiredArgsConstructor
public class SectionController {
    private final SectionService sectionService;
    private final CloudUploadService cloudUploadService;

    // 특정 포트폴리오의 섹션 목록 조회
    @GetMapping("/list/{portfolioIdx}")
    public ResponseEntity list(@PathVariable Long portfolioIdx) {
        List<SectionDto.Res> sectionList = sectionService.getSectionList(portfolioIdx);
        return ResponseEntity.ok(BaseResponse.success(sectionList));
    }

    // 섹션 내용 수정
    @PatchMapping("/update/{sectionIdx}")
    public ResponseEntity updateSection(
            @PathVariable Long sectionIdx,
            @RequestBody SectionDto.Req updateReq) {

        sectionService.updateSection(sectionIdx, updateReq);
        return ResponseEntity.ok(BaseResponse.success("섹션이 성공적으로 수정되었습니다."));
    }

    @PostMapping(value = "/image", consumes = {"multipart/form-data"})
    public ResponseEntity uploadSectionImage(@RequestPart("image") MultipartFile image) throws Exception {
        System.out.println("📸 프론트에서 이미지 도착함! 파일명: " + image.getOriginalFilename()); // ✨ 이 줄 추가
        String imageUrl = cloudUploadService.saveFile(image);
        return ResponseEntity.ok(BaseResponse.success(imageUrl));
    }
}