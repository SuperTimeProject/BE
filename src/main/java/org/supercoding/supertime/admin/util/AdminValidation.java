package org.supercoding.supertime.admin.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.supercoding.supertime.golbal.web.advice.CustomAccessDeniedException;
import org.supercoding.supertime.golbal.web.advice.CustomDataIntegerityCiolationException;
import org.supercoding.supertime.golbal.web.advice.CustomNoSuchElementException;
import org.supercoding.supertime.golbal.web.advice.CustomNotFoundException;
import org.supercoding.supertime.golbal.web.enums.InquiryClosed;
import org.supercoding.supertime.golbal.web.enums.Roles;
import org.supercoding.supertime.golbal.web.enums.Verified;
import org.supercoding.supertime.inquiry.repository.InquiryRepository;
import org.supercoding.supertime.inquiry.web.entity.InquiryEntity;
import org.supercoding.supertime.user.repository.AuthImageRepository;
import org.supercoding.supertime.user.repository.AuthStateRepository;
import org.supercoding.supertime.user.repository.UserRepository;
import org.supercoding.supertime.user.web.entity.AuthImageEntity;
import org.supercoding.supertime.user.web.entity.AuthStateEntity;
import org.supercoding.supertime.user.web.entity.user.UserEntity;

@RequiredArgsConstructor
@Component
public class AdminValidation {
    private final UserRepository userRepository;
    private final AuthStateRepository authStateRepository;
    private final AuthImageRepository authImageRepository;
    private final InquiryRepository inquiryRepository;

    public void validateAdminRole(User user) {
        UserEntity targetUser = userRepository.findByUserId(user.getUsername())
                .orElseThrow(()-> new CustomNotFoundException("유저가 존재하지 않습니다."));

        if(targetUser.getRoles() != Roles.ROLE_ADMIN) {
            throw new CustomAccessDeniedException("관지자 권한이 없습니다.");
        };
    }

    public UserEntity findUserEntityByUserCid(long userCid) {
        return userRepository.findByUserCid(userCid)
                .orElseThrow(()-> new CustomNotFoundException("일치하는 유저가 존재하지 않습니다."));
    }

    public Page<UserEntity> validateVerifiedUser(Verified verified, Pageable pageable) {
        Page<UserEntity> userEntities = userRepository.findAllByValified(verified, pageable);

        if(userEntities.isEmpty()) {
            throw new CustomNoSuchElementException("인증 대기중인 유저가 없습니다.");
        }

        return userEntities;
    }

    public AuthStateEntity validateGetAuthState(String userId) {
        return authStateRepository.findByUserId(userId)
                .orElseThrow(()-> new CustomNoSuchElementException("[GET_USER_VALIFIED]일치하는 인증요청이 존재하지 않습니다."));
    }

    public UserEntity findUserEntityByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(()-> new CustomNotFoundException("일치하는 유저가 존재하지 않습니다."));
    }

    public AuthImageEntity validateExistAuthImage(long authImageId) {
        return authImageRepository.findById(authImageId)
                .orElseThrow(()-> new CustomNoSuchElementException("인증요청의 이미지 값이 존재하지 않습니다."));
    }

    public Page<InquiryEntity> validateGetInquiry(Pageable pageable, InquiryClosed isclosed) {
        Page<InquiryEntity> inquiryList = inquiryRepository.findAllByIsClosed(isclosed, pageable);

        if(inquiryList.isEmpty()) {
            switch (isclosed){
                case OPEN -> throw new CustomNoSuchElementException("열려있는 문의가 없습니다.");
                case CLOSED -> throw new CustomNoSuchElementException("답변완료된 문의가 없습니다.");
            }
        }

        return inquiryList;
    }

    public InquiryEntity validateAnsweredInquiry(Long inquiryCid) {
        InquiryEntity inquiry = inquiryRepository.findById(inquiryCid)
                .orElseThrow(() -> new CustomNotFoundException("일치하는 문의가 존재하지 않습니다."));

        if(inquiry.getAnswer() != null) {
            throw new CustomDataIntegerityCiolationException("이미 답변 완료된 문의 입니다.");
        }

        return inquiry;
    }

    public InquiryEntity validateExistInquiry(Long inquiryCid) {
        return inquiryRepository.findById(inquiryCid)
                .orElseThrow(() -> new CustomNotFoundException("일치하는 문의가 존재하지 않습니다."));

    }
}
