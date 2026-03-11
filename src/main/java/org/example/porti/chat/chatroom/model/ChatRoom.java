package org.example.porti.chat.chatroom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.porti.common.model.BaseEntity;
import org.example.porti.user.model.User;

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

    public User getOpponent(Long currentUserIdx) {
        return this.hostUser.getIdx().equals(currentUserIdx) ? this.guestUser : this.hostUser;
    }
}
