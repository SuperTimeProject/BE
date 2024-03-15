package org.supercoding.supertime.inquiry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supercoding.supertime.inquiry.web.entity.InquiryImageEntity;

@Repository
public interface InquiryImageRepository extends JpaRepository<InquiryImageEntity, Long> {

}
