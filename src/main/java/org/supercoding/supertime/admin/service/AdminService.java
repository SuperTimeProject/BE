package org.supercoding.supertime.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supercoding.supertime.admin.util.AdminValidation;
import org.supercoding.supertime.golbal.web.enums.Roles;
import org.supercoding.supertime.golbal.web.enums.Verified;
import org.supercoding.supertime.inquiry.repository.InquiryRepository;
import org.supercoding.supertime.user.repository.AuthImageRepository;
import org.supercoding.supertime.user.repository.AuthStateRepository;
import org.supercoding.supertime.user.repository.UserRepository;
import org.supercoding.supertime.golbal.web.advice.CustomNoSuchElementException;
import org.supercoding.supertime.golbal.web.advice.CustomNotFoundException;
import org.supercoding.supertime.admin.web.dto.GetVerifiedUserDetailDto;
import org.supercoding.supertime.admin.web.dto.GetVerifiedUserDto;
import org.supercoding.supertime.admin.web.dto.UpdateUserInfoRequestDto;
import org.supercoding.supertime.admin.web.dto.PendingImageDto;
import org.supercoding.supertime.golbal.web.dto.CommonResponseDto;
import org.supercoding.supertime.golbal.aws.service.ImageUploadService;
import org.supercoding.supertime.inquiry.web.dto.GetUnclosedInquiryDetailDto;
import org.supercoding.supertime.inquiry.web.dto.GetUnclosedInquiryResponseDto;
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

    public CommonResponseDto varification(String userId, Verified verified) {
        log.info("[ADMIN] 사용자 인증상태 변경 요청이 들어왔습니다.");
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(()-> new CustomNotFoundException("일치하는 유저가 존재하지 않습니다."));

        AuthStateEntity authState = authStateRepository.findByUserId(user.getUserId())
                .orElseThrow(()-> new CustomNoSuchElementException("일치하는 인증내역이 존재하지 않습니다."));

        //if(user.getVerified()==Verified.COMPLETED)
        //    throw new DataIntegrityViolationException("이미 인증된 사용자 입니다.");

        user.setVerified(verified);
        authState.setVerified(verified);

        userRepository.save(user);
        authStateRepository.save(authState);

        return CommonResponseDto.successResponse("회원 인증 정보 변경에 성공했습니다.");
    }

    public GetUnclosedInquiryResponseDto getUnclosedInquiry(int page) {
        log.info("[ADMIN] 문의 조회 요청이 들어왔습니다.");

        Pageable pageable = PageRequest.of(page-1, 10);
        Page<InquiryEntity> inquiryList = inquiryRepository.findAll(pageable);

        List<GetUnclosedInquiryDetailDto> inquiryDtoList =  new ArrayList<>();

        if(inquiryList.isEmpty()){
            throw new CustomNoSuchElementException("문의내용이 없습니다.");
        }

        for(InquiryEntity inquiryEntity:inquiryList){
            GetUnclosedInquiryDetailDto dto = GetUnclosedInquiryDetailDto.builder()
                    .inquiryCid(inquiryEntity.getInquiryCid())
                    .author(inquiryEntity.getUser().getUserId())
                    .inquiryTitle(inquiryEntity.getInquiryTitle())
                    .inquiryContent(inquiryEntity.getInquiryContent())
                    .createdAt(inquiryEntity.getCreatedAt().toString())
                    .answer(inquiryEntity.getAnswer())
                    .build();

            inquiryDtoList.add(dto);
        }


        return GetUnclosedInquiryResponseDto.builder()
                .code(200)
                .success(true)
                .message("문의 기록 조회에 성공했습니다.")
                .inquiryList(inquiryDtoList)
                .build();
    }

    public CommonResponseDto answerInquiry(Long inquiryCid, String inquiryContent) {
        log.info("[ADMIN] 문의 답변 요청이 들어왔습니다.");
        InquiryEntity inquiryEntity = inquiryRepository.findById(inquiryCid)
                .orElseThrow(()-> new CustomNotFoundException("해당 문의가 존재하지 않습니다."));

        if(inquiryEntity.getAnswer()!=null)
            throw new DataIntegrityViolationException("이미 답변한 문의입니다.");

        inquiryEntity.setAnswer(inquiryContent);
        inquiryEntity.setIsClosed(InquiryClosed.CLOSED);

        inquiryRepository.save(inquiryEntity);

        return CommonResponseDto.successResponse("문의 답변에 성공했습니다.");
    }

    public CommonResponseDto deleteInquiry(Long inquiryCid){
        log.info("[ADMIN] 문의 삭제 요청이 들어왔습니다.");
        InquiryEntity inquiryEntity = inquiryRepository.findById(inquiryCid)
                .orElseThrow(()-> new CustomNotFoundException("해당 문의가 존재하지 않습니다."));

        List<InquiryImageEntity> inquiryImageEntity = inquiryEntity.getInquiryImages();

        if(!inquiryEntity.getInquiryImages().isEmpty()) {
            for(InquiryImageEntity image : inquiryImageEntity) {
                imageUploadService.deleteImage(image.getInquiryImageFilePath());
            }
        }
        inquiryRepository.delete(inquiryEntity);


        return CommonResponseDto.successResponse("문의 삭제에 성공했습니다.");
    }
}
