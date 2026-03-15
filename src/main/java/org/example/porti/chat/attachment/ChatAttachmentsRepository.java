package org.example.porti.chat.attachment;

import org.example.porti.chat.attachment.model.ChatAttachments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatAttachmentsRepository extends JpaRepository<ChatAttachments, Long> {
}
