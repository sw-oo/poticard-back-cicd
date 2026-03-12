package org.example.porti.notice;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.porti.notice.model.Notice;
import org.example.porti.notice.model.NoticeDto;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    // 공지 생성(Postman용 - 공지 작성 기능은 구현 안 할 예정)
    @Transactional
    public Long create(NoticeDto.Req req) {
        Notice notice = req.toEntity();
        return noticeRepository.save(notice).getIdx();
    }

    // 공지 리스트 조회
    @Transactional(readOnly = true)
    public Page<NoticeDto.ListRes> NoticeList(Pageable pageable) {
        return noticeRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(NoticeDto.ListRes::from);
    }

    // 공지 상세 조회
    @Transactional(readOnly = true)
    public NoticeDto.Res NoticeDetail(Long noticeIdx) {
        Notice notice = noticeRepository.findById(noticeIdx)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 공지사항입니다."));

        return NoticeDto.Res.from(notice);
    }

    // 공지 삭제 (Postman용 - 공지 작성 기능은 구현 안 할 예정)
    @Transactional
    public void deleteNotice(Long noticeIdx) {
        noticeRepository.deleteById(noticeIdx);
    }
}