package org.example.porti.orders;

import lombok.RequiredArgsConstructor;
import org.example.porti.common.model.BaseResponse;
import org.example.porti.common.model.BaseResponseStatus;
import org.example.porti.orders.model.OrdersDto;
import org.example.porti.user.model.AuthUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/orders")
@RestController
public class OrdersController {
    private final OrdersService ordersService;

    @PostMapping("/verify")
    public ResponseEntity verify(
            @RequestBody OrdersDto.VerifyReq dto,
            @AuthenticationPrincipal AuthUserDetails user) {
        try {
            OrdersDto.OrdersRes response = ordersService.verify(dto, user);
            return ResponseEntity.ok(BaseResponse.success(response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(BaseResponse.fail(BaseResponseStatus.FAIL, e.getMessage()));
        }
    }

    @GetMapping("/check-pro")
    public ResponseEntity checkPro(@AuthenticationPrincipal AuthUserDetails user) {
        boolean isPro = ordersService.checkProPlan(user);
        return ResponseEntity.ok(BaseResponse.success(isPro));
    }
}