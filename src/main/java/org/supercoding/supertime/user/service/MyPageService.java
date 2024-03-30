package org.supercoding.supertime.user.service;

import com.nimbusds.jose.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.supercoding.supertime.golbal.aws.service.ImageUploadService;
import org.supercoding.supertime.user.repository.UserProfileRepository;
import org.supercoding.supertime.user.repository.UserRepository;
import org.supercoding.supertime.user.util.UserValidation;
import org.supercoding.supertime.user.web.entity.user.UserEntity;
import org.supercoding.supertime.user.web.entity.user.UserProfileEntity;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyPageService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final ImageUploadService imageUploadService;

    private final UserValidation userValidation;

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

}
