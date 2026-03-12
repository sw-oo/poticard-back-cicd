package org.example.porti.notification;

import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.example.porti.notification.model.NotificationDto;
import org.example.porti.notification.model.NotificationEntity;
import org.springframework.stereotype.Service;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final PushService pushService;

    public NotificationService(NotificationRepository notificationRepository) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        this.notificationRepository = notificationRepository;

        if (Security.getProperty(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        this.pushService = new PushService();
        this.pushService.setPublicKey("BLHgfPga02L2u89uc4xjhbUFTy_U04rQCjGq7o24oxtqfVmAPHTxOmp6xndSHZtGQpmt7gqTFdMXco2gRNP7_p8");
        this.pushService.setPrivateKey("pWhOI-mTyOyx5hogOmKRiYHDCtm_IMpnz1lzWNdMfKU");
        this.pushService.setSubject("우리 사이트이다");
    }

    public void subscribe(NotificationDto.Subscribe dto) {
        // 이미 해당 엔드포인트(기기)가 등록되어 있는지 확인
        notificationRepository.findByEndpoint(dto.getEndpoint())
                .ifPresentOrElse(
                        existing -> {
                            // 이미 있다면 사용자 ID만 업데이트 (기기 주인이 바뀔 수도 있으므로)
                            NotificationEntity updated = NotificationEntity.builder()
                                    .idx(existing.getIdx())
                                    .userIdx(dto.getUserIdx())
                                    .endpoint(existing.getEndpoint())
                                    .p256dh(dto.getKeys().get("p256dh"))
                                    .auth(dto.getKeys().get("auth"))
                                    .build();
                            notificationRepository.save(updated);
                        },
                        () -> {
                            // 없다면 새로 저장
                            notificationRepository.save(dto.toEntity());
                        }
                );
    }
}
