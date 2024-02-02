package org.supercoding.supertime.service;

import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.supercoding.supertime.repository.BoardRepository;
import org.supercoding.supertime.repository.SemesterRepository;
import org.supercoding.supertime.repository.UserRepository;
import org.supercoding.supertime.web.dto.auth.LoginRequestDto;
import org.supercoding.supertime.web.dto.auth.SignupRequestDto;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;
import org.supercoding.supertime.web.entity.SemesterEntity;
import org.supercoding.supertime.web.entity.board.BoardEntity;
import org.supercoding.supertime.web.entity.enums.Roles;
import org.supercoding.supertime.web.entity.user.UserEntity;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final SemesterRepository semesterRepository;
    public CommonResponseDto login(LoginRequestDto loginInfo) {
        UserEntity user = userRepository.findByUserId(loginInfo.getUserId()).orElseThrow(()-> new NotFoundException("일치하는 유저가 존재하지 않습니다."));
        int isDeleted = user.getIsDeleted();

        if(isDeleted == 1) {
            throw new NotFoundException("탈퇴 처리된 유저입니다.");
        };
        // TODO - security 구현 후 'passwordEncoder.matches'를 사용한 비밀번호 확인 절차로 수정
        if(!user.getUserPassword().equals(loginInfo.getUserPassword())){
            // TODO - security에서 제공하는 예외처리로 수정
            throw new NotFoundException("비밀번호가 일치하지 않습니다.");
            // throw new BadCredentialException("비밀번호가 일치하지 않습니다.");
        }

        // TODO - 토큰 발급 과정 추가

        return CommonResponseDto.builder()
                .code(200)
                .success(true)
                .message("로그인에 성고하였습니다.")
                .build();
    }

    public CommonResponseDto signup(SignupRequestDto signupInfo) {
        Boolean existUserId = userRepository.existsByUserId(signupInfo.getUserName());
        if(existUserId){
            throw new DataIntegrityViolationException("중복된 아이디가 존재합니다.");
        }

        Boolean existUserNickname = userRepository.existsByUserNickname(signupInfo.getUserNickname());
        if(existUserId){
            throw new DataIntegrityViolationException("중복된 닉네임이 존재합니다.");
        }

        List<BoardEntity> userBoard = new ArrayList<>();
//        List<String> boardList = Arrays.asList("전체 게시판", "커뮤니티 게시판");
        SemesterEntity userSemester = semesterRepository.findById(signupInfo.getSemesterCid()).orElseThrow(()->new NotFoundException("기수가 존재하지 않습니다."));
        String[] boardList = {"전체 게시판", "커뮤니티 게시판", "기수 게시판 ("+userSemester.getSemesterName().toString()+")"};
        log.info("보드리스트" + boardList);

        for(String boardName : boardList){
            BoardEntity board = boardRepository.findByBoardName(boardName);
            userBoard.add(board);
        }

        UserEntity signupUser = UserEntity.builder()
                .userId(signupInfo.getUserId())
                .userNickname(signupInfo.getUserNickname())
                .userPassword(signupInfo.getUserPassword())
                .semester(signupInfo.getSemesterCid())
                .boardList(userBoard)
                .roles(Roles.ROLE_USER)
                .isDeleted(0)
                .varified(0)
                .build();

        userRepository.save(signupUser);

        return CommonResponseDto.builder()
                .success(true)
                .code(200)
                .message("회원가입에 성공했습니다.")
                .build();
    }

    public CommonResponseDto emailDuplicateTest(String userEmail) {
        Boolean duplicateResult = userRepository.existsByUserId(userEmail);
        if (duplicateResult){
            throw new DataIntegrityViolationException("이미 사용중인 이메일입니다.");
        }

        return CommonResponseDto.builder()
                .success(true)
                .code(200)
                .message("사용 가능한 email입니다.")
                .build();
    }

    public CommonResponseDto nicknameDuplicateTest(String nickname) {
        Boolean duplicateResult = userRepository.existsByUserNickname(nickname);
        if (duplicateResult){
            throw new DataIntegrityViolationException("이미 사용중인 닉네임입니다.");
        }

        return CommonResponseDto.builder()
                .success(true)
                .code(200)
                .message("사용 가능한 닉네임입니다.")
                .build();
    }
}
