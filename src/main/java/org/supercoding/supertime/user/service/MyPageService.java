package org.supercoding.supertime.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.supercoding.supertime.board.web.entity.PostImageEntity;
import org.supercoding.supertime.golbal.aws.service.ImageUploadService;
import org.supercoding.supertime.golbal.web.advice.CustomAccessDeniedException;
import org.supercoding.supertime.golbal.web.enums.Part;
import org.supercoding.supertime.inquiry.repository.InquiryRepository;
import org.supercoding.supertime.semester.web.entity.SemesterEntity;
import org.supercoding.supertime.user.repository.UserProfileRepository;
import org.supercoding.supertime.user.repository.UserRepository;
import org.supercoding.supertime.user.util.UserValidation;
import org.supercoding.supertime.user.web.entity.user.UserEntity;
import org.supercoding.supertime.user.web.entity.user.UserProfileEntity;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyPageService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final ImageUploadService imageUploadService;

    private final UserValidation userValidation;

    final int MILLISECONDS_PER_DAY = 1000 * 60 * 60 * 24;
    final int SELECTABLE_START_DATE = 14;
    final int SELECTABLE_END_DATE = 21;

    /**
     * 기능 - 유저 프로필 이미지 수정
     *
     * @param user
     * @param newImage
     *
     * @return void
     */
    public void editProfileImage(
            User user,
            MultipartFile newImage
    ) {
        final String oldProfilePath ;
        UserEntity loggedUser = userValidation.validateExistUser(user.getUsername());
        oldProfilePath = (loggedUser.getUserProfileCid() != null)
                ? deleteOldProfile(loggedUser.getUserCid()) : null;

        Long newProfileCid = uploadNewProfile(newImage);
        loggedUser.setUserProfileCid(newProfileCid);

        userRepository.save(loggedUser);
        if(oldProfilePath != null) {
            deleteImageFromS3(oldProfilePath);
        }
    }

    private Long uploadNewProfile(MultipartFile profileImage) {
        UserProfileEntity newProfile = imageUploadService.uploadUserProfileImages(profileImage, "profile");
        userProfileRepository.save(newProfile);

        return newProfile.getUserProfileCid();
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteImageFromS3(String imagePath) {
        imageUploadService.deleteImage(imagePath);
    }

    /**
     * 기능 - 유저 닉네임 수정
     *
     * @param user
     * @param newNickname
     *
     * @return void;
     */
    @Transactional(readOnly = true)
    public void editNickname(
            User user,
            String newNickname
    ) {
        UserEntity loggedUser = userValidation.validateExistUser(user.getUsername());

        userValidation.validateDuplicateNickname(newNickname);
        loggedUser.setUserNickname(newNickname);
    }

    private String deleteOldProfile (Long userCid) {
        UserProfileEntity userProfile = userProfileRepository.findByUserProfileCid(userCid);

        userProfileRepository.delete(userProfile);
        return userProfile.getUserProfileFilePath();
    }

    /**
     * 기능 - 유저 파트 선택
     *
     * @param user
     * @param part
     *
     * return void
     */
    @Transactional
    public void selectPart(User user, String part) {
        UserEntity userEntity = userValidation.validateExistUser(user.getUsername());

        SemesterEntity semester = userValidation.validateExistSemester(userEntity.getSemester());

        if(!isPossible(semester.getStartDate())) {
            throw new CustomAccessDeniedException("변경 가능일이 아닙니다.");
        }
            userEntity.setPart(Part.valueOf(part));
            userRepository.save(userEntity);
    }

    private Boolean isPossible(Date startDate) {
        Date today = new Date();

        Long PERIOD = (today.getTime() - startDate.getTime())/(MILLISECONDS_PER_DAY);

        return PERIOD >= SELECTABLE_START_DATE && PERIOD <= SELECTABLE_END_DATE;
    }

    @Transactional
    public void deleteProfileImage(User user) {
        UserEntity userEntity = userValidation.validateExistUser(user.getUsername());

        userValidation.validateExistProfileImage(userEntity);

        String oldPath = deleteProfileImage(userEntity.getUserProfileCid());

        deleteImageFromS3(oldPath);
    }

    private String deleteProfileImage(Long profileCid) {
        UserProfileEntity userProfile = userProfileRepository.findByUserProfileCid(profileCid);

        userProfileRepository.delete(userProfile);

        return userProfile.getUserProfileFilePath();
    }
}