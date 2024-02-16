package org.supercoding.supertime.service.user;

import com.amazonaws.services.kms.model.NotFoundException;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.supercoding.supertime.repository.*;
import org.supercoding.supertime.service.ImageUploadService;
import org.supercoding.supertime.web.advice.CustomNotFoundException;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;
import org.supercoding.supertime.web.dto.inquiry.InquiryDetailDto;
import org.supercoding.supertime.web.dto.inquiry.InquiryImageDto;
import org.supercoding.supertime.web.dto.inquiry.InquiryRequestDto;
import org.supercoding.supertime.web.dto.inquiry.InquiryResponseDto;
import org.supercoding.supertime.web.dto.user.EditUserInfoRequestDto;
import org.supercoding.supertime.web.dto.user.getUserDto.GetUserPageResponseDto;
import org.supercoding.supertime.web.dto.user.getUserDto.UserProfileDto;
import org.supercoding.supertime.web.dto.user.getUserDto.UserSemesterDto;
import org.supercoding.supertime.web.entity.Inquiry.InquiryEntity;
import org.supercoding.supertime.web.entity.Inquiry.InquiryImageEntity;
import org.supercoding.supertime.web.entity.SemesterEntity;
import org.supercoding.supertime.web.entity.board.BoardEntity;
import org.supercoding.supertime.web.entity.board.PostEntity;
import org.supercoding.supertime.web.entity.enums.InquiryClosed;
import org.supercoding.supertime.web.entity.enums.Part;
import org.supercoding.supertime.web.entity.user.UserEntity;
import org.supercoding.supertime.web.entity.user.UserProfileEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final SemesterRepository semesterRepository;
    private final UserProfileRepository userProfileRepository;
    private final PostRepository postRepository;
    private final InquiryRepository inquiryRepository;
    private final InquiryImageRepository inquiryImageRepository;
    private final ImageUploadService imageUploadService;

    private static final long SELECT_START_DAY = 2 * 14;
    private static final long PERIOD = 3;

    public CommonResponseDto editUserInfo(
            User user,
            String nickName,
            MultipartFile profileImg
    ) {
        log.info("[EDIT_USER_INFO] 프로필 수정 요청이 들어왔습니다.");
        UserEntity loggedInUser = userRepository.findByUserId(user.getUsername())
                .orElseThrow(()-> new CustomNotFoundException("로그인된 유저가 존재하지 않습니다."));

        if(nickName!=null)
           loggedInUser.setUserNickname(nickName);

        if(profileImg != null){
            UserProfileEntity userProfileEntity = userProfileRepository.findByUserProfileCid(loggedInUser.getUserProfileCid());

            if(userProfileEntity!=null){
                //기존 이미지 삭제
                imageUploadService.deleteImage(userProfileEntity.getUserProfileFilePath());
                userProfileRepository.delete(userProfileEntity);
            }

            //이미지 업로드
            userProfileEntity = imageUploadService.uploadUserProfileImages(profileImg,"profile");
            userProfileRepository.save(userProfileEntity);
            loggedInUser.setUserProfileCid(userProfileEntity.getUserProfileCid());

            log.info("[EDIT_USER_INFO] 프로필 이미지가 추가되었습니다.");
        }

        userRepository.save(loggedInUser);

        return CommonResponseDto.builder()
                .success(true)
                .code(201)
                .message("유저 정보 수정에 성공했습니다.")
                .build();
    }

    public CommonResponseDto deleteProfileImage(User user){
        log.info("[EDIT_USER_INFO] 프로필 수정 요청이 들어왔습니다.");
        UserEntity loggedInUser = userRepository.findByUserId(user.getUsername())
                .orElseThrow(()-> new CustomNotFoundException("로그인된 유저가 존재하지 않습니다."));

        if (loggedInUser.getUserProfileCid()==null){
            throw new CustomNotFoundException("프로필 이미지가 존재하지 않습니다.");
        }

        UserProfileEntity userProfileEntity = userProfileRepository.findByUserProfileCid(loggedInUser.getUserProfileCid());

        imageUploadService.deleteImage(userProfileEntity.getUserProfileFilePath());
        userProfileRepository.delete(null);

        return CommonResponseDto.successResponse("프로필 사진 삭제에 성공했습니다");
    }


    public InquiryResponseDto getInquiryHistory(User user,String inquiryClosedStr,int page) {
        log.info("[GET_INQUIRY] 문의내역 조회 요청이 들어왔습니다.");
        UserEntity userEntity = userRepository.findByUserId(user.getUsername())
                .orElseThrow(()-> new CustomNotFoundException("로그인된 유저가 존재하지 않습니다."));

        InquiryClosed inquiryClosed = InquiryClosed.valueOf(inquiryClosedStr);

        Pageable pageable = PageRequest.of(page-1, 10);
        Page<InquiryEntity> inquiryList = null;

        if(inquiryClosed == InquiryClosed.OPEN){
            log.info("[USER] 미답변 문의 기록 조회");
            inquiryList = inquiryRepository.findAllByIsClosed(InquiryClosed.OPEN,pageable);

        } else{
            log.info("[USER] 답변완료 문의 기록 조회");
            inquiryList = inquiryRepository.findAllByIsClosed(InquiryClosed.CLOSED,pageable);
        }

        log.info("[GET_INQUIRY] entity를 조회 했습니다.");



        List<InquiryDetailDto> inquiryListDto = new ArrayList<>();

        if(inquiryList.isEmpty()){
            throw new CustomNotFoundException("문의내용이 없습니다.");
        }

        for(InquiryEntity inquiry: inquiryList){
            List<InquiryImageDto> imageListDto = new ArrayList<>();

            for(InquiryImageEntity img:inquiry.getInquiryImages()){
                InquiryImageDto dto = InquiryImageDto.builder()
                        .postImageFileName(img.getInquiryImageFileName())
                        .postImageFilePath(img.getInquiryImageFilePath())
                        .build();

                imageListDto.add(dto);
            }

            InquiryDetailDto inquiryDto = InquiryDetailDto.builder()
                    .userId(userEntity.getUserId())
                    .inquiryTitle(inquiry.getInquiryTitle())
                    .inquiryContent(inquiry.getInquiryContent())
                    .imageList(imageListDto)
                    .answer(inquiry.getAnswer())
                    .isClosed(inquiry.getIsClosed())
                    .build();

            inquiryListDto.add(inquiryDto);
        }

        return InquiryResponseDto.builder()
                .success(true)
                .code(200)
                .message("문의하기 기록 조회에 성공했습니다.")
                .inquiryList(inquiryListDto)
                .build();
    }

    public CommonResponseDto createInquiry(User user, InquiryRequestDto inquiryRequestDto, List<MultipartFile> images) {
        UserEntity userEntity = userRepository.findByUserId(user.getUsername())
                .orElseThrow(()-> new CustomNotFoundException("탈퇴한 유저입니다."));

        InquiryEntity inquiryEntity = InquiryEntity.builder()
                .inquiryTitle(inquiryRequestDto.getInquiryTitle())
                .inquiryContent(inquiryRequestDto.getInquiryContent())
                .user(userEntity)
                .isClosed(InquiryClosed.OPEN)
                .build();

        if(images != null){
            List<InquiryImageEntity> uploadImages = imageUploadService.uploadInquiryImages(images, "inquiry");
            inquiryEntity.setInquiryImages(uploadImages);
            log.info("[CREATE_INQUIRY] 문의에 이미지가 추가되었습니다.");
        }

        inquiryRepository.save(inquiryEntity);

        return CommonResponseDto.builder()
                .success(true)
                .code(200)
                .message("문의하기에 성공했습니다.")
                .build();
    }

    public CommonResponseDto selectPart(User user,String part){

        UserEntity userEntity = userRepository.findByUserId(user.getUsername())
                .orElseThrow(()-> new CustomNotFoundException("유저가 존재하지 않습니다."));

        SemesterEntity semester = semesterRepository.findById(userEntity.getSemester())
                .orElseThrow(()-> new CustomNotFoundException("기수가 존재하지 않습니다."));

        Date now = new Date();

        Long Period = (now.getTime() - semester.getStartDate().getTime())/(1000 * 60 * 60 * 24);

        //TODO 1차배포용으로 열어놓음
        boolean isSelectPeriod =true;
        //boolean isSelectPeriod = Period >= SELECT_START_DAY && Period <= SELECT_START_DAY + PERIOD;

        if(isSelectPeriod) {
            userEntity.setPart(Part.valueOf(part));

            userRepository.save(userEntity);

            return CommonResponseDto.builder()
                    .success(true)
                    .code(200)
                    .message("파트 선택에 성공했습니다.")
                    .build();
        }

        return CommonResponseDto.builder()
                .success(false)
                .code(400)
                .message("파트 선택에 실패했습니다.")
                .build();
    }


    /* 유저정보 조회 사용안할듯
    public GetUserPageResponseDto GetUserInfo(User user){
        UserEntity loggedInUser = userRepository.findByUserId(user.getUsername())
                .orElseThrow(()-> new CustomNotFoundException("유저가 존재하지 않습니다."));

        List<Long> boardList = new ArrayList<>();

        for(BoardEntity board:loggedInUser.getBoardList()){
            boardList.add(board.getBoardCid());
        }

        SemesterEntity semesterEntity = semesterRepository.findById(loggedInUser.getSemester())
                .orElseThrow(()->new CustomNotFoundException("기수가 존재하지 않습니다."));

        UserSemesterDto semester = UserSemesterDto.builder()
                .semesterCid(semesterEntity.getSemesterCid())
                .semesterDetailName(semesterEntity.getSemesterDetailName())
                .isFull(semesterEntity.getIsFull())
                .build();

        UserProfileDto userProfile = null;
        if(loggedInUser.getUserProfileCid() != null){
            UserProfileEntity userProfileEntity = userProfileRepository.findById(loggedInUser.getUserProfileCid())
                    .orElseThrow(()->new CustomNotFoundException("찾는 프로필이 존재하지 않습니다."));

            userProfile = UserProfileDto.builder()
                    .userProfileCid(userProfileEntity.getUserProfileCid())
                    .userProfileFileName(userProfileEntity.getUserProfileFileName())
                    .userProfileFilePath(userProfileEntity.getUserProfileFilePath())
                    .build();
        }

        List<PostEntity> posts = postRepository.findAllByUserEntity_UserCid(loggedInUser.getUserCid());

        GetUserPageResponseDto responseDto
                = GetUserPageResponseDto.builder()
                .userCid(loggedInUser.getUserCid())
                .userId(loggedInUser.getUserId())
                .part(loggedInUser.getPart())
                .userName(loggedInUser.getUserName())
                .userNickname(loggedInUser.getUserNickname())
                .part(loggedInUser.getPart())
                .posts(posts)
                .userProfile(userProfile)
                .build();

        log.debug("GetUserPageResponseDto 성공" +responseDto);
        System.out.println("PART = "+loggedInUser.getPart() );

        return responseDto;
    }
*/

}
