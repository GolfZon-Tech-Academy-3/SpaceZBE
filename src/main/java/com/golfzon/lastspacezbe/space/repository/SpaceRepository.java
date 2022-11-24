package com.golfzon.lastspacezbe.space.repository;

import com.golfzon.lastspacezbe.company.entity.Company;
import com.golfzon.lastspacezbe.space.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SpaceRepository extends JpaRepository<Space,Long> {
    List<Space> findAllByCompanyId(Long companyId);

    Space findBySpaceId(Long spaceId);

}
