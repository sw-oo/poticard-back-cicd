package org.example.porti.notification;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/sse")
public class SseController {

    // 유저별 SseEmitter를 관리할 Map (실제 서비스에선 Redis나 별도 저장소 권장)
    private static final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @GetMapping(value = "/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Long userId) {
        // 유효시간 1시간 설정
        SseEmitter emitter = new SseEmitter(60 * 30 * 1000L);
        emitters.put(userId, emitter);

        // 연결 종료/타임아웃 시 Map에서 삭제
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));

        // 최초 연결 시 더미 데이터 전송 (503 에러 방지)
        try {
            emitter.send(SseEmitter.event().name("connect").data("connected!"));
        } catch (IOException e) {
            emitters.remove(userId);
        }
        return emitter;
    }

    public boolean hasConnection(Long userId) {
        return emitters.containsKey(userId);
    }

    // 특정 유저에게 알림 전송 로직 (서비스 계층에서 호출)
    public void sendNotification(Long userId, Object data) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(data));
            } catch (IOException e) {
                emitters.remove(userId);
            }
        }
    }
}