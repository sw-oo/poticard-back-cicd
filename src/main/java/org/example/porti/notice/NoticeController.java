package org.example.porti.notice;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.example.porti.common.model.BaseResponse;
import org.example.porti.notice.model.NoticeDto;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 공지 생성 (Postman용)
    @PostMapping("/create")
    public BaseResponse<Long> createNotice(@RequestBody NoticeDto.Req req) {
        Long noticeIdx = noticeService.create(req);
        return BaseResponse.success(noticeIdx);
    }

    // 공지 리스트 조회
    @GetMapping("/list")
    public BaseResponse<Page<NoticeDto.ListRes>> getNoticeList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NoticeDto.ListRes> result = noticeService.NoticeList(pageable);
        return BaseResponse.success(result);
    }

    // 공지 상세 조회
    @GetMapping("/{noticeIdx}")
    public BaseResponse<NoticeDto.Res> getNoticeDetail(@PathVariable Long noticeIdx) {
        NoticeDto.Res result = noticeService.NoticeDetail(noticeIdx);
        return BaseResponse.success(result);
    }

    // 공지 삭제 (Postman용)
    @DeleteMapping("/{noticeIdx}")
    public BaseResponse<String> deleteNotice(@PathVariable Long noticeIdx) {
        noticeService.deleteNotice(noticeIdx);
        return BaseResponse.success("공지사항 삭제 성공");
    }
}