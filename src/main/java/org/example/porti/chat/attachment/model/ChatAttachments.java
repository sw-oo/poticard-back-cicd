package org.example.porti.chat.attachment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.porti.chat.message.model.ChatMessage;
import org.example.porti.common.model.BaseEntity;

@Entity
@Getter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ChatAttachments extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(columnDefinition = "TEXT")
    private String filePath;

    private String fileName;

    private String fileType;

    private Long fileSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_message_idx")
    private ChatMessage chatMessage;
}
