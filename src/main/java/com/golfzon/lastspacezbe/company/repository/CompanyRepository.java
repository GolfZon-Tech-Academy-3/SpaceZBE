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
    List<Company> findTop8ByApproveStatusOrderByReviewAvgDesc(String status);

    // 최근 등록된 순으로 4개 가져오기
    List<Company> findTop4ByApproveStatusOrderByCreatedTimeDesc(String status);

    // 승인 거절용 찾기
    Company findByCompanyId(Long companyId);

    // jwt companyId 넣기
    Company findByMember(Member member);

    // 최근 등록된 순으로 가져오기
    @Query
            (nativeQuery = true,
                    value = "select * from company where company_id in (:companyIdList) and approve_status='001' order by created_time desc",
                    countQuery = "select * from company where company_id in (:companyIdList) and approve_status='001' order by created_time desc"
            )
    Page<Company> findAllByOrderByCreatedTimeDesc(Pageable pageable, @Param("companyIdList") Set<Long> companyIdList);

    // 업체검색 조회
    @Query
            (nativeQuery = true,
                    value = "select * from company where company_id in (:companyIdList) and approve_status='001'",
                    countQuery = "select * from company where company_id in (:companyIdList) and approve_status='001'"
            )
    Page<Company> findAllCompany(@Param("companyIdList") List<Long> companyIdList, Pageable pageable);

    // 업체검색 중 지역 검색
    @Query
            (nativeQuery = true,
                    value = "select company_id from (select * from company where approve_status='001') company where location like :location or company_name like :location"
            )
    Set<Long> findAllByLocation(@Param("location") String location);

    // 업체검색 페이지네이션 및 최근등록된 순으로 가져오기
    @Query
            (nativeQuery = true,
                    value = "select * from company where company_id in (:companyIds) and approve_status='001' order by created_time desc",
                    countQuery = "select * from company where company_id in (:companyIds)  and approve_status='001' order by created_time desc"
            )
    Page<Company> findAllByCompanyIdOrderByCreatedTimeDesc(Pageable pageable, @Param("companyIds") Set<Long> companyIds);

    // 업체 조회
    @Query
            (nativeQuery = true,
                    value = "select * from company where company_id in (:companyIds) and approve_status='001' order by company_id asc"
            )
    List<Company> findAllByCompanyIds(@Param("companyIds") Set<Long> companyIdList);

}
