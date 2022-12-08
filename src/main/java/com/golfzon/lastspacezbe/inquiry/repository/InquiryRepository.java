package com.golfzon.lastspacezbe.inquiry.repository;

import com.golfzon.lastspacezbe.inquiry.entity.Inquiry;
import com.golfzon.lastspacezbe.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry,Long> {

    // 사무공간의 문의 내역
    List<Inquiry> findAllByCompanyIdOrderByInquiriesTimeDesc(Long companyId);

    List<Inquiry> findAllByCompanyId(Long companyId);
    // 마이페이지
    List<Inquiry> findAllByMember(Member member);
}
