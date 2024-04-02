package org.supercoding.supertime.inquiry.web.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.supercoding.supertime.golbal.web.entity.TimeEntity;
import org.supercoding.supertime.golbal.web.enums.InquiryClosed;
import org.supercoding.supertime.inquiry.web.dto.InquiryRequestDto;
import org.supercoding.supertime.user.web.entity.user.UserEntity;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "inquiry_table")
public class InquiryEntity extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_cid")
    @Schema(description = "문의 식별번호", example = "1")
    private Long inquiryCid;

    @ManyToOne
    @JoinColumn(name = "user_cid", nullable = false)
    @Schema(description = "사용자 식별번호")
    private UserEntity user;

    @Column(name = "inquiry_title")
    @Schema(description = "문의 제목", example = "문의 제목 예시")
    private String inquiryTitle;

    @Column(name = "inquiry_content", columnDefinition = "TEXT")
    @Schema(description = "문의 내용")
    private String inquiryContent;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<InquiryImageEntity> inquiryImages;

    @Column(name = "answer", columnDefinition = "TEXT")
    @Schema(description = "답변 내용")
    private String answer;

    @Column(name = "isClosed")
    @Schema(description = "문의 닫힘 여부 (1 or 0)", example = "1")
    private InquiryClosed isClosed;

    public static InquiryEntity from(InquiryRequestDto inquiryRequestDto, UserEntity userEntity) {
        return InquiryEntity.builder()
                .inquiryTitle(inquiryRequestDto.getInquiryTitle())
                .inquiryContent(inquiryRequestDto.getInquiryContent())
                .user(userEntity)
                .isClosed(InquiryClosed.OPEN)
                .build();
    }
}
