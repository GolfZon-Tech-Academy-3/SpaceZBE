package com.golfzon.lastspacezbe.space.repository;

import com.golfzon.lastspacezbe.space.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SpaceRepository extends JpaRepository<Space,Long> {


    Space findAllBySpaceId(Long spaceId);
}
