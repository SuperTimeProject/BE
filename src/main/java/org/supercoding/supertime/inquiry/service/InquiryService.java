package org.supercoding.supertime.inquiry.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.supercoding.supertime.golbal.aws.service.ImageUploadService;
import org.supercoding.supertime.inquiry.repository.InquiryRepository;
import org.supercoding.supertime.inquiry.web.dto.InquiryDetailDto;
import org.supercoding.supertime.inquiry.web.dto.InquiryImageDto;
import org.supercoding.supertime.inquiry.web.dto.InquiryRequestDto;
import org.supercoding.supertime.inquiry.web.entity.InquiryEntity;
import org.supercoding.supertime.inquiry.web.entity.InquiryImageEntity;
import org.supercoding.supertime.user.util.UserValidation;
import org.supercoding.supertime.user.web.entity.user.UserEntity;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {
    private final UserValidation userValidation;
    private final InquiryRepository inquiryRepository;

    private final ImageUploadService imageUploadService;

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
