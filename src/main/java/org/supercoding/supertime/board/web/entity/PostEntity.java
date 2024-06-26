package org.supercoding.supertime.board.web.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.supercoding.supertime.board.web.dto.CreatePostRequestDto;
import org.supercoding.supertime.golbal.web.entity.TimeEntity;
import org.supercoding.supertime.user.web.entity.user.UserEntity;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "post_table")
public class PostEntity extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_cid")
    @Schema(description = "게시물 식별번호", example = "1")
    private Long postCid;

    @ManyToOne
    @JoinColumn(name = "board_cid", referencedColumnName = "board_cid", nullable = false)
    @Schema(description = "게시판 식별번호")
    private BoardEntity boardEntity;

    @ManyToOne
    @JoinColumn(name = "user_cid", referencedColumnName = "user_cid", nullable = false)
    @Schema(description = "사용자 식별번호")
    private UserEntity userEntity;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<PostImageEntity> postImages;

    @Column(name = "post_title", columnDefinition = "TEXT")
    @Schema(description = "게시물 제목", example = "게시물 1")
    private String postTitle;

    @Column(name = "post_content")
    @Schema(description = "게시물 내용", example = "1번 게시물 내용 예시")
    private String postContent;

    @Column(name = "post_view")
    @Schema(description = "게시물 조회수", example = "39")
    private int postView;

    public void updatePostView(){
        this.postView++;
    }

    public static PostEntity create(final BoardEntity board, final UserEntity user, final CreatePostRequestDto postInfo){
        return PostEntity.builder()
                .boardEntity(board)
                .userEntity(user)
                .postTitle(postInfo.getPostTitle())
                .postContent(postInfo.getPostContent())
                .build();
    }
}
