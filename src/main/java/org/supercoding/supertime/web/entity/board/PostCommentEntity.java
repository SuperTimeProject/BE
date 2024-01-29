package org.supercoding.supertime.web.entity.board;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.supercoding.supertime.web.entity.user.UserEntity;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "post_comment_table")
public class PostCommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_comment_cid")
    @Schema(description = "게시판 댓글 식별번호", example = "1")
    private Long postImageCid;

    @ManyToOne
    @JoinColumn(name = "user_cid",referencedColumnName = "user_cid", nullable = false)
    @Schema(description = "사용자 식별번호")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "post_cid",referencedColumnName = "post_cid", nullable = false)
    @Schema(description = "사용자 식별번호")
    private PostEntity postEntity;

    @Column(name = "content", columnDefinition = "TEXT")
    @Schema(description = "댓글 내용", example = "예시 댓글 내용")
    private String postImageFileName;

}
