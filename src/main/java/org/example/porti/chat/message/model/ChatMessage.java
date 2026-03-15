package org.example.porti.chat.message.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.porti.chat.attachment.model.ChatAttachments;
import org.example.porti.chat.room.model.ChatRoom;
import org.example.porti.common.model.BaseEntity;
import org.example.porti.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ChatMessage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private String contents;

    @Enumerated(EnumType.STRING)
    private ContentsType contentsType;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_idx")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_idx")
    private User user;

    @OneToMany(mappedBy = "chatMessage", cascade = CascadeType.ALL)
    private List<ChatAttachments> attachments = new ArrayList<>();
}
