package org.supercoding.supertime.schedule.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.supercoding.supertime.golbal.web.dto.CommonResponseDto;
import org.supercoding.supertime.schedule.service.ScheduleService;
import org.supercoding.supertime.semester.web.dto.ScheduleResponseDto;
import org.supercoding.supertime.golbal.web.enums.IsFull;
import org.supercoding.supertime.golbal.web.enums.Part;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "시간표 관련 API")
public class ScheduleController {
    private final ScheduleService scheduleService;


    @Operation(summary = "시간표 조회", description = "오늘의 시간표를 조회하는 api입니다.")
    @GetMapping("/user/schedule")
    public ResponseEntity<ScheduleResponseDto> getSchedule(
            @AuthenticationPrincipal User user,
            @RequestParam Part part,
            @RequestParam IsFull isFull
    ){
        log.info("[SCHEDULE] 시간표 조회 요청이 들어왔습니다.");
        ScheduleResponseDto getScheduleResult = scheduleService.getSchedule(user,part,isFull);
        log.info("[SCHEDULE] 기수 조회 결과 = " + getScheduleResult);

        return ResponseEntity.status(HttpStatus.CREATED).body(getScheduleResult);
    }


    @Operation(summary = "시간표 생성", description = "시간표를 생성하는 api입니다.")
    @PostMapping(value = "/admin/timetable",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponseDto> createSchedule(
            //TODO DTO로 바꿔야 하는걸 알지만.. 프론트에서 구현 시간 모자랄까바 일단 넣기
            @RequestParam Part part,
            @RequestParam IsFull isFull,
            @RequestPart(name = "scheduleImage") List<MultipartFile> scheduleImage
    ){
        log.info("[SCHEDULE] 시간표 생성 요청이 들어왔습니다.");
        CommonResponseDto createSchedulerResult = scheduleService.createSchedule(part,isFull,scheduleImage);
        log.info("[SCHEDULE] 시간표 생성 결과 = " + createSchedulerResult);

        return ResponseEntity.status(HttpStatus.CREATED).body(createSchedulerResult);
    }

    @Operation(summary = "시간표 수정", description = "시간표를 수정하는 api입니다.")
    @PutMapping(value = "/admin/timetable", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponseDto> editSchedule(
            //TODO DTO로 바꿔야 하는걸 알지만.. 프론트에서 구현 시간 모자랄까바 일단 넣기
            @RequestParam Part part,
            @RequestParam IsFull isFull,
            @RequestPart(name = "scheduleImage") List<MultipartFile> scheduleImage
    ){
        log.info("[SCHEDULE] 시간표 수정 요청이 들어왔습니다.");
        CommonResponseDto editSchedulerResult = scheduleService.editSchedule(part,isFull,scheduleImage);
        log.info("[SCHEDULE] 시간표 수정 결과 = " + editSchedulerResult);

        return ResponseEntity.status(HttpStatus.CREATED).body(editSchedulerResult);
    }

}
