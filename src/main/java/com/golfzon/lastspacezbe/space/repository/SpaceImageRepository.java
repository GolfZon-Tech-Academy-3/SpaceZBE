package com.golfzon.lastspacezbe.space.repository;

import com.golfzon.lastspacezbe.space.entity.Space;
import com.golfzon.lastspacezbe.space.entity.SpaceImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SpaceImageRepository extends JpaRepository<SpaceImage,Long> {
    List<SpaceImage> findAllBySpace(Space space);
}
