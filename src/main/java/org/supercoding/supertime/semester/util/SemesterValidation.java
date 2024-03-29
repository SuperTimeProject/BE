package org.supercoding.supertime.semester.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.supercoding.supertime.golbal.web.advice.CustomDataIntegerityCiolationException;
import org.supercoding.supertime.golbal.web.advice.CustomNoSuchElementException;
import org.supercoding.supertime.semester.repository.SemesterRepository;
import org.supercoding.supertime.semester.web.entity.SemesterEntity;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Component
public class SemesterValidation {
    private final SemesterRepository semesterRepository;

    public void validateSemesterExist (int semesterName) {
        semesterRepository.findBySemesterName(semesterName)
                .orElseThrow(() -> new CustomDataIntegerityCiolationException("이미 중복된 기수가 존재합니다."));
    }

    public List<SemesterEntity> validateSemesterIsEmpty () {
        List<SemesterEntity> semesterEntities = semesterRepository.findAll();

        Date currentDate = new Date();

        List<SemesterEntity> futureSemesters = semesterEntities.stream()
                .filter(semester -> semester.getStartDate().after(currentDate))
                .collect(Collectors.toList());

        if(futureSemesters.isEmpty()) {
            throw new CustomNoSuchElementException("등록 가능한 기수가 존재하지 않습니다.");
        }

        return futureSemesters;
    }

}
