package org.example.porti.notification;

import lombok.RequiredArgsConstructor;
import org.example.porti.common.model.BaseResponse;
import org.example.porti.notification.model.NotificationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/push")
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/sub")
    public ResponseEntity subscribe(@RequestBody NotificationDto.Subscribe dto) {

        notificationService.subscribe(dto);
        return ResponseEntity.ok().body(BaseResponse.success(dto));
    }
}
