package com.golfzon.lastspacezbe.member.repository;

import com.golfzon.lastspacezbe.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    Optional<Member> findByMemberName(String memberName);

    // memberId 로 Company 업체 조회
    Member findByMemberId(Long memberId);

    Optional<Member> findById(Long memberId);

    // 권한으로 member 조회
    List<Member> findAllByAuthority(String authority);

    // 마스터 권한 승격될 멤버 조회
    @Query (nativeQuery = true, value = "select * from (select * from member where authority='member') as member where member.username like ?1 or member.member_name like ?1")
    List<Member> findMembers(String searchWord);

    Optional<Member> findByUsername(String username);
}
