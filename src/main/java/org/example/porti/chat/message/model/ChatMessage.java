package org.example.porti.chat.message.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.porti.chat.attachment.model.ChatAttachments;
import org.example.porti.chat.room.model.ChatRoom;
import org.example.porti.common.model.BaseEntity;
import org.example.porti.user.model.User;

import java.util.List;

@Entity
@Getter @Builder @Setter
@NoArgsConstructor @AllArgsConstructor
public class ChatMessage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private String contents;

    @Enumerated(EnumType.STRING)
    @Column(name = "contents_type", length = 20)
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
    private List<ChatAttachments> attachments;
}
