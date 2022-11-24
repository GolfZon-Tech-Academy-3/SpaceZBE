package com.golfzon.lastspacezbe.company.repository;

import com.golfzon.lastspacezbe.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company,Long> {

    // 관심등록 수가 많은 8개 순으로 가져오기
    List<Company> findTop8ByOrderByLikeCountDesc();
    // 최근 등록된 순으로 4개 가져오기
    List<Company> findTop4ByOrderByCreatedTimeDesc();
}
