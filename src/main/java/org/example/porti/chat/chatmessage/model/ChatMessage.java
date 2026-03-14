package org.example.porti.chat.chatmessage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.porti.chat.chatroom.model.ChatRoom;
import org.example.porti.common.model.BaseEntity;
import org.example.porti.user.model.User;

@Entity
@Getter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ChatMessage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_idx")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_idx")
    private User user;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    public void read() {
        this.isRead = true;
    }
}
