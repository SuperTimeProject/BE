package org.supercoding.supertime.service.user;

import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.supercoding.supertime.repository.*;
import org.supercoding.supertime.service.ImageUploadService;
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

    public GetUserPageResponseDto GetUserInfo(User user){
        UserEntity loggedInUser = userRepository.findByUserId(user.getUsername())
                .orElseThrow(()-> new NotFoundException("유저가 존재하지 않습니다."));

        List<Long> boardList = new ArrayList<>();

        for(BoardEntity board:loggedInUser.getBoardList()){
            boardList.add(board.getBoardCid());
        }

        SemesterEntity semesterEntity = semesterRepository.findById(loggedInUser.getSemester())
                .orElseThrow(()->new NotFoundException("기수가 존재하지 않습니다."));

        UserSemesterDto semester = UserSemesterDto.builder()
                .semesterCid(semesterEntity.getSemesterCid())
                .semesterDetailName(semesterEntity.getSemesterDetailName())
                .isFull(semesterEntity.getIsFull())
                .build();

        UserProfileDto userProfile = null;
        if(loggedInUser.getUserProfileCid() != null){
            UserProfileEntity userProfileEntity = userProfileRepository.findById(loggedInUser.getUserProfileCid())
                    .orElseThrow(()->new NotFoundException("찾는 프로필이 존재하지 않습니다."));

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


    public CommonResponseDto editUserInfo(
            User user,
            EditUserInfoRequestDto editUserInfoRequestDto
    ) {
        log.info("0");

        UserEntity loggedInUser = userRepository.findByUserId(user.getUsername())
                .orElseThrow(()-> new NotFoundException("로그인된 유저가 존재하지 않습니다."));

        log.info("11");


        UserProfileEntity userProfileEntity = userProfileRepository.findById(editUserInfoRequestDto.getUserProfileCid())
                .orElseThrow(()-> new NotFoundException("변경하려는 프로필 사진이 존재하지 않습니다."));

        log.info("1");

        if(editUserInfoRequestDto != null){
            loggedInUser.setUserNickname(editUserInfoRequestDto.getUserNickname());
            loggedInUser.setUserProfileCid(editUserInfoRequestDto.getUserProfileCid());
        }
        log.info("2");

        userRepository.save(loggedInUser);

        log.info("3");


        return CommonResponseDto.builder()
                .success(true)
                .code(200)
                .message("유저 정보 수정에 성공했습니다.")
                .build();
    }

    public InquiryResponseDto getInquiryHistory(User user) {
        String userId = user.getUsername();
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(()-> new NotFoundException("로그인된 유저가 존재하지 않습니다."));

        List<InquiryEntity> inquiryList = inquiryRepository.findAllByUser_UserId(userId);
        List<InquiryDetailDto> inquiryListDto = new ArrayList<>();



        if(inquiryList.isEmpty()){
            throw new NoSuchElementException("문의내용이 없습니다.");
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

            InquiryDetailDto dto = InquiryDetailDto.builder()
                    .user(userEntity)
                    .inquiryTitle(inquiry.getInquiryTitle())
                    .inquiryContent(inquiry.getInquiryContent())
                    .imageList(imageListDto)
                    .answer(inquiry.getAnswer())
                    .isClosed(inquiry.getIsClosed())
                    .build();

            inquiryListDto.add(dto);
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
                .orElseThrow(()-> new NotFoundException("탈퇴한 유저입니다."));

        InquiryEntity inquiryEntity = InquiryEntity.builder()
                .inquiryTitle(inquiryRequestDto.getInquiryTitle())
                .inquiryContent(inquiryRequestDto.getInquiryContent())
                .user(userEntity)
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
                .orElseThrow(()-> new NotFoundException("유저가 존재하지 않습니다."));

        SemesterEntity semester = semesterRepository.findById(userEntity.getSemester())
                .orElseThrow(()-> new NotFoundException("기수가 존재하지 않습니다."));

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
}