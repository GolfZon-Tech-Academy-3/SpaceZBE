package com.golfzon.lastspacezbe.member.repository;

import com.golfzon.lastspacezbe.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByMemberName(String memberName);

    // memberId 로 Company 업체 조회
    Member findByMemberId(Long memberId);

    Optional<Member> findById(Long memberId);

    List<Member> findAllByAuthority(String authority);

    @Query (nativeQuery = true, value = "select * from (select * from member where authority='member') as member where member.email like ?1 or member.member_name like ?1")
    List<Member> findMembers(String searchWord);
}
