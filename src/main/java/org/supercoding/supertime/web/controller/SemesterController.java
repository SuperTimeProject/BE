package org.supercoding.supertime.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.supercoding.supertime.service.SemesterService;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;
import org.supercoding.supertime.web.dto.semester.CreateSemesterRequestDto;
import org.supercoding.supertime.web.dto.semester.GetAllSemesterResponseDto;

@RestController
@Slf4j
@RequestMapping("/semester")
@RequiredArgsConstructor
@Tag(name = "기수 관리 API", description = "기수에대한 api입니다.")
public class SemesterController {
    private final SemesterService semesterService;

    @Operation(summary = "기수 생성", description = "기수를 생성하는 api입니다.")
    @PostMapping("/createSemester")
    public ResponseEntity<CommonResponseDto> createSemester(@RequestBody CreateSemesterRequestDto createSemesterInfo){
        log.info("[SEMESTER] 기수 생성 요청이 들어왔습니다.");
        CommonResponseDto createSemesterResult = semesterService.createSemester(createSemesterInfo);
        log.info("[SEMESTER] 기수 생성 결과 = " + createSemesterResult);

        return ResponseEntity.ok().body(createSemesterResult);
    }

    @Operation(summary = "전체 기수 불러오기", description = "생성되어있는 기수를 불러오는 api입니다.")
    @PostMapping("/getAllSemester")
    public ResponseEntity<GetAllSemesterResponseDto> getAllSemester(){
        log.info("[SEMESTER] 기수 리스트 불러오기 요청이 들어왔습니다.");
        GetAllSemesterResponseDto getAllSemesterResult = semesterService.getAllSemester();
        log.info("[SEMESTER] 기수 리스트 불러오기 결과 = " + getAllSemesterResult);

        return ResponseEntity.ok().body(getAllSemesterResult);
    }
}
