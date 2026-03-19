package org.example.porti.community.comment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.porti.common.model.BaseEntity;
import org.example.porti.community.model.Community;
import org.example.porti.user.model.User;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CommunityComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_idx", nullable = false)
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", nullable = false)
    private User user;

    public void update(String contents) {
        this.contents = contents;
    }
}