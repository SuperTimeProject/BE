package org.supercoding.supertime.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.supercoding.supertime.repository.ScheduleImageRepository;
import org.supercoding.supertime.repository.ScheduleRepository;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;
import org.supercoding.supertime.web.dto.schedule.ScheduleResponseDto;
import org.supercoding.supertime.web.dto.semester.CreateSemesterRequestDto;
import org.supercoding.supertime.web.entity.enums.IsFull;
import org.supercoding.supertime.web.entity.enums.Part;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ScheduleImageRepository scheduleImageRepository;

    @Transactional
    public CommonResponseDto createSchedule(Part part, IsFull isFull, List<MultipartFile> images) {



        return CommonResponseDto.successResponse("시간표 생성에 성공했습니다.");
    }

    @Transactional
    public CommonResponseDto editSchedule(Part part, IsFull isFull, List<MultipartFile> images) {



        return CommonResponseDto.successResponse("시간표 수정에 성공했습니다.");
    }

    @Transactional
    public ScheduleResponseDto getSchedule(Part part, IsFull isFull, List<MultipartFile> images) {

    }


}
