package org.supercoding.supertime.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.supercoding.supertime.repository.AuthImageRepository;
import org.supercoding.supertime.repository.AuthStateRepository;
import org.supercoding.supertime.repository.UserRepository;
import org.supercoding.supertime.web.advice.CustomNotFoundException;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;
import org.supercoding.supertime.web.entity.auth.AuthImageEntity;
import org.supercoding.supertime.web.entity.auth.AuthStateEntity;
import org.supercoding.supertime.web.entity.enums.Valified;
import org.supercoding.supertime.web.entity.user.UserEntity;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationService {
    private final UserRepository userRepository;
    private final ImageUploadService imageUploadService;
    private final AuthStateRepository authStateRepository;
    private final AuthImageRepository authImageRepository;

    @Transactional
    public CommonResponseDto apply(User user, MultipartFile image){
        UserEntity loggedInUser = userRepository.findByUserId(user.getUsername())
                .orElseThrow(()-> new CustomNotFoundException("유저가 존재하지 않습니다."));

        AuthStateEntity authStateEntity = authStateRepository.findByUserId(loggedInUser.getUserId()).orElse(null);

        if(authStateEntity!=null || loggedInUser.getValified()!=Valified.COMPLETED) {
            switch (loggedInUser.getValified()){
                case NEEDED, PENDING:
                    throw new DataIntegrityViolationException("인증이 대기중입니다.");
                case COMPLETED:
                    throw new DataIntegrityViolationException("이미 완료된 인증입니다.");
                case DENIED:
                    throw new DataIntegrityViolationException("인증이 거부되었습니다.");
            }
        }

        AuthStateEntity newAuth = AuthStateEntity.builder()
                .userId(loggedInUser.getUserId())
                .valified(Valified.PENDING)
                .build();

        if(image!=null){
            AuthImageEntity img = imageUploadService.uploadAuthImages(image,"authImage");
            authImageRepository.save(img);
            newAuth.setAuthImageId(img.getAuthImageCid());
        }

        loggedInUser.setValified(Valified.PENDING);
        userRepository.save(loggedInUser);
        authStateRepository.save(newAuth);

        return CommonResponseDto.successResponse("인증 신청 성공");
    }

}