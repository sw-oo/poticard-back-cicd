package org.example.porti.chat.room.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.porti.chat.attachment.model.ChatAttachments;
import org.example.porti.chat.message.model.ChatMessage;
import org.example.porti.common.model.BaseEntity;
import org.example.porti.user.model.User;

import java.util.List;

@Entity
@Getter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ChatRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idx;

    @ManyToOne // 호스트 유저와 1:N 관계
    @JoinColumn(name = "host_user_idx")
    private User hostUser;

    @ManyToOne // 게스트 유저와 1:N 관계
    @JoinColumn(name = "guest_user_idx")
    private User guestUser;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages;

    public User getOpponent(Long currentUserIdx) {
        if (this.hostUser != null && this.hostUser.getIdx().equals(currentUserIdx)) {
            return this.guestUser;
        }
        // 내가 게스트라면 호스트를 반환 (호스트가 나갔으면 null 반환)
        return this.hostUser;
    }

    public void removeHost() {
        this.hostUser = null;
    }

    public void removeGuest() {
        this.guestUser = null;
    }
}
