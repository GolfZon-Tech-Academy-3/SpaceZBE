package com.golfzon.lastspacezbe.inquiry.entity;

import com.golfzon.lastspacezbe.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(sequenceName = "seq_inquiry", allocationSize = 1, name="seq_inquiry")
@Entity
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "inquiry_id")
    Long inquiryId; // 문의 번호

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 회원 번호
    @Column(name = "company_id")
    private Long companyId; // 사무공간 번호

    String inquiries; // 문의 내용
    String answers; // 문의 답변

    @CreationTimestamp
    LocalDateTime inquiriesTime; // 문의한 날짜

    public Inquiry(Long companyId,Member member, String inquiries) {
        this.companyId = companyId;
        this.member = member;
        this.inquiries = inquiries;
    }
}
