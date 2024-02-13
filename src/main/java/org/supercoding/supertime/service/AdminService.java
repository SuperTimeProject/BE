package org.supercoding.supertime.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.supercoding.supertime.config.security.TokenProvider;
import org.supercoding.supertime.repository.*;
import org.supercoding.supertime.web.advice.CustomNotFoundException;
import org.supercoding.supertime.web.dto.auth.LoginRequestDto;
import org.supercoding.supertime.web.dto.board.getBoardPost.GetBoardPostDetailDto;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;
import org.supercoding.supertime.web.dto.inquiry.GetUnclosedInquiryDetailDto;
import org.supercoding.supertime.web.dto.inquiry.GetUnclosedInquiryResponseDto;
import org.supercoding.supertime.web.entity.Inquiry.InquiryEntity;
import org.supercoding.supertime.web.entity.enums.InquiryClosed;
import org.supercoding.supertime.web.entity.enums.Valified;
import org.supercoding.supertime.web.entity.user.UserEntity;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BoardRepository boardRepository;
    private final SemesterRepository semesterRepository;
    private final UserProfileRepository userProfileRepository;
    private final InquiryRepository inquiryRepository;

    public CommonResponseDto verification(String userName) {
        UserEntity user = userRepository.findByUserId(userName)
                .orElseThrow(()-> new CustomNotFoundException("일치하는 유저가 존재하지 않습니다."));

        user.setValified(Valified.valueOf("COMPLETED"));
        userRepository.save(user);

        return CommonResponseDto.successResponse("회원 인증에 성공했습니다.");
    }

    public GetUnclosedInquiryResponseDto getUnclosedInquiry(){
        List<GetUnclosedInquiryDetailDto> inquiryList = inquiryRepository.findAllByIsClosed(InquiryClosed.OPEN);

        if(inquiryList.isEmpty()){
            throw new CustomNotFoundException("문의내용이 없습니다.");
        }


        return GetUnclosedInquiryResponseDto.builder()
                .code(200)
                .success(true)
                .message("문의 조회에 성공했습니다.")
                .inquiryList(inquiryList)
                .build();
    }

    public CommonResponseDto answerInquiry(Long inquiryCid, String inquiryContent) {
        InquiryEntity inquiryEntity = inquiryRepository.findById(inquiryCid)
                .orElseThrow(()-> new CustomNotFoundException("해당 문의가 존재하지 않습니다."));

        inquiryEntity.setAnswer(inquiryContent);
        inquiryEntity.setIsClosed(InquiryClosed.CLOSED);

        inquiryRepository.save(inquiryEntity);

        return CommonResponseDto.successResponse("문의 답변에 성공했습니다.");
    }
}
