package org.supercoding.supertime.inquiry.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
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
import org.supercoding.supertime.inquiry.service.InquiryService;
import org.supercoding.supertime.inquiry.web.dto.InquiryDetailDto;
import org.supercoding.supertime.inquiry.web.dto.InquiryDetailResponseDto;
import org.supercoding.supertime.inquiry.web.dto.InquiryRequestDto;
import org.supercoding.supertime.inquiry.web.dto.InquiryResponseDto;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user")
@RequiredArgsConstructor
@Builder
@Tag(name = "유저 문의 API")
public class InquiryController {

    private final InquiryService inquiryService;

    @Operation(summary = "유저 문의 상세 조회", description = "유저의 문의 상세 내역을 불러오는 API입니다.")
    @GetMapping("/user-inquiry/detail/{inquiryCid}")
    public ResponseEntity<InquiryDetailResponseDto> getInquiryDetail(
            @AuthenticationPrincipal User user,
            @PathVariable Long inquiryCid
    ) {
        log.debug("[MY_PAGE] 유저 문의 상세 조회 요청이 들어왔습니다.");
        InquiryDetailDto inquiryDetail = inquiryService.getInquiryDetail(user, inquiryCid);
        log.debug(("[MY_PAGE] 유저 문의 상세 내용을 성공적으로 조회하였습니다."));

        return ResponseEntity.ok(InquiryDetailResponseDto.from("유저 문의 상세 내용을 성공적으로 불러왔습니다.", inquiryDetail));
    }

    @Operation(summary = "유저 문의 조회", description = "유저가 작성한 문의를 불러오는 API입니다.")
    @GetMapping("/user-inquiry")
    public ResponseEntity<InquiryResponseDto> getUserInquiry(
            @AuthenticationPrincipal User user,
            @RequestParam int page
    ) {
        log.debug("[MY_PAGE] 유저 문의 목록 조회 요청이 들어왔습니다.");
        List<InquiryDetailDto> inquiryList = inquiryService.getUserInquiry(user, page);
        log.debug(("[MY_PAGE] 유저 문의를 성공적으로 불러왔습니다."));

        return ResponseEntity.ok(InquiryResponseDto.from("문의 리스트를 성공적으로 불러왔습니다.", inquiryList));
    }

    @Operation(summary = "문의 작성", description = "문의를 작성하는 API입니다.")
    @PostMapping(value = "/inquiry", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponseDto> createInquiry(
            @AuthenticationPrincipal User user,
            @RequestPart(name = "inquiryInfo") @Parameter(schema = @Schema(type = "string", format = "binary")) InquiryRequestDto inquiryRequestDto,
            @RequestPart(name = "inquiryImage", required = false) List<MultipartFile> inquiryImage
    ) {
        log.debug("[MY_PAGE] 문의하기 작성 요청이 들어왔습니다.");
        inquiryService.createInquiry(user, inquiryRequestDto, inquiryImage);
        log.debug("[MY_PAGE] 문의하기를 성공적으로 추가하였습니다.");

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponseDto.successResponse("문의를 성공적으로 추가하였습니다."));
    }
}
