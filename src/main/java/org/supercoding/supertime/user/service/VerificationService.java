package org.supercoding.supertime.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.supercoding.supertime.golbal.aws.service.ImageUploadService;
import org.supercoding.supertime.user.repository.AuthImageRepository;
import org.supercoding.supertime.user.repository.AuthStateRepository;
import org.supercoding.supertime.user.repository.UserRepository;
import org.supercoding.supertime.golbal.web.advice.CustomNoSuchElementException;
import org.supercoding.supertime.golbal.web.advice.CustomNotFoundException;
import org.supercoding.supertime.golbal.web.dto.CommonResponseDto;
import org.supercoding.supertime.user.util.UserValidation;
import org.supercoding.supertime.user.web.entity.AuthImageEntity;
import org.supercoding.supertime.user.web.entity.AuthStateEntity;
import org.supercoding.supertime.golbal.web.enums.Valified;
import org.supercoding.supertime.user.web.entity.user.UserEntity;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationService {
    private final UserRepository userRepository;
    private final ImageUploadService imageUploadService;
    private final AuthStateRepository authStateRepository;
    private final AuthImageRepository authImageRepository;

    private final UserValidation userValidation;

    /**
     * 기능 - 유저 인증 신청
     * @param user
     * @param image
     */
    @Transactional
    public void certificationRequest(User user, MultipartFile image) {
        UserEntity userEntity = userValidation.validateCertificationRequest(user.getUsername(), image);
        AuthStateEntity authStateEntity = userValidation.validateAuthState(userEntity.getUserId());

        validateCertificationState(userEntity.getValified());
        authStateEntity.setAuthImageId(addImage(image));

        setCertificationState(userEntity, authStateEntity);
    }

    private void validateCertificationState(Valified userValified) {
        switch (userValified){
            case DENIED:
                throw new DataIntegrityViolationException("인증 거부 상태입니다. 인증 재요청을 진행해 주세요.");
            case PENDING:
                throw new DataIntegrityViolationException("인증이 대기중입니다.");
            case COMPLETED:
                throw new DataIntegrityViolationException("이미 완료된 인증입니다.");
        }
    }

    /**
     * 기능 - 유저 인증 재요청
     * @param user
     * @param image
     */
    @Transactional
    public void certificationResubmission(User user, MultipartFile image) {
        UserEntity userEntity = userValidation.validateCertificationRequest(user.getUsername(), image);
        AuthStateEntity authStateEntity = userValidation.validateAuthState(userEntity.getUserId());

        validateCertificationStateForResubmission(userEntity.getValified());
        authStateEntity.setAuthImageId(addImage(image));

        setCertificationState(userEntity, authStateEntity);
    }

    private void validateCertificationStateForResubmission(Valified userValified) {
        switch (userValified){
            case NEEDED:
                throw new DataIntegrityViolationException("아직 인증요청을 보낸적 없는 유저입니다.");
            case PENDING:
                throw new DataIntegrityViolationException("인증이 대기중입니다.");
            case COMPLETED:
                throw new DataIntegrityViolationException("이미 완료된 인증입니다.");
        }
    }

    private Long addImage(MultipartFile image) {
        AuthImageEntity authImage = imageUploadService.uploadAuthImages(image,"authImage");
        authImageRepository.save(authImage);

        return authImage.getAuthImageCid();
    }

    private void setCertificationState(UserEntity userEntity, AuthStateEntity authStateEntity) {
        userEntity.setValified(Valified.PENDING);
        authStateEntity.setValified(Valified.PENDING);

        userRepository.save(userEntity);
        authStateRepository.save(authStateEntity);
    }
}
