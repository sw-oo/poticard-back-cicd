package org.example.porti.community;

import lombok.RequiredArgsConstructor;
import org.example.porti.common.model.BaseResponse;
import org.example.porti.community.comment.model.CommunityCommentDto;
import org.example.porti.community.model.CommunityDto;
import org.example.porti.user.model.AuthUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/community")
@RestController
@RequiredArgsConstructor
public class CommunityController {
    private final CommunityService communityService;

    @PostMapping("/reg")
    public ResponseEntity<?> register(
            @AuthenticationPrincipal AuthUserDetails user,
            @RequestBody CommunityDto.RegReq dto) {

        CommunityDto.RegRes result = communityService.register(user, dto);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(
            @AuthenticationPrincipal AuthUserDetails user,
            @RequestParam(required = false, defaultValue = "ALL") String scope,
            @RequestParam(required = true, defaultValue = "0") int page,
            @RequestParam(required = true, defaultValue = "20") int size) {
        CommunityDto.PageRes dto = communityService.list(user, page, size, scope);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }

    @GetMapping("/read/{idx}")
    public ResponseEntity<?> read(@AuthenticationPrincipal AuthUserDetails user,
                                  @PathVariable Long idx) {
        CommunityDto.ReadRes dto = communityService.read(user, idx);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }

    @PutMapping("/update/{idx}")
    public ResponseEntity<?> update(@AuthenticationPrincipal AuthUserDetails user,
                                    @PathVariable Long idx,
                                    @RequestBody CommunityDto.RegReq dto) {
        CommunityDto.RegRes result = communityService.update(user, idx, dto);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @DeleteMapping("/delete/{idx}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal AuthUserDetails user,
                                    @PathVariable Long idx) {
        communityService.delete(user, idx);
        return ResponseEntity.ok(BaseResponse.success("성공"));
    }

    @PatchMapping("/favorite/{idx}")
    public ResponseEntity<?> toggleFavorite(@AuthenticationPrincipal AuthUserDetails user,
                                            @PathVariable Long idx) {
        CommunityDto.FavoriteToggleRes result = communityService.toggleFavorite(user, idx);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/summary")
    public ResponseEntity<?> summary(@AuthenticationPrincipal AuthUserDetails user) {
        CommunityDto.SummaryRes result = communityService.summary(user);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/comment/list/{communityIdx}")
    public ResponseEntity<?> listComments(@AuthenticationPrincipal AuthUserDetails user,
                                          @PathVariable Long communityIdx) {
        List<CommunityCommentDto.CommentRes> result = communityService.listComments(user, communityIdx);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @PostMapping("/comment/reg/{communityIdx}")
    public ResponseEntity<?> registerComment(@AuthenticationPrincipal AuthUserDetails user,
                                             @PathVariable Long communityIdx,
                                             @RequestBody CommunityCommentDto.RegReq dto) {
        CommunityCommentDto.CommentRes result = communityService.registerComment(user, communityIdx, dto);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @DeleteMapping("/comment/delete/{commentIdx}")
    public ResponseEntity<?> deleteComment(@AuthenticationPrincipal AuthUserDetails user,
                                           @PathVariable Long commentIdx) {
        communityService.deleteComment(user, commentIdx);
        return ResponseEntity.ok(BaseResponse.success("성공"));
    }
}