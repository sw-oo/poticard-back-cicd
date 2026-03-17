package org.example.porti.orders;

import io.portone.sdk.server.payment.PaidPayment;
import io.portone.sdk.server.payment.Payment;
import io.portone.sdk.server.payment.PaymentClient;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.porti.orders.model.Orders;
import org.example.porti.orders.model.OrdersDto;
import org.example.porti.user.UserRepository;
import org.example.porti.user.model.AuthUserDetails;
import org.example.porti.user.model.User;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrdersService {
    private final OrdersRepository ordersRepository;
    private final UserRepository userRepository;
    private final PaymentClient pg;

    @Transactional(readOnly = true)
    public boolean checkProPlan(AuthUserDetails authUser) {
        return ordersRepository.findFirstByUserIdxAndPaidTrueOrderByIdxDesc(authUser.getIdx())
                .map(order -> "PRO".equalsIgnoreCase(order.getPlanCode()))
                .orElse(false);
    }

    @Transactional
    public OrdersDto.OrdersRes verify(OrdersDto.VerifyReq dto, AuthUserDetails authUser) {
        CompletableFuture<Payment> completableFuture = pg.getPayment(dto.getPaymentId());
        Payment payment = completableFuture.join();

        if (payment instanceof PaidPayment paidPayment) {
            if (paidPayment.getAmount().getTotal() == dto.getAmount()) {

                User user = userRepository.findById(authUser.getIdx())
                        .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

                Orders orders = Orders.builder()
                        .paid(true)
                        .paymentPrice(dto.getAmount())
                        .pgPaymentId(dto.getPaymentId())
                        .merchantUid(dto.getMerchantUid())
                        .planCode(dto.getPlanCode())
                        .email(dto.getEmail())
                        .user(user)
                        .build();

                Orders saved = ordersRepository.save(orders);

                return OrdersDto.OrdersRes.from(saved);
            } else {
                throw new RuntimeException("결제 금액 위변조가 의심됩니다.");
            }
        } else {
            throw new RuntimeException("결제가 완료되지 않았습니다.");
        }
    }
}
