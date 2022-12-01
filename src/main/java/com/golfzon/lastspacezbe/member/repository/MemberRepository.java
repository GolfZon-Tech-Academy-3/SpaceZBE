package com.golfzon.lastspacezbe.member.repository;

import com.golfzon.lastspacezbe.company.entity.Company;
import com.golfzon.lastspacezbe.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.xml.bind.ValidationEventLocator;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByMemberName(String memberName);

    // memberId 로 Company 업체 조회
    Optional<Company> findByMemberId(Long memberId);

    Optional<Member> findById(Long memberId);

    List<Member> findAllByAuthority(String authority);

    @Query
            (value = "select * from member where email like :searchWord or member_name like :searchWord and authority like 'member'",
                    nativeQuery = true)
    List<Member> findMemberBySearchWord(@Param("searchWord") String searchWord);
}
