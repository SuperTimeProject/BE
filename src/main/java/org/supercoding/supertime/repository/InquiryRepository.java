package org.supercoding.supertime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.supercoding.supertime.web.dto.inquiry.GetUnclosedInquiryDetailDto;
import org.supercoding.supertime.web.entity.Inquiry.InquiryEntity;
import org.supercoding.supertime.web.entity.enums.InquiryClosed;
import org.supercoding.supertime.web.entity.user.UserEntity;

import java.util.List;

public interface InquiryRepository extends JpaRepository<InquiryEntity, Long> {
    List<InquiryEntity> findAllByUser(UserEntity userEntity);
    List<InquiryEntity> findAllByIsClosed(InquiryClosed inquiryClosed);
}
