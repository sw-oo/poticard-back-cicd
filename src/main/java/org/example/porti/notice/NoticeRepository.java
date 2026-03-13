package org.example.porti.notice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.porti.notice.model.Notice;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Page<Notice> findAllByOrderByCreatedAtDesc(Pageable pageable);
}