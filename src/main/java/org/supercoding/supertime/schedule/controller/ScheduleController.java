package org.supercoding.supertime.schedule.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.supercoding.supertime.schedule.service.ScheduleService;
import org.supercoding.supertime.semester.dto.ScheduleResponseDto;
import org.supercoding.supertime.golbal.web.enums.IsFull;
import org.supercoding.supertime.golbal.web.enums.Part;

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
