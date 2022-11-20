package com.golfzon.lastspacezbe.member.repository;

import com.golfzon.lastspacezbe.company.entity.Company;
import com.golfzon.lastspacezbe.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByMemberName(String memberName);

    // memberId 로 Company 업체 조회
    Optional<Company> findByMemberId(Long memberId);
}
