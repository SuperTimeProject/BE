package org.supercoding.supertime.semester.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.supercoding.supertime.schedule.web.dto.GetSemesterDto;
import org.supercoding.supertime.semester.service.SemesterService;
import org.supercoding.supertime.golbal.web.dto.CommonResponseDto;
import org.supercoding.supertime.schedule.web.dto.CreateSemesterRequestDto;
import org.supercoding.supertime.schedule.web.dto.GetAllSemesterResponseDto;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "기수 관리 API")
public class SemesterController {
    private final SemesterService semesterService;

    @Operation(summary = "기수 생성", description = "기수를 생성하는 api입니다.")
    @PostMapping("/admin/semester")
    public ResponseEntity<CommonResponseDto> createSemester(@RequestBody CreateSemesterRequestDto createSemesterInfo, @AuthenticationPrincipal User user){
        log.debug("[SEMESTER] 기수 생성 요청이 들어왔습니다.");
        semesterService.createSemester(createSemesterInfo, user);
        log.debug("[SEMESTER] 기수가 성공적으로 생성되었습니다.");

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponseDto.createSuccessResponse("기수 생성이 완료되었습니다."));
    }

    @Operation(summary = "전체 기수 불러오기", description = "생성되어있는 기수를 불러오는 api입니다.")
    @GetMapping("/public/semester")
    public ResponseEntity<GetAllSemesterResponseDto> getAllSemester(){
        log.debug("[SEMESTER] 기수 리스트 불러오기 요청이 들어왔습니다.");
        List<GetSemesterDto> resultList = semesterService.getAllSemester();
        log.debug("[SEMESTER] 기수 리스트를 성공적으로 불러왔습니다.");

        return ResponseEntity.ok().body(GetAllSemesterResponseDto.successResponse("기수를 성공적으로 불러왔습니다.", resultList));
    }
}
