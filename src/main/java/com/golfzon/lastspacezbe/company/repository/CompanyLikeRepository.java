package com.golfzon.lastspacezbe.company.repository;

import com.golfzon.lastspacezbe.company.entity.Company;
import com.golfzon.lastspacezbe.company.entity.CompanyLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyLikeRepository extends JpaRepository<CompanyLike,Long> {
    //관심등록 확인
    CompanyLike findByCompanyAndMemberId(Company company, Long memberId);
}
