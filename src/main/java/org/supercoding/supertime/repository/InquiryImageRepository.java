package org.supercoding.supertime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supercoding.supertime.web.entity.Inquiry.InquiryImageEntity;

@Repository
public interface InquiryImageRepository extends JpaRepository<InquiryImageEntity, Long> {

}
