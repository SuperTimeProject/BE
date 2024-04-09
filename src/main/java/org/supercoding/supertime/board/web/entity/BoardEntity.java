package org.supercoding.supertime.board.web.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.supercoding.supertime.user.web.entity.user.UserEntity;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "board_table")
public class BoardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_cid")
    @Schema(description = "게시판 식별 번호", example = "1")
    private Long boardCid;

    @Column(name = "board_name")
    @Schema(description = "게시판 이름", example = "자유게시판")
    private String boardName;

    @ManyToMany(mappedBy = "boardList")
    private List<UserEntity> userList;

    public static BoardEntity toSemesterBoard(int semesterName) {
        return BoardEntity.builder()
                .boardName("기수 게시판 ("+semesterName+")")
                .build();
    }

    public static BoardEntity toStudyBoard(int semesterName, String part) {
        return BoardEntity.builder()
                .boardName("스터디 게시판 ("+semesterName+part+")")
                .build();
    }
}
