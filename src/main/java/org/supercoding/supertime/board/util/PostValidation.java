package org.supercoding.supertime.board.util;

import com.nimbusds.jose.util.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.supercoding.supertime.board.repository.BoardRepository;
import org.supercoding.supertime.board.repository.PostRepository;
import org.supercoding.supertime.board.web.entity.BoardEntity;
import org.supercoding.supertime.board.web.entity.PostEntity;
import org.supercoding.supertime.golbal.web.advice.CustomAccessDeniedException;
import org.supercoding.supertime.golbal.web.advice.CustomNoSuchElementException;
import org.supercoding.supertime.golbal.web.advice.CustomNotFoundException;
import org.supercoding.supertime.user.repository.UserRepository;
import org.supercoding.supertime.user.web.entity.user.UserEntity;

@RequiredArgsConstructor
@Component
public class PostValidation {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // 유저가 게시판에 작성 가능한지 검증하는 메서드
    public Pair<UserEntity, BoardEntity> validateUserWriteAccessToBoard(User user, Long boardCid) {
        UserEntity author = userRepository.findByUserId(user.getUsername())
                .orElseThrow(() -> new CustomNotFoundException("일치하는 유저가 존재하지 않습니다."));

        BoardEntity targetBoard = boardRepository.findById(boardCid)
                .orElseThrow(() -> new CustomNotFoundException("게시판이 존재하지 않습니다."));

        if (author.getBoardList().stream()
                .noneMatch(cid -> cid.equals(boardCid))) {
            throw new CustomAccessDeniedException("게시판 작성 권한이 없습니다.");
        }

        return Pair.of(author, targetBoard);
    }

    // 유저가 게시판에 접근 가능한지 검증하는 메서드
    public void validateUserAccessToBoard(UserEntity user, Long boardCid) {
        BoardEntity boardEntity = boardRepository.findById(boardCid)
                .orElseThrow(() -> new CustomNotFoundException("게시판이 존재하지 않습니다."));

        if(user.getBoardList().stream()
                .noneMatch(cid -> cid.equals(boardEntity.getBoardCid()))
        ) {
            throw new CustomAccessDeniedException("게시물 조회 권한이 없습니다.");
        }
    }

    // 유저 존재 여부를 검증하는 메서드
    public UserEntity validateUserExistence(String userName) {
        return userRepository.findByUserId(userName)
                .orElseThrow(() -> new CustomNotFoundException("유저가 존재하지 않습니다."));
    }

    // 게시물 존재 여부를 검증하는 메서드
    public PostEntity validatePostExistence(Long postCid) {
        return postRepository.findById(postCid)
                .orElseThrow(() -> new CustomNotFoundException("게시물이 존재하지 않습니다."));
    }

    // 게시물 수정 권한을 검증하는 메서드
    public void validatePostEditPermission(PostEntity targetPost, User user) {
        if (!targetPost.getUserEntity().getUserId().equals(user.getUsername())) {
            throw new CustomAccessDeniedException("권한이 없습니다.");
        }
    }

    // 게시판 조회 권한을 검증하는 메서드
    public void validateGetBoardPermission(UserEntity user, Long boardCid) {
        if(user.getBoardList().stream()
                .noneMatch(cid -> cid.equals(boardCid))
        ) {
            throw new CustomAccessDeniedException("게시판 조회 권한이 없습니다.");
        }
    }

    // 게시판이 비어있는지 검증하는 메서드
    public void validatePostListIsEmpty(Page<PostEntity> postList) {
        if(postList.isEmpty()){
            throw new CustomNoSuchElementException("게시판에 게시글이 없습니다.");
        }
    }
}
