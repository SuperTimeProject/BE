package org.supercoding.supertime.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.supercoding.supertime.repository.ScheduleImageRepository;
import org.supercoding.supertime.repository.ScheduleRepository;
import org.supercoding.supertime.web.advice.CustomNotFoundException;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;
import org.supercoding.supertime.web.dto.schedule.ScheduleImageDto;
import org.supercoding.supertime.web.dto.schedule.ScheduleResponseDto;
import org.supercoding.supertime.web.entity.schedule.ScheduleEntity;
import org.supercoding.supertime.web.entity.schedule.ScheduleImageEntity;
import org.supercoding.supertime.web.entity.enums.IsFull;
import org.supercoding.supertime.web.entity.enums.Part;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ScheduleImageRepository scheduleImageRepository;
    private final ImageUploadService imageUploadService;
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
            Part part, IsFull isFull, Integer weekNumber) {
        ScheduleEntity schedule = scheduleRepository.findByPartAndIsFull(part,isFull)
                .orElseThrow(()-> new CustomNotFoundException("시간표가 존재하지 않습니다."));

        ScheduleImageEntity image = scheduleImageRepository.findByScheduleIdAndWeekNumber(schedule.getScheduleCid(),weekNumber)
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
