package org.example.porti.community;

import org.example.porti.common.model.BaseResponse;
import org.example.porti.community.model.CommunityDto;
import org.example.porti.user.model.AuthUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/community")
@RestController
@RequiredArgsConstructor
public class CommunityController {
    private final CommunityService communityService;

    @PostMapping("/reg")
    public ResponseEntity register(
            @AuthenticationPrincipal AuthUserDetails user,
            @RequestBody CommunityDto.RegReq dto) {

        CommunityDto.RegRes result = communityService.register(user, dto);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/list")
    public ResponseEntity list(
            @RequestParam(required = true, defaultValue = "0") int page,
            @RequestParam(required = true, defaultValue = "5") int size) {
        CommunityDto.PageRes dto = communityService.list(page, size);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }

    @GetMapping("/read/{idx}")
    public ResponseEntity read(@PathVariable Long idx) {
        CommunityDto.ReadRes dto = communityService.read(idx);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }

    @PutMapping("/update/{idx}")
    public ResponseEntity update(@PathVariable Long idx, @RequestBody CommunityDto.RegReq dto) {
        CommunityDto.RegRes result = communityService.update(idx, dto);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @DeleteMapping("/delete/{idx}")
    public ResponseEntity delete(@PathVariable Long idx) {
        communityService.delete(idx);
        return ResponseEntity.ok(BaseResponse.success("성공"));
    }
}
