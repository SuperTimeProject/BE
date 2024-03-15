package org.supercoding.supertime.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.supercoding.supertime.inquiry.repository.InquiryRepository;
import org.supercoding.supertime.user.repository.AuthImageRepository;
import org.supercoding.supertime.user.repository.AuthStateRepository;
import org.supercoding.supertime.user.repository.UserRepository;
import org.supercoding.supertime.golbal.web.advice.CustomNoSuchElementException;
import org.supercoding.supertime.golbal.web.advice.CustomNotFoundException;
import org.supercoding.supertime.admin.web.dto.GetPendingUserDetailDto;
import org.supercoding.supertime.admin.web.dto.GetPendingUserDto;
import org.supercoding.supertime.admin.web.dto.UpdateUserInfoRequestDto;
import org.supercoding.supertime.admin.web.dto.PendingImgaeDto;
import org.supercoding.supertime.golbal.web.dto.CommonResponseDto;
import org.supercoding.supertime.golbal.aws.service.ImageUploadService;
import org.supercoding.supertime.inquiry.web.dto.GetUnclosedInquiryDetailDto;
import org.supercoding.supertime.inquiry.web.dto.GetUnclosedInquiryResponseDto;
import org.supercoding.supertime.inquiry.web.entity.InquiryEntity;
import org.supercoding.supertime.inquiry.web.entity.InquiryImageEntity;
import org.supercoding.supertime.user.entity.AuthImageEntity;
import org.supercoding.supertime.user.entity.AuthStateEntity;
import org.supercoding.supertime.golbal.web.enums.InquiryClosed;
import org.supercoding.supertime.golbal.web.enums.Valified;
import org.supercoding.supertime.user.entity.user.UserEntity;

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



    ///TODO
    public GetPendingUserDto getUserByValified(Valified valified, int page){
        log.info("[ADMIN SERVICE] 사용자 인증 대기 조회 요청이 들어왔습니다.");
        List<GetPendingUserDetailDto> userList = new ArrayList<>();

        Pageable pageable = PageRequest.of(page-1, 10);
        Page<UserEntity> userEntities = userRepository.findAllByValified(valified,pageable);

        if(userEntities.isEmpty()){
            throw new CustomNoSuchElementException("[ADMIN] 인증 대기중인 유저가 없습니다.");
        }

        for(UserEntity user : userEntities) {
            AuthStateEntity authState = authStateRepository.findByUserId(user.getUserId())
                    .orElseThrow(()-> new CustomNoSuchElementException("[GET_USER_VALIFIED]일치하는 인증요청이 존재하지 않습니다."));

            // 이미지 없는 경우
            AuthImageEntity authImageEntity = authImageRepository.findById(authState.getAuthCid()).orElse(null);
            PendingImgaeDto image = null;

            if(authImageEntity!=null){
                image = PendingImgaeDto.builder()
                        .authImageCid(authImageEntity.getAuthImageCid())
                        .authImageFileName(authImageEntity.getAuthImageFileName())
                        .authImageFilePath(authImageEntity.getAuthImageFilePath())
                        .build();
            }

            GetPendingUserDetailDto dto = GetPendingUserDetailDto.builder()
                    .userId(user.getUserId())
                    .userNickname(user.getUserNickname())
                    .semester(user.getSemester())
                    .userName(user.getUserName())
                    .image(image)
                    .valified(valified)
                    .build();


            userList.add(dto);
        }

        return GetPendingUserDto.builder()
                .code(200)
                .success(true)
                .message("인증 대기 조회 요청에 성공했습니다.")
                .userList(userList)
                .build();
    }

        public GetPendingUserDetailDto getValifiedDetail(String userId){
            log.info("[ADMIN SERVICE] 사용자 인증대기 상세 조회 요청이 들어왔습니다.");
            UserEntity user = userRepository.findByUserId(userId)
                    .orElseThrow(()-> new CustomNoSuchElementException("인증요청의 유저값이 존재하지 않습니다."));

            AuthStateEntity authState = authStateRepository.findByUserId(user.getUserId())
                    .orElseThrow(()-> new CustomNoSuchElementException("일치하는 인증요청이 존재하지 않습니다."));


            PendingImgaeDto image = null;

            if(authState.getAuthImageId()!=null){
                AuthImageEntity authImageEntity = authImageRepository.findById(authState.getAuthImageId())
                        .orElseThrow(()-> new CustomNoSuchElementException("인증요청의 이미지 값이 존재하지 않습니다."));

                image = PendingImgaeDto.builder()
                        .authImageCid(authImageEntity.getAuthImageCid())
                        .authImageFileName(authImageEntity.getAuthImageFileName())
                        .authImageFilePath(authImageEntity.getAuthImageFilePath())
                        .build();
            }

            return GetPendingUserDetailDto.builder()
                    .userCid(user.getUserCid())
                    .userId(user.getUserId())
                    .userName(user.getUserName())
                    .userNickname(user.getUserNickname())
                    .image(image)
                    .semester(user.getSemester())
                    .valified(user.getValified())
                    .build();
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
        if(updateUserInfoRequestDto.getValified()!=null){
            userEntity.setValified(updateUserInfoRequestDto.getValified());
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
    /* 아니면 가독성과 디비 왕창조회를 챙길것인가..? 어차피 한번에 많이 수정되는건 아닌데..
    public void updateUserSemester(UserEntity user,UpdateUserInfoRequestDto updateUserInfoRequestDto){
        if(updateUserInfoRequestDto.getUserNickname() != null){
            user.setUserNickname(updateUserInfoRequestDto.getUserNickname());
        }
        userRepository.save(user);
    }
    public void updateUserPassword(UserEntity user,UpdateUserInfoRequestDto updateUserInfoRequestDto){
        if(updateUserInfoRequestDto.getUserPassword() != null){
            user.setUserPassword(updateUserInfoRequestDto.getUserPassword());
        }
        userRepository.save(user);
    }
    public void updateUserName(UserEntity user,UpdateUserInfoRequestDto updateUserInfoRequestDto){
        if(updateUserInfoRequestDto.getUserName() != null){
            user.setUserName(updateUserInfoRequestDto.getUserName());
        }
        userRepository.save(user);
    }
    public void updateUserNickname(UserEntity user,UpdateUserInfoRequestDto updateUserInfoRequestDto){
        if(updateUserInfoRequestDto.getUserNickname() != null){
            user.setUserNickname(updateUserInfoRequestDto.getUserNickname());
        }
        userRepository.save(user);
    }
    public void updateValified(UserEntity user,UpdateUserInfoRequestDto updateUserInfoRequestDto){
        if(updateUserInfoRequestDto.getValified() != null){
            user.setValified(updateUserInfoRequestDto.getValified());
        }
        userRepository.save(user);
    }
    public void updatePart(UserEntity user,UpdateUserInfoRequestDto updateUserInfoRequestDto){
        if(updateUserInfoRequestDto.getPart() != null){
            user.setPart(updateUserInfoRequestDto.getPart());
        }
        userRepository.save(user);
    }
    public void updateRoles(UserEntity user,UpdateUserInfoRequestDto updateUserInfoRequestDto){
        if(updateUserInfoRequestDto.getRoles() != null){
            user.setRoles(updateUserInfoRequestDto.getRoles());
        }
        userRepository.save(user);
    }
    public void updateIsDeleted(UserEntity user,UpdateUserInfoRequestDto updateUserInfoRequestDto){
        if(updateUserInfoRequestDto.getIsDeleted() != null){
            user.setIsDeleted(updateUserInfoRequestDto.getIsDeleted());
        }
        userRepository.save(user);
    }
*/

    public CommonResponseDto varification(String userId, Valified valified) {
        log.info("[ADMIN] 사용자 인증상태 변경 요청이 들어왔습니다.");
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(()-> new CustomNotFoundException("일치하는 유저가 존재하지 않습니다."));

        AuthStateEntity authState = authStateRepository.findByUserId(user.getUserId())
                .orElseThrow(()-> new CustomNoSuchElementException("일치하는 인증내역이 존재하지 않습니다."));

        //if(user.getValified()==Valified.COMPLETED)
        //    throw new DataIntegrityViolationException("이미 인증된 사용자 입니다.");

        user.setValified(valified);
        authState.setValified(valified);

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
