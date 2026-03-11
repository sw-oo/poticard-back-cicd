package org.example.porti.community.model;

import org.example.porti.common.model.BaseEntity;
import org.example.porti.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Community extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false, length = 30)
    private String category;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String contents;

    @Column(length = 500)
    private String tags;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean solved;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean anonymous;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int likesCount;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int commentCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

    @Version
    @ColumnDefault("0")
    private Long version;

    public void update(CommunityDto.RegReq dto) {
        this.category = dto.getCategory();
        this.title = dto.getTitle();
        this.contents = dto.getContents();
        this.tags = dto.getTags();
        this.anonymous = dto.isAnonymous();
    }

    public void increaseLikesCount() {
        this.likesCount = this.likesCount + 1;
    }

    public void decreaseLikesCount() {
        if (this.likesCount > 0) {
            this.likesCount = this.likesCount - 1;
        }
    }

    public void increaseCommentCount() {
        this.commentCount = this.commentCount + 1;
    }

    public void decreaseCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount = this.commentCount - 1;
        }
    }

    public void completeSolved() {
        this.solved = true;
    }
}