package com.golfzon.lastspacezbe.member.repository;

import com.golfzon.lastspacezbe.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByMemberName(String memberName);

    // memberId 로 Company 업체 조회
    Member findByMemberId(Long memberId);

    Optional<Member> findById(Long memberId);

    List<Member> findAllByAuthority(String authority);

    @Query (value = "select m from Member m where m.email like 'member@gmail.com' and m.authority like 'member'")
    Member findMemberBySearchWord(String searchWord);

    List<Member> findByEmailLikeOrMemberNameLikeAndAuthority(String searchWord, String searchWord1, String member);
}
