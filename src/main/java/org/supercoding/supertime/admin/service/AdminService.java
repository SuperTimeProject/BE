package org.supercoding.supertime.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.supercoding.supertime.admin.util.AdminValidation;
import org.supercoding.supertime.admin.web.dto.*;
import org.supercoding.supertime.admin.web.dto.inquiry.GetInquiryDetail;
import org.supercoding.supertime.admin.web.dto.verified.GetVerifiedUserDetailDto;
import org.supercoding.supertime.admin.web.dto.verified.PendingImageDto;
import org.supercoding.supertime.golbal.web.enums.Roles;
import org.supercoding.supertime.golbal.web.enums.Verified;
import org.supercoding.supertime.inquiry.repository.InquiryImageRepository;
import org.supercoding.supertime.inquiry.repository.InquiryRepository;
import org.supercoding.supertime.user.repository.AuthImageRepository;
import org.supercoding.supertime.user.repository.AuthStateRepository;
import org.supercoding.supertime.user.repository.UserRepository;
import org.supercoding.supertime.golbal.web.advice.CustomNotFoundException;
import org.supercoding.supertime.golbal.web.dto.CommonResponseDto;
import org.supercoding.supertime.golbal.aws.service.ImageUploadService;
import org.supercoding.supertime.inquiry.web.entity.InquiryEntity;
import org.supercoding.supertime.inquiry.web.entity.InquiryImageEntity;
import org.supercoding.supertime.user.web.entity.AuthImageEntity;
import org.supercoding.supertime.user.web.entity.AuthStateEntity;
import org.supercoding.supertime.golbal.web.enums.InquiryClosed;
import org.supercoding.supertime.user.web.entity.user.UserEntity;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    private final UserRepository userRepository;
    private final InquiryRepository inquiryRepository;
    private final InquiryImageRepository inquiryImageRepository;
    private final ImageUploadService imageUploadService;
    private final AuthImageRepository authImageRepository;
    private final AuthStateRepository authStateRepository;

    private final AdminValidation adminValidation;

    /**
     * 기능 - 유저의 권한을 수정합니다
     * @param userCid
     * @param role
     */
    @Transactional
    public void changeRole(Long userCid, Roles role) {
        UserEntity user = adminValidation.findUserEntityByUserCid(userCid);

        setRole(user, role);
    }

    private void setRole(UserEntity user, Roles role) {
        user.setRoles(role);
        userRepository.save(user);
    }

    /**
     * 기능 - 인증 상태별 유저 조회
     * @param verified
     * @param page
     * @return List<GetVerifiedUserDetailDto>
     */
    @Transactional(readOnly = true)
    public List<GetVerifiedUserDetailDto> findUserByVerified(Verified verified, int page) {
        Page<UserEntity> verifiedUserList = getUserList(verified, page);

        return toVerifiedUserDto(verifiedUserList, verified);
    }

    private Page<UserEntity> getUserList(Verified verified, int page) {
        Pageable pageable = PageRequest.of(page - 1, 10);
        return adminValidation.validateVerifiedUser(verified, pageable);
    }

    private List<GetVerifiedUserDetailDto> toVerifiedUserDto(Page<UserEntity> verifiedUserList, Verified verified) {
        List<GetVerifiedUserDetailDto> userList = new ArrayList<>();

        for(UserEntity user : verifiedUserList) {
            AuthStateEntity authState = adminValidation.validateGetAuthState(user.getUserId());

            AuthImageEntity authImageEntity = authImageRepository.findById(authState.getAuthCid()).orElse(null);

            if(authImageEntity == null) {
                userList.add(GetVerifiedUserDetailDto.from(user, null, verified));
                continue;
            }

            PendingImageDto image = PendingImageDto.from(authImageEntity);
            userList.add(GetVerifiedUserDetailDto.from(user, image, verified));
        }

        return userList;
    }

    /**
     * 기능 - 유저의 인증상태관리에 대한 조회
     * @param userId
     * @return GetVerifiedUserDetailDto
     */
    public GetVerifiedUserDetailDto getVerifiedUserDetail(String userId) {
        UserEntity user = adminValidation.findUserEntityByUserId(userId);
        AuthStateEntity authState = adminValidation.validateGetAuthState(user.getUserId());
        PendingImageDto pendingImage = getPendingImage(authState);

        return GetVerifiedUserDetailDto.getVerifiedDetail(user, pendingImage);
    }

    private PendingImageDto getPendingImage(AuthStateEntity authState) {
        if(authState.getAuthImageId() == null) {
            return null;
        }

        AuthImageEntity authImageEntity = adminValidation.validateExistAuthImage(authState.getAuthImageId());

        return PendingImageDto.from(authImageEntity);
    }

    /**
     * 기능 - 유저의 인증상태 수정
     * @param userId
     * @param verified
     */
    @Transactional
    public void changeVerification(String userId, Verified verified) {
        UserEntity user = adminValidation.findUserEntityByUserId(userId);
        AuthStateEntity authState = adminValidation.validateGetAuthState(userId);

        setVerifiedState(user, authState, verified);
    }

    private void setVerifiedState(UserEntity user, AuthStateEntity authState, Verified verified) {
        user.setVerified(verified);
        authState.setVerified(verified);

        userRepository.save(user);
        authStateRepository.save(authState);
    }

    /**
     * 기능 - 문의하기 불러오기
     * @param page
     * @return List<GetInquiryDetail>
     */
    @Transactional(readOnly = true)
    public List<GetInquiryDetail> getInquiryByIsClosed(int page, InquiryClosed isClosed) {
        Page<InquiryEntity> inquiryList = getPageableInquiry(page, isClosed);

        return inquiryToDto(inquiryList);
    }

    private Page<InquiryEntity> getPageableInquiry(int page, InquiryClosed isClosed) {
        Pageable pageable = PageRequest.of(page - 1, 10);
        return adminValidation.validateGetInquiry(pageable, isClosed);
    }

    private List<GetInquiryDetail> inquiryToDto(Page<InquiryEntity> inquiryList) {
        List<GetInquiryDetail> inquiryDtoList =  new ArrayList<>();

        for(InquiryEntity inquiry : inquiryList) {
            inquiryDtoList.add(GetInquiryDetail.from(inquiry));
        }

        return inquiryDtoList;
    }

    /**
     * 기능 - 문의 답변
     * @param inquiryCid
     * @param answer
     */
    @Transactional
    public void answerInquiry(Long inquiryCid, String answer) {
        InquiryEntity inquiry = adminValidation.validateAnsweredInquiry(inquiryCid);

        inquiry.setAnswer(answer);
        inquiry.setIsClosed(InquiryClosed.CLOSED);
        inquiryRepository.save(inquiry);
    }

    public void deleteInquiry(Long inquiryCid) {
        InquiryEntity inquiry = adminValidation.validateExistInquiry(inquiryCid);

        List<String> oldPathList = deleteInquiryImage(inquiry.getInquiryImages());
        inquiryRepository.delete(inquiry);

        deleteImagesFromS3(oldPathList);
    }

    private List<String> deleteInquiryImage(List<InquiryImageEntity> inquiryImages) {
        List<String> oldPathList = new ArrayList<>();

        for(InquiryImageEntity image : inquiryImages) {
            inquiryImageRepository.delete(image);
            oldPathList.add(image.getInquiryImageFilePath());
        }

        return oldPathList;
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteImagesFromS3(List<String> oldPathList) {
        for (String path : oldPathList) {
            imageUploadService.deleteImage(path);
        }
    }


    public CommonResponseDto updateUserInfo(UpdateUserInfoRequestDto updateUserInfoRequestDto){

        UserEntity userEntity = userRepository.findByUserId(updateUserInfoRequestDto.getUserName())
                .orElseThrow(()-> new CustomNotFoundException("일치하는 유저가 존재하지 않습니다."));

        //DB조회하는것보다는 효율적이지 않을까?
        if(updateUserInfoRequestDto.getSemester()!=null){
            userEntity.setSemester(updateUserInfoRequestDto.getSemester());
        }
        if(updateUserInfoRequestDto.getUserPassword()!=null){
            userEntity.setUserPassword(updateUserInfoRequestDto.getUserPassword());
        }
        if(updateUserInfoRequestDto.getUserName()!=null){
            userEntity.setUserName(updateUserInfoRequestDto.getUserName());
        }
        if(updateUserInfoRequestDto.getUserNickname()!=null){
            userEntity.setUserNickname(updateUserInfoRequestDto.getUserNickname());
        }
        if(updateUserInfoRequestDto.getVerified()!=null){
            userEntity.setVerified(updateUserInfoRequestDto.getVerified());
        }
        if(updateUserInfoRequestDto.getPart()!=null){
            userEntity.setPart(updateUserInfoRequestDto.getPart());
        }
        if(updateUserInfoRequestDto.getRoles()!=null){
            userEntity.setRoles(updateUserInfoRequestDto.getRoles());
        }
        if(updateUserInfoRequestDto.getIsDeleted()!=null){
            userEntity.setIsDeleted(updateUserInfoRequestDto.getIsDeleted());
        }

        userRepository.save(userEntity);

        return CommonResponseDto.successResponse("회원 정보 변경에 성공했습니다.");
    }

}
