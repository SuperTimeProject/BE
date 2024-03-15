package org.supercoding.supertime.admin.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.supercoding.supertime.admin.service.AdminService;
import org.supercoding.supertime.schedule.service.ScheduleService;
import org.supercoding.supertime.admin.web.dto.GetPendingUserDetailDto;
import org.supercoding.supertime.admin.web.dto.GetPendingUserDto;
import org.supercoding.supertime.admin.web.dto.UpdateUserInfoRequestDto;
import org.supercoding.supertime.golbal.web.dto.CommonResponseDto;
import org.supercoding.supertime.inquiry.web.dto.GetUnclosedInquiryResponseDto;
import org.supercoding.supertime.golbal.web.enums.IsFull;
import org.supercoding.supertime.golbal.web.enums.Part;
import org.supercoding.supertime.golbal.web.enums.Valified;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "관리자 관련 API")
public class AdminController {

    private final AdminService adminService;
    private final ScheduleService scheduleService;

    @Operation(summary = "회원 인증상태 별 조회", description = "회원인증 인증상태에 따른 대기내역을 조회하는 api입니다.")
    @GetMapping("/pending-user/{page}")
    public ResponseEntity<GetPendingUserDto> getUserByValified(
            @RequestParam Valified valified,
            @PathVariable int page
    ){
        log.info("[ADMIN] 회원인증 인증상태 별 조회 요청이 들어왔습니다.");
        GetPendingUserDto getPendingResult = adminService.getUserByValified(valified,page);
        log.info("[ADMIN] 조회 결과 = " + getPendingResult);
        return ResponseEntity.ok().body(getPendingResult);
    }

    @Operation(summary = "회원 인증요청 상세 조회", description = "회원 인증요청 상세내용을 조회하는 api입니다.")
    @GetMapping("/pending-user/detail/{userId}")
    public ResponseEntity<GetPendingUserDetailDto> getValifiedDetail(
            @PathVariable String userId
    ){
        log.info("[ADMIN] 회원인증 인증상태 별 조회 요청이 들어왔습니다.");
        GetPendingUserDetailDto getValifiedDetailResult = adminService.getValifiedDetail(userId);
        log.info("[ADMIN] 조회 결과 = " + getValifiedDetailResult);
        return ResponseEntity.ok().body(getValifiedDetailResult);
    }

    @Operation(summary = "회원 인증 관리", description = "회원 인증상태를 변경하는 api입니다.")
    @PutMapping("/verification")
    public ResponseEntity<CommonResponseDto> verification(
            @RequestParam String userId,
            @RequestParam Valified valified
    ){
        log.info("[ADMIN] 회원인증 요청이 들어왔습니다.");
        CommonResponseDto verifiResult = adminService.varification(userId, valified);
        log.info("[ADMIN] 인증 결과 = " + verifiResult);
        return ResponseEntity.ok().body(verifiResult);
    }

    @Operation(summary = "회원 정보 수정", description = "회원의 모든 정보를 수정 할 수 있는 api입니다.")
    @PutMapping("/users-info")
    public ResponseEntity<CommonResponseDto> updateUserInfo(
            @RequestPart(name="userInfo")
            @Parameter(schema = @Schema(type = "string", format = "binary"))
            UpdateUserInfoRequestDto updateUserInfoRequestDto
            ){
        log.info("[ADMIN] 회원인증 요청이 들어왔습니다.");
        CommonResponseDto updateUserInfoResult = adminService.updateUserInfo(updateUserInfoRequestDto);
        log.info("[ADMIN] 인증 결과 = " + updateUserInfoResult);
        return ResponseEntity.ok().body(updateUserInfoResult);
    }


    @Operation(summary = "문의 조회하기", description = "문의기록을 조회하는 api입니다.")
    @GetMapping("/inquiry/{page}")
    public ResponseEntity<GetUnclosedInquiryResponseDto> getUnclosedInquiry(
            @PathVariable int page
    ){
        log.info("[ADMIN] 문의 조회 요청이 들어왔습니다.");
        GetUnclosedInquiryResponseDto getInquiryResult = adminService.getUnclosedInquiry(page);
        log.info("[ADMIN] 문의 조회 결과 = " + getInquiryResult);
        return ResponseEntity.ok().body(getInquiryResult);
    }

    @Operation(summary = "문의 답변하기", description = "문의에 대한 답변을 하는 api입니다.")
    @PutMapping("/inquiry/answer/{inquiryCid}")
    public ResponseEntity<CommonResponseDto> answerInquiry(
           @PathVariable Long inquiryCid,
           @RequestParam String inquiryContent
    ){
        log.info("[ADMIN] 문의 답변 요청이 들어왔습니다.");
        CommonResponseDto answerResult = adminService.answerInquiry(inquiryCid,inquiryContent);
        log.info("[ADMIN] 답변 결과 = " + answerResult);
        return ResponseEntity.ok().body(answerResult);
    }

    @Operation(summary = "문의 삭제하기", description = "문의를 삭제하는 api입니다.")
    @DeleteMapping("/inquiry/{inquiryCid}")
    public ResponseEntity<CommonResponseDto> deleteInquiry(
            @PathVariable Long inquiryCid
    ){
        log.info("[ADMIN] 문의 삭제 요청이 들어왔습니다.");
        CommonResponseDto deleteResult = adminService.deleteInquiry(inquiryCid);
        log.info("[ADMIN] 답변 결과 = " + deleteResult);
        return ResponseEntity.ok().body(deleteResult);
    }


    @Operation(summary = "시간표 생성", description = "시간표를 생성하는 api입니다.")
    @PostMapping(value = "/timetable",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
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
    @PutMapping(value = "/timetable", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
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
