package org.supercoding.supertime.user.service;

import com.nimbusds.jose.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.supercoding.supertime.golbal.aws.service.ImageUploadService;
import org.supercoding.supertime.inquiry.repository.InquiryRepository;
import org.supercoding.supertime.inquiry.web.dto.InquiryDetailDto;
import org.supercoding.supertime.inquiry.web.dto.InquiryImageDto;
import org.supercoding.supertime.inquiry.web.dto.InquiryRequestDto;
import org.supercoding.supertime.inquiry.web.entity.InquiryEntity;
import org.supercoding.supertime.inquiry.web.entity.InquiryImageEntity;
import org.supercoding.supertime.user.repository.UserProfileRepository;
import org.supercoding.supertime.user.repository.UserRepository;
import org.supercoding.supertime.user.util.UserValidation;
import org.supercoding.supertime.user.web.entity.user.UserEntity;
import org.supercoding.supertime.user.web.entity.user.UserProfileEntity;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyPageService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final ImageUploadService imageUploadService;
    private final InquiryRepository inquiryRepository;

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
     * 기능 - 유저 문의 조회
     *
     * @param user
     * @param page
     *
     * @return List<InquiryDetailDto>
     */
    @Transactional(readOnly = true)
    public List<InquiryDetailDto> getUserInquiry(User user, int page) {
        UserEntity userEntity = userValidation.validateExistUser(user.getUsername());

        Page<InquiryEntity> inquiryList = userValidation.validateExistUserInquiry(page, userEntity);

        return inquiryToDto(inquiryList, userEntity);
    }

    private List<InquiryDetailDto> inquiryToDto(Page<InquiryEntity> inquiryList, UserEntity user) {
        List<InquiryDetailDto> inquiryDetailList = new ArrayList<>();
        for(InquiryEntity inquiry : inquiryList) {
            List<InquiryImageDto> imageList = inquiryImageToDto(inquiry);
            inquiryDetailList.add(InquiryDetailDto.from(inquiry, user, imageList));
        }

        return inquiryDetailList;
    }

    private List<InquiryImageDto> inquiryImageToDto(InquiryEntity inquiry) {
        List<InquiryImageDto> imageList = new ArrayList<>();
        for(InquiryImageEntity image : inquiry.getInquiryImages()) {
            imageList.add(InquiryImageDto.from(image));
        }

        return imageList;
    }

    /**
     * 기능 - 유저 문의 내용 상세 조회
     *
     * @param user
     * @param inquiryCid
     *
     * @return InquiryDetailDto
     */
    public InquiryDetailDto getInquiryDetail(User user, Long inquiryCid) {
        UserEntity userEntity = userValidation.validateExistUser(user.getUsername());

        InquiryEntity inquiry = userValidation.validateExistInquiry(inquiryCid);

        List<InquiryImageDto> imageList = inquiryImageToDto(inquiry);

        return InquiryDetailDto.from(inquiry, userEntity, imageList);
    }

    /**
     * 기능 - 문의 작성
     *
     * @param user
     * @param inquiryRequestDto
     * @param inquiryImage
     *
     * @return void
     */
    @Transactional
    public void createInquiry(User user, InquiryRequestDto inquiryRequestDto, List<MultipartFile> inquiryImage) {
        UserEntity userEntity = userValidation.validateExistUser(user.getUsername());

        InquiryEntity inquiry = InquiryEntity.from(inquiryRequestDto, userEntity);

        addInquiryImage(inquiry, inquiryImage);

        inquiryRepository.save(inquiry);
    }

    private void addInquiryImage(InquiryEntity inquiry, List<MultipartFile> imageList) {
        if(imageList != null) {
            List<InquiryImageEntity> uploadImages = imageUploadService.uploadInquiryImages(imageList, "inquiry");
            inquiry.setInquiryImages(uploadImages);
        }
    }

}
