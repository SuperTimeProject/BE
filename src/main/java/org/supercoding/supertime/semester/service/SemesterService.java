package org.supercoding.supertime.semester.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supercoding.supertime.admin.util.AdminValidation;
import org.supercoding.supertime.chat.service.ChatRoomService;
import org.supercoding.supertime.board.repository.BoardRepository;
import org.supercoding.supertime.semester.repository.SemesterRepository;
import org.supercoding.supertime.schedule.web.dto.CreateSemesterRequestDto;
import org.supercoding.supertime.schedule.web.dto.GetSemesterDto;
import org.supercoding.supertime.semester.util.SemesterValidation;
import org.supercoding.supertime.semester.web.entity.SemesterEntity;
import org.supercoding.supertime.board.web.entity.BoardEntity;
import org.supercoding.supertime.golbal.web.enums.IsFull;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SemesterService {
    private final SemesterRepository semesterRepository;
    private final BoardRepository boardRepository;
    private final ChatRoomService chatRoomService;

    private final SemesterValidation semesterValidation;
    private final AdminValidation adminValidation;

    @Value("${spring.default.part_list}")
    private List<String> partList;
    @Value("${spring.default.time_list}")
    private List<String> timeList;

    /**
     * 기능 - 기수 생성
     *
     * @param createSemesterInfo
     *
     * @return void
     */
    @Transactional
    public void createSemester(CreateSemesterRequestDto createSemesterInfo, User user) {

        adminValidation.validateAdminRole(user);

        int semesterName = createSemesterInfo.getSemesterName();

        semesterValidation.validateSemesterExist(semesterName);

        createSemesterBoard(semesterName);

        createSemesterEntity(createSemesterInfo);

        chatRoomService.createDefaultRoom(String.valueOf(createSemesterInfo.getSemesterName()));
    }

    private void createSemesterBoard(int semesterName) {
        boardRepository.save(BoardEntity.toSemesterBoard(semesterName));

        for(String part: partList){
            boardRepository.save(BoardEntity.toStudyBoard(semesterName, part));
        }
    }

    private void createSemesterEntity(CreateSemesterRequestDto semesterInfo) {
        for(String isFull: timeList){
            String detailName = semesterInfo.getSemesterName() + isFull;
            semesterRepository.save(SemesterEntity.from(semesterInfo, detailName, getIsFullEnum(isFull)));
        }
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

    /**
     * 기능 - 모든 기수 조회
     *
     * @return List<SemesterEntity>
     */
    @Transactional(readOnly = true)
    public List<GetSemesterDto> getAllSemester() {

        List<GetSemesterDto> resultList = new ArrayList<>();

        for(SemesterEntity semester: semesterValidation.validateSemesterIsEmpty()){
            resultList.add(GetSemesterDto.from(semester));
        }

        return resultList;
    }
}
