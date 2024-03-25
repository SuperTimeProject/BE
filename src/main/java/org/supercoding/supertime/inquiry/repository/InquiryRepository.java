package org.supercoding.supertime.inquiry.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.supercoding.supertime.inquiry.web.entity.InquiryEntity;
import org.supercoding.supertime.golbal.web.enums.InquiryClosed;
import org.supercoding.supertime.user.entity.user.UserEntity;

public interface InquiryRepository extends JpaRepository<InquiryEntity, Long> {
    Page<InquiryEntity> findAllByUser(UserEntity userEntity,Pageable pageable);
    Page<InquiryEntity> findAllByIsClosed(InquiryClosed inquiryClosed, Pageable pageable);
}
