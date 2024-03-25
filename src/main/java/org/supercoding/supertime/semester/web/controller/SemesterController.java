package org.supercoding.supertime.semester.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.supercoding.supertime.semester.service.SemesterService;
import org.supercoding.supertime.golbal.web.dto.CommonResponseDto;
import org.supercoding.supertime.schedule.web.dto.CreateSemesterRequestDto;
import org.supercoding.supertime.schedule.web.dto.GetAllSemesterResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "기수 관리 API")
public class SemesterController {
    private final SemesterService semesterService;

    @Operation(summary = "기수 생성", description = "기수를 생성하는 api입니다.")
    @PostMapping("/admin/semester/create")
    public ResponseEntity<CommonResponseDto> createSemester(@RequestBody CreateSemesterRequestDto createSemesterInfo){
        log.info("[SEMESTER] 기수 생성 요청이 들어왔습니다.");
        CommonResponseDto createSemesterResult = semesterService.createSemester(createSemesterInfo);
        log.info("[SEMESTER] 기수 생성 결과 = " + createSemesterResult);

        return ResponseEntity.status(HttpStatus.CREATED).body(createSemesterResult);
    }

    @Operation(summary = "전체 기수 불러오기", description = "생성되어있는 기수를 불러오는 api입니다.")
    @GetMapping("/public/semester/all")
    public ResponseEntity<GetAllSemesterResponseDto> getAllSemester(){
        log.info("[SEMESTER] 기수 리스트 불러오기 요청이 들어왔습니다.");
        GetAllSemesterResponseDto getAllSemesterResult = semesterService.getAllSemester();
        log.info("[SEMESTER] 기수 리스트 불러오기 결과 = " + getAllSemesterResult);

        return ResponseEntity.ok().body(getAllSemesterResult);
    }
}
