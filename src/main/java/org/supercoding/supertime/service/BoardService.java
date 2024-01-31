package org.supercoding.supertime.service;

import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.supercoding.supertime.repository.BoardRepository;
import org.supercoding.supertime.repository.PostRepository;
import org.supercoding.supertime.repository.UserRepository;
import org.supercoding.supertime.web.dto.board.CreatePostRequestDto;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;
import org.supercoding.supertime.web.entity.board.BoardEntity;
import org.supercoding.supertime.web.entity.board.PostEntity;
import org.supercoding.supertime.web.entity.user.UserEntity;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {
    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommonResponseDto createPost(Long boardCid, CreatePostRequestDto createPostInfo) {
        // TODO
        // - 게시판 유무 확인
        // - 게시판 작성 권한 확인
        // - 게시물 생성
        BoardEntity targetBoard = boardRepository.findById(boardCid).orElseThrow(()-> new NotFoundException("게시판이 존재하지 않습니다."));
        // 시큐리티 적용시 토큰기반으로 검색하는 로직으로 수정
        UserEntity author = userRepository.findById(createPostInfo.getUserCid()).orElseThrow(()-> new NotFoundException("일치하는 유저가 존재하지 않습니다."));
        // TODO (희망사항) 권한 정보에 대한 칼럼을 추가하여 유저가 포함되어있는지 확인하는 구문 구현

        PostEntity newPost = PostEntity.builder()
                .boardEntity(targetBoard)
                .userEntity(author)
                .postTitle(createPostInfo.getPostTitle())
                .postContent(createPostInfo.getPostContent())
                .build();

        postRepository.save(newPost);

        return CommonResponseDto.builder()
                .code(200)
                .success(true)
                .message("게시물 작성이 성공적으로 이루어졌습니다.")
                .build();
    }
}
