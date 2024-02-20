package org.supercoding.supertime.web.controller;

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
import org.supercoding.supertime.service.ScheduleService;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;
import org.supercoding.supertime.web.dto.schedule.ScheduleResponseDto;
import org.supercoding.supertime.web.dto.semester.CreateSemesterRequestDto;
import org.supercoding.supertime.web.entity.enums.IsFull;
import org.supercoding.supertime.web.entity.enums.Part;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/schedule")
@RequiredArgsConstructor
@Tag(name = "시간표 관련 API")
public class ScheduleController {
    private final ScheduleService scheduleService;


    @Operation(summary = "시간표 조회", description = "오늘의 시간표를 조회하는 api입니다.")
    @GetMapping("/view")
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

}
