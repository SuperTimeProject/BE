package org.supercoding.supertime.schedule.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.supercoding.supertime.schedule.repository.ScheduleImageRepository;
import org.supercoding.supertime.schedule.repository.ScheduleRepository;
import org.supercoding.supertime.semester.repository.SemesterRepository;
import org.supercoding.supertime.user.repository.UserRepository;
import org.supercoding.supertime.golbal.web.advice.CustomNotFoundException;
import org.supercoding.supertime.golbal.web.dto.CommonResponseDto;
import org.supercoding.supertime.golbal.aws.service.ImageUploadService;
import org.supercoding.supertime.semester.dto.ScheduleImageDto;
import org.supercoding.supertime.semester.dto.ScheduleResponseDto;
import org.supercoding.supertime.semester.entity.SemesterEntity;
import org.supercoding.supertime.schedule.entity.ScheduleEntity;
import org.supercoding.supertime.schedule.entity.ScheduleImageEntity;
import org.supercoding.supertime.golbal.web.enums.IsFull;
import org.supercoding.supertime.golbal.web.enums.Part;
import org.supercoding.supertime.user.entity.user.UserEntity;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ScheduleImageRepository scheduleImageRepository;
    private final ImageUploadService imageUploadService;
    private final UserRepository userRepository;
    private final SemesterRepository semesterRepository;

    @Transactional
    public CommonResponseDto createSchedule(Part part, IsFull isFull, List<MultipartFile> images) {
        if(!scheduleRepository.findByPartAndIsFull(part,isFull).isEmpty())
            throw new DataIntegrityViolationException("이미 생성된 시간표가 존재합니다.");

        ScheduleEntity schedule = ScheduleEntity.builder()
                .part(part)
                .isFull(isFull)
                .build();


        if(images != null){
            List<ScheduleImageEntity> uploadImages = imageUploadService.uploadScheduleImages(images, "inquiry");
            schedule.setImageList(uploadImages);
            log.info("[CREATE_SCHEDULE] 시간표에 이미지가 추가되었습니다.");
        }

        scheduleRepository.save(schedule);

        for(ScheduleImageEntity image:schedule.getImageList())
        {
            image.setScheduleId(schedule.getScheduleCid());
            scheduleImageRepository.save(image);
        }


        return CommonResponseDto.successResponse("시간표 생성에 성공했습니다.");
    }

    @Transactional
    public CommonResponseDto editSchedule(Part part, IsFull isFull, List<MultipartFile> images) {
        ScheduleEntity schedule = scheduleRepository.findByPartAndIsFull(part,isFull)
                .orElseThrow(()-> new CustomNotFoundException("시간표가 존재하지 않습니다."));

        List<ScheduleImageEntity> imageList = schedule.getImageList();

        for(ScheduleImageEntity image : imageList){
            //s3에서 사진 삭제
            imageUploadService.deleteImage(image.getScheduleImageFilePath());
            scheduleImageRepository.delete(image);
            schedule.setImageList(null);
        }

        scheduleRepository.save(schedule);

        if(images != null){
            List<ScheduleImageEntity> uploadImages = imageUploadService.uploadScheduleImages(images, "inquiry");
            schedule.setImageList(uploadImages);
            log.info("[CREATE_SCHEDULE] 시간표에 이미지가 추가되었습니다.");
        }

        for(ScheduleImageEntity image:schedule.getImageList())
        {
            image.setScheduleId(schedule.getScheduleCid());
            scheduleImageRepository.save(image);
        }


        return CommonResponseDto.successResponse("시간표 수정에 성공했습니다.");
    }

    @Transactional
    public ScheduleResponseDto getSchedule(
            User user, Part part, IsFull isFull) {
        ScheduleEntity schedule = scheduleRepository.findByPartAndIsFull(part,isFull)
                .orElseThrow(()-> new CustomNotFoundException("시간표가 존재하지 않습니다."));

        UserEntity userEntity = userRepository.findByUserId(user.getUsername())
                .orElseThrow(()-> new CustomNotFoundException("유저가 존재하지 않습니다."));

        SemesterEntity semester = semesterRepository.findById(userEntity.getSemester())
                .orElseThrow(()-> new CustomNotFoundException("기수가 존재하지 않습니다."));

        int startDay = (int)(semester.getStartDate().getTime() / (1000 * 60 * 60 * 24));
        int nowDay = (int)(new Date().getTime() / (1000 * 60 * 60 * 24));

        int week = (int)((nowDay - startDay) / 7) + 1;


        ScheduleImageEntity image = scheduleImageRepository.findByScheduleIdAndWeekNumber(schedule.getScheduleCid(),week)
                .orElseThrow(()-> new CustomNotFoundException("시간표 이미지가 존재하지 않습니다."));

        ScheduleImageDto dto = ScheduleImageDto.builder()
                .scheduleImageCid(image.getScheduleId())
                .scheduleImageFileName(image.getScheduleImageFileName())
                .scheduleImageFilePath(image.getScheduleImageFilePath())
                .build();

        return ScheduleResponseDto.builder()
                .success(true)
                .code(201)
                .message("시간표 이미지를 조회했습니다.")
                .image(dto)
                .build();
    }


}
