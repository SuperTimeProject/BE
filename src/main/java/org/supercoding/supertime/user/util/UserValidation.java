package org.supercoding.supertime.user.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.supercoding.supertime.board.repository.BoardRepository;
import org.supercoding.supertime.board.web.entity.BoardEntity;
import org.supercoding.supertime.chat.repository.ChatRoomRepository;
import org.supercoding.supertime.chat.web.entity.ChatRoomEntity;
import org.supercoding.supertime.golbal.web.advice.*;
import org.supercoding.supertime.inquiry.repository.InquiryRepository;
import org.supercoding.supertime.inquiry.web.entity.InquiryEntity;
import org.supercoding.supertime.semester.repository.SemesterRepository;
import org.supercoding.supertime.semester.web.entity.SemesterEntity;
import org.supercoding.supertime.user.repository.AuthStateRepository;
import org.supercoding.supertime.user.repository.UserProfileRepository;
import org.supercoding.supertime.user.repository.UserRepository;
import org.supercoding.supertime.user.web.dto.LoginRequestDto;
import org.supercoding.supertime.user.web.entity.AuthStateEntity;
import org.supercoding.supertime.user.web.entity.user.UserEntity;
import org.supercoding.supertime.user.web.entity.user.UserProfileEntity;

@Component
@RequiredArgsConstructor
public class UserValidation {

    private final UserRepository userRepository;
    private final InquiryRepository inquiryRepository;
    private final SemesterRepository semesterRepository;
    private final BoardRepository boardRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserProfileRepository userProfileRepository;
    private final AuthStateRepository authStateRepository;

    private final PasswordEncoder passwordEncoder;

    public UserEntity validateExistUser(String username) {
        return userRepository.findByUserId(username)
                .orElseThrow(()-> new CustomNotFoundException("로그인된 유저가 존재하지 않습니다."));
    }

    public void validateLogin(LoginRequestDto loginInfo) {
        UserEntity userEntity = userRepository.findByUserId(loginInfo.getUserId())
                .orElseThrow(() -> new CustomNotFoundException("일치하는 유저가 존재하지 않습니다."));

        if(userEntity.getIsDeleted() == 1) {
            throw new CustomAccessDeniedException("탈퇴 처리된 유저입니다.");
        }

        if(!passwordEncoder.matches(loginInfo.getUserPassword(), userEntity.getUserPassword())){
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
    }


    public Page<InquiryEntity> validateExistUserInquiry(int page, UserEntity user) {
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<InquiryEntity> inquiryList = inquiryRepository.findAllByUser(user, pageable);

        if(inquiryList.isEmpty()) {
            throw new CustomNoSuchElementException("문의가 존재하지 않습니다.");
        }

        return inquiryList;
    }

    public InquiryEntity validateExistInquiry(Long inquiryCid) {
        return inquiryRepository.findById(inquiryCid)
                .orElseThrow(()-> new CustomNotFoundException("해당 문의가 존재하지 않습니다."));
    }

    public SemesterEntity validateExistSemester(Long semesterCid) {
        return semesterRepository.findById(semesterCid)
                .orElseThrow(()-> new CustomNotFoundException("기수가 존재하지 않습니다."));
    }

    public void validateExistProfileImage(UserEntity user) {
        if(user.getUserProfileCid() == null) {
            throw new CustomNotFoundException("프로필 이미지가 존재하지 않습니다.");
        }
    }

    public void validateDuplicateEmail(String email) {
        if (userRepository.existsByUserId(email)) {
            throw new CustomDataIntegerityCiolationException("이미 사용중인 이메일입니다.");
        }
    }

    public void validateDuplicateNickname(String nickname) {
        if (userRepository.existsByUserNickname(nickname)) {
            throw new CustomDataIntegerityCiolationException("이미 사용중인 닉네임입니다.");
        }
    }

    public SemesterEntity findSemester(long semesterCid) {
        return semesterRepository.findById(semesterCid)
                .orElseThrow(() -> new CustomNotFoundException("기수가 존재하지 않습니다."));
    }

    public BoardEntity findBoard(String boardName) {
        return boardRepository.findByBoardName(boardName)
                .orElseThrow(()-> new CustomNotFoundException("일치하는 게시판이 존재하지 않습니다."));
    }

    public BoardEntity validateExistBoard(Long boardCid) {
        return boardRepository.findById(boardCid)
                .orElseThrow(()-> new CustomNotFoundException("일치하는 게시판이 존재하지 않습니다."));
    }

    public ChatRoomEntity validateExistChatRoom(String chatroomName) {
        return chatRoomRepository.findByChatRoomName(chatroomName)
                .orElseThrow(()-> new CustomNotFoundException("일치하는 채팅방이 존재하지 않습니다."));
    }

    public UserProfileEntity findUserProfile(long userProfileCid) {
        return userProfileRepository.findById(userProfileCid)
                .orElseThrow(()->new CustomNotFoundException("찾는 프로필이 존재하지 않습니다."));
    }

    public UserEntity validateCertificationRequest(String username, MultipartFile image) {
        if(image == null) {
            throw new CustomMissingFileException("인증 요청 이미지가 없습니다");
        }

        return userRepository.findByUserId(username)
                .orElseThrow(()-> new CustomNotFoundException("인증 신청하는 유저가 존재하지 않습니다."));
    }

    public AuthStateEntity validateAuthState(String userId) {
        return authStateRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomNotFoundException("일치하는 인증상태 정보가 존재하지 않습니다."));
    }

}
