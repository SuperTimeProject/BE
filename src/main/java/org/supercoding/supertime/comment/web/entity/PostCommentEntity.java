package org.supercoding.supertime.comment.web.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.supercoding.supertime.comment.web.dto.request.CreateCommentReqDto;
import org.supercoding.supertime.golbal.web.entity.TimeEntity;
import org.supercoding.supertime.board.web.entity.PostEntity;
import org.supercoding.supertime.user.web.entity.user.UserEntity;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "post_comment_table")
public class PostCommentEntity extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_comment_cid")
    @Schema(description = "게시판 댓글 식별번호", example = "1")
    private Long postCommentCid;

    @Schema(description = "사용자 식별번호")
    private Long userCid;

    @Schema(description = "사용자 식별번호")
    private Long postCid;

    @Column(name = "content", columnDefinition = "TEXT")
    @Schema(description = "댓글 내용", example = "예시 댓글 내용")
    private String comment;

    public static PostCommentEntity from(Long userCid, CreateCommentReqDto commentInfo) {
        return PostCommentEntity.builder()
                .userCid(userCid)
                .postCid(commentInfo.getPostCid())
                .comment(commentInfo.getContent())
                .build();
    }
}
