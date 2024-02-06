package org.supercoding.supertime.service.user;

import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.supercoding.supertime.config.security.TokenProvider;
import org.supercoding.supertime.repository.*;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;
import org.supercoding.supertime.web.dto.user.getUserDto.GetUserPageResponseDto;
import org.supercoding.supertime.web.dto.user.getUserDto.UserProfileDto;
import org.supercoding.supertime.web.dto.user.getUserDto.UserSemesterDto;
import org.supercoding.supertime.web.entity.SemesterEntity;
import org.supercoding.supertime.web.entity.board.BoardEntity;
import org.supercoding.supertime.web.entity.board.PostEntity;
import org.supercoding.supertime.web.entity.user.UserEntity;
import org.supercoding.supertime.web.entity.user.UserProfileEntity;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BoardRepository boardRepository;
    private final SemesterRepository semesterRepository;
    private final UserProfileRepository userProfileRepository;
    private final PostRepository postRepository;

    public GetUserPageResponseDto GetUserInfo(User user){
        UserEntity loggedInUser = userRepository.findByUserId(user.getUsername())
                .orElseThrow(()-> new NotFoundException("유저가 존재하지 않습니다."));

        List<Long> boardList = new ArrayList<>();

        for(BoardEntity board:loggedInUser.getBoardList()){
            boardList.add(board.getBoardCid());
        }

        SemesterEntity semesterEntity = semesterRepository.findById(loggedInUser.getSemester())
                .orElseThrow(()->new NotFoundException("기수가 존재하지 않습니다."));

        UserSemesterDto semester = UserSemesterDto.builder()
                .semesterCid(semesterEntity.getSemesterCid())
                .semesterDetailName(semesterEntity.getSemesterDetailName())
                .isFull(semesterEntity.getIsFull())
                .build();

        UserProfileDto userProfile = null;
        if(loggedInUser.getUserProfileCid() != null){
            UserProfileEntity userProfileEntity = userProfileRepository.findById(loggedInUser.getUserProfileCid())
                    .orElseThrow(()->new NotFoundException("찾는 프로필이 존재하지 않습니다."));

            userProfile = UserProfileDto.builder()
                    .userProfileCid(userProfileEntity.getUserProfileCid())
                    .userProfileFileName(userProfileEntity.getUserProfileFileName())
                    .userProfileFilePath(userProfileEntity.getUserProfileFilePath())
                    .build();
        }

        List<PostEntity> posts = postRepository.findAllByUserEntity_UserCid(loggedInUser.getUserCid());

        GetUserPageResponseDto responseDto
                = GetUserPageResponseDto.builder()
                .userId(loggedInUser.getUserId())
                .part(loggedInUser.getPart())
                .userName(loggedInUser.getUserName())
                .userNickname(loggedInUser.getUserNickname())
                .part(loggedInUser.getPart())
                .posts(posts)
                .userProfile(userProfile)
                .build();

        log.debug("GetUserPageResponseDto 성공" +responseDto);

        return responseDto;
    }


    public CommonResponseDto editUserInfo() {
        return CommonResponseDto.builder()
                .success(true)
                .code(200)
                .message("유저 정보 수정에 성공했습니다.")
                .build();
    }

    public CommonResponseDto getInquiryHistory() {
        return CommonResponseDto.builder()
                .success(true)
                .code(200)
                .message("문의하기 기록 조회에 성공했습니다.")
                .build();
    }

    public CommonResponseDto inquiry() {
        return CommonResponseDto.builder()
                .success(true)
                .code(200)
                .message("문의하기에 성공했습니다.")
                .build();
    }
}
