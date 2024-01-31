package org.supercoding.supertime.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.supercoding.supertime.repository.BoardRepository;
import org.supercoding.supertime.repository.SemesterRepository;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;
import org.supercoding.supertime.web.dto.semester.CreateSemesterRequestDto;
import org.supercoding.supertime.web.dto.semester.GetAllSemesterResponseDto;
import org.supercoding.supertime.web.dto.semester.GetSemesterDto;
import org.supercoding.supertime.web.entity.SemesterEntity;
import org.supercoding.supertime.web.entity.board.BoardEntity;
import org.supercoding.supertime.web.entity.enums.IsFull;
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

        // 기수에 대해 FULL, HALF에 두개 생성
        List<String> partList = Arrays.asList("FULL", "HALF");

        for(String part:partList){
            String detailName = createSemesterInfo.getSemesterName() + part;

            SemesterEntity newSemester = SemesterEntity.builder()
                    .semesterName(createSemesterInfo.getSemesterName())
                    .semesterDetailName(detailName)
                    .startDate(createSemesterInfo.getStartDate())
                    .isFull("FULL".equals(part) ? IsFull.FULL : IsFull.HALF)
                    .build();

            semesterRepository.save(newSemester);

            // 파트별 스터디 게시판 생성
            BoardEntity newSemesterPartBoard = BoardEntity.builder()
                    .boardName("스터디 게시판 ("+createSemesterInfo.getSemesterName()+part+")")
                    .build();
            boardRepository.save(newSemesterPartBoard);
        }
        // 결과 전달

        return CommonResponseDto.builder()
                .code(200)
                .success(true)
                .message("기수 생성이 완료되었습니다.")
                .build();
    }

    public GetAllSemesterResponseDto getAllSemester() {
        List<SemesterEntity> semesterList = semesterRepository.findAll();
        List<GetSemesterDto> resultList = new ArrayList<>();

        if(semesterList.isEmpty()){
            throw new NoSuchElementException("기수 리스트가 비어있습니다.");
        }
        for(SemesterEntity semester:semesterList){
            GetSemesterDto newSemesterDto = GetSemesterDto.builder()
                    .semesterCid(semester.getSemesterCid())
                    .semesterDetailName(semester.getSemesterDetailName())
                    .build();

            resultList.add(newSemesterDto);
        }

        return GetAllSemesterResponseDto.builder()
                .code(200)
                .success(true)
                .message("기수를 성공적으로 불러왔습니다.")
                .semesterList(resultList)
                .build();
    }
}
