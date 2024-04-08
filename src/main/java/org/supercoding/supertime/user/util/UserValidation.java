package org.supercoding.supertime.user.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.supercoding.supertime.golbal.web.advice.CustomDataIntegerityCiolationException;
import org.supercoding.supertime.golbal.web.advice.CustomNoSuchElementException;
import org.supercoding.supertime.golbal.web.advice.CustomNotFoundException;
import org.supercoding.supertime.inquiry.repository.InquiryRepository;
import org.supercoding.supertime.inquiry.web.entity.InquiryEntity;
import org.supercoding.supertime.semester.repository.SemesterRepository;
import org.supercoding.supertime.semester.web.entity.SemesterEntity;
import org.supercoding.supertime.user.repository.UserRepository;
import org.supercoding.supertime.user.web.entity.user.UserEntity;

@Component
@RequiredArgsConstructor
public class UserValidation {

    private final UserRepository userRepository;
    private final InquiryRepository inquiryRepository;
    private final SemesterRepository semesterRepository;

    public UserEntity validateExistUser(String username) {
        return userRepository.findByUserId(username)
                .orElseThrow(()-> new CustomNotFoundException("로그인된 유저가 존재하지 않습니다."));
    }

    public void validateDuplicateNickname(String newNickname) {
        if(userRepository.existsByUserNickname(newNickname)) {
            throw new CustomDataIntegerityCiolationException("이미 사용중인 닉네임입니다.");
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
}
