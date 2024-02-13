package org.supercoding.supertime.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.supercoding.supertime.repository.BoardRepository;
import org.supercoding.supertime.repository.SemesterRepository;
import org.supercoding.supertime.web.advice.CustomNoSuchElementException;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;
import org.supercoding.supertime.web.dto.semester.CreateSemesterRequestDto;
import org.supercoding.supertime.web.dto.semester.GetAllSemesterResponseDto;
import org.supercoding.supertime.web.dto.semester.GetSemesterDto;
import org.supercoding.supertime.web.entity.SemesterEntity;
import org.supercoding.supertime.web.entity.board.BoardEntity;
import org.supercoding.supertime.web.entity.enums.IsFull;
import org.supercoding.supertime.web.entity.enums.Part;
import org.supercoding.supertime.web.entity.enums.Roles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SemesterService {
    private final SemesterRepository semesterRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public CommonResponseDto createSemester(CreateSemesterRequestDto createSemesterInfo) {
        // 중복된 기수가 있는지 확인
        Boolean isExist = semesterRepository.existsBySemesterName(createSemesterInfo.getSemesterName());
        if(isExist){
            throw new DataIntegrityViolationException("이미 중복된 기수가 존재합니다.");
        }
        BoardEntity newSemesterBoard = BoardEntity.builder()
                .boardName("기수 게시판 ("+createSemesterInfo.getSemesterName()+")")
                .build();
        boardRepository.save(newSemesterBoard);

        List<String> enumList = Arrays.asList("FULL", "HALF");

        for(String isFull:enumList){
            String detailName = createSemesterInfo.getSemesterName() + isFull;

            SemesterEntity newSemester = SemesterEntity.builder()
                    .semesterName(createSemesterInfo.getSemesterName())
                    .semesterDetailName(detailName)
                    .startDate(createSemesterInfo.getStartDate())
                    .isFull(getIsFullEnum(isFull))
                    .build();

            semesterRepository.save(newSemester);

            // 파트별 스터디 게시판 생성
            BoardEntity newSemesterPartBoard = BoardEntity.builder()
                    .boardName("스터디 게시판 ("+createSemesterInfo.getSemesterName()+isFull+")")
                    .build();
            boardRepository.save(newSemesterPartBoard);
        }
        // 결과 전달

        return CommonResponseDto.createSuccessResponse("기수 생성이 완료되었습니다.");
    }

    private static IsFull getIsFullEnum(String inputString) {
        switch (inputString) {
            case "FULL":
                return IsFull.FULL;
            case "HALF":
                return IsFull.HALF;
            default:
                // 부적절한 문자열인 경우에는 null을 반환하거나 예외처리를 수행할 수 있음
                return null;
        }
    }

    public GetAllSemesterResponseDto getAllSemester() {
        List<SemesterEntity> semesterList = semesterRepository.findAll();
        List<GetSemesterDto> resultList = new ArrayList<>();

        if(semesterList.isEmpty()){
            throw new CustomNoSuchElementException("기수 리스트가 비어있습니다.");
        }
        for(SemesterEntity semester:semesterList){
            GetSemesterDto newSemesterDto = GetSemesterDto.builder()
                    .semesterCid(semester.getSemesterCid())
                    .semesterDetailName(semester.getSemesterDetailName())
                    .build();

            resultList.add(newSemesterDto);
        }

        return GetAllSemesterResponseDto.successResponse("기수를 성공적으로 불러왔습니다.", resultList);
    }
}
