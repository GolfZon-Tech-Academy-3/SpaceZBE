package com.golfzon.lastspacezbe.company.repository;

import com.golfzon.lastspacezbe.company.entity.Company;
import com.golfzon.lastspacezbe.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface CompanyRepository extends JpaRepository<Company,Long> {

    // 관심등록 수가 많은 8개 순으로 가져오기
    List<Company> findTop8ByOrderByLikeCountDesc();
    // 최근 등록된 순으로 4개 가져오기
    List<Company> findTop4ByOrderByCreatedTimeDesc();

    // 승인 거절용 찾기
    Company findByCompanyId(Long companyId);

    // jwt companyId 넣기
    Company findByMember(Member member);

    // 최근 등록된 순으로 가져오기
    @Query
            (nativeQuery = true,
                    value = "select * from company order by created_time desc"
            )
    List<Company> findAllByOrderByCreatedTimeDesc(Pageable pageable);

    @Query
            (nativeQuery = true,
                    value = "select * from company where company_id in (:companyIdList)"
            )
    List<Company> findAllByCompanyId(@Param("companyIdList") List<Long> companyIdList);


    @Query
            (nativeQuery = true,
                    value = "select company_id from company where location like ?1"
            )
    Set<Long> findAllByLocation(String location);

    @Query
            (nativeQuery = true,
                    value = "select * from company where company_id in (:companyIds) order by created_time desc",
                    countQuery = "select * from company where company_id in (:companyIds) order by created_time desc"
            )
    List<Company> findAllByCompanyIdOrderByCreatedTimeDesc(Pageable pageable, @Param("companyIds") Set<Long> companyIds);

    @Query
            (nativeQuery = true,
                    value = "select company_id from company"
            )
    Set<Long> findAllCompanyIds();
}
