package com.golfzon.lastspacezbe.space.repository;

import com.golfzon.lastspacezbe.space.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;


public interface SpaceRepository extends JpaRepository<Space,Long> {
    List<Space> findAllByCompanyId(Long companyId);

    Space findBySpaceId(Long spaceId);

    // 오피스로 등록된 공간 모두 조회
    @Query
            (value = "select distinct company_id from space where type like '오피스'",
            nativeQuery = true)
    Set<Long> findAllCompanyIdByOffice();
    // 오피스로 등록된 공간 등록된 순으로 9개씩 조회
    @Query
            (value = "select distinct company_id from space where type like '오피스' order by company_id desc",
            nativeQuery = true)
    List<Long> findAllOfficeCompany();

    // 데스크로 등록된 공간 모두 조회
    @Query
            (value = "select distinct company_id from space where type like '데스크'",
                    nativeQuery = true)
    Set<Long> findAllCompanyIdByDesk();
    // 데스크로 등록된 공간 등록된 순으로 9개씩 조회
    @Query
            (value = "select distinct company_id from space where type like '데스크' order by company_id desc",
                    nativeQuery = true)
    List<Long> findAllByDeskOrderByCompanyIdDesc();

    // 회의실로 등록된 공간 모두 조회
    @Query
            (value = "select distinct company_id from space where type like '%회의실%'",
                    nativeQuery = true)
    Set<Long> findAllCompanyIdByMeetingRoom();
    // 회의실로 등록된 공간 등록된 순으로 9개씩 조회
    @Query
            (value = "select distinct company_id from space where type like '%회의실%' order by company_id desc",
                    nativeQuery = true)
    List<Long> findAllByMeetingRoomOrderByCompanyIdDesc();

    // 최근 등록된 순으로 업체 아이디 가져오기
    @Query
            (nativeQuery = true,
                    value = "select company_id from space order by space_id desc",
                    countQuery = "select company_id from space order by space_id desc"
            )
    Set<Long> findAllCompanyIds();
}
