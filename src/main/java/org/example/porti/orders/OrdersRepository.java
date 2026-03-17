package org.example.porti.orders;

import org.example.porti.orders.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    boolean existsByPgPaymentId(String pgPaymentId);

    Optional<Orders> findFirstByUserIdxAndPaidTrueOrderByIdxDesc(Long userIdx);
}