package com.golfzon.lastspacezbe.space.repository;

import com.golfzon.lastspacezbe.space.entity.Space;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface SpaceRepository extends JpaRepository<Space,Long> {
    List<Space> findAllByCompanyId(Long companyId);

    Space findBySpaceId(Long spaceId);

    @Query
            (value = "select distinct company_id from space where type like '오피스' order by company_id desc",
            countQuery = "select distinct company_id from space where type like '오피스' order by company_id desc",
            nativeQuery = true)
    List<Long> findAllByOfficeOrderByCompanyIdDesc(Pageable pageable);

    @Query
            (value = "select distinct company_id from space where type like '데스크' order by company_id desc",
                    countQuery = "select distinct company_id from space where type like '데스크' order by company_id desc",
                    nativeQuery = true)
    List<Long> findAllByDeskOrderByCompanyIdDesc(Pageable pageable);

    @Query
            (value = "select distinct company_id from space where type like '%회의실%' order by company_id desc",
                    countQuery = "select distinct company_id from space where type like '%회의실%' order by company_id desc",
                    nativeQuery = true)
    List<Long> findAllByMeetingRoomOrderByCompanyIdDesc(Pageable pageable);
}
