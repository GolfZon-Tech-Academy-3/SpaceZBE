package com.golfzon.lastspacezbe.company.entity;

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
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(sequenceName = "seq_company", allocationSize = 1, name="seq_company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "company_id")
    Long companyId; // 예약 번호

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "company_name")
    String companyName; // 업체명
    String info; // 업체 장소 소개
    String rules; // 이용 규칙
    String location; // 업체 위치

    String details; // 상세 주소
    String summary; // 소개 요약
    String approveStatus; //승인 상태(활동중, 승인대기, 활동중지)
    int likeCount; // 관심등록수

    String imageName; // 이미지 이름
    @CreationTimestamp
    LocalDateTime createdTime; // 업체 등록 날짜

    public Company(Member member, String companyName, String info,
                   String rules, String location, String details, String summary, String approveStatus) {
        this.member = member;
        this.companyName = companyName;
        this.info = info;
        this.rules = rules;
        this.location = location;
        this.details = details;
        this.summary = summary;
        this.approveStatus = approveStatus;
    }

    @Override
    public String toString() {
        return "Company{" +
                "companyId=" + companyId +
                ", member=" + member +
                ", companyName='" + companyName + '\'' +
                ", info='" + info + '\'' +
                ", rules='" + rules + '\'' +
                ", location='" + location + '\'' +
                ", summary='" + summary + '\'' +
                ", approveStatus='" + approveStatus + '\'' +
                ", likeCount=" + likeCount +
                ", createdTime=" + createdTime +
                '}';
    }
}
