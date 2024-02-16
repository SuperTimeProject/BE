package org.supercoding.supertime.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.supercoding.supertime.repository.*;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerifyService {
    private final UserRepository userRepository;
    private final SemesterRepository semesterRepository;
    private final UserProfileRepository userProfileRepository;
    private final PostRepository postRepository;
    private final InquiryRepository inquiryRepository;
    private final InquiryImageRepository inquiryImageRepository;
    private final ImageUploadService imageUploadService;

    public CommonResponseDto requestAuthentication(MultipartFile certificationImage){



        return CommonResponseDto.successResponse("인증 요청이 성공했습니다.");
    }


}
