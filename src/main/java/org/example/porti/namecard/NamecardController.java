package org.example.porti.namecard;

import lombok.RequiredArgsConstructor;
import org.example.porti.common.model.BaseResponse;
import org.example.porti.namecard.model.NamecardDto;
import org.example.porti.user.model.AuthUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/namecard")
@RequiredArgsConstructor
public class NamecardController {
    private final NamecardService namecardService;
    // 명함 리스트(슬라이스 처리)
    @GetMapping("/list")
    public ResponseEntity list(
            @RequestParam(required = true, defaultValue = "0") int page,
            @RequestParam(required = true, defaultValue = "10") int size
            ){
        NamecardDto.SliceRes dto = namecardService.list(page, size);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }

//    @GetMapping("/list")
//    public ResponseEntity list(){
//        List<NamecardDto.NamecardRes> dto = namecardService.list();
//        return ResponseEntity.ok(BaseResponse.success(dto));
//    }
    // 특정 한명의 명함 불러오기
    @GetMapping("/singleUser")
    public ResponseEntity singleUser (@RequestParam Long userId){
        NamecardDto.NamecardRes dto = namecardService.singleUser(userId);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }
    // 명함 등록 및 업데이트
    @PostMapping("/reg")
    public ResponseEntity register(
            @RequestBody NamecardDto.Register dto,
            @AuthenticationPrincipal AuthUserDetails user){
        namecardService.reg(dto, user);
        return ResponseEntity.ok(BaseResponse.success("성공"));
    }
    // 명함 갯수 구하기
    @GetMapping("/amount")
    public ResponseEntity amount(){
        Long res = namecardService.amount();
        return ResponseEntity.ok(BaseResponse.success(res));
    }
}
