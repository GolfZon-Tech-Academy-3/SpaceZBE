package com.golfzon.lastspacezbe.mileage.repository;

import com.golfzon.lastspacezbe.mileage.entity.Mileage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MileageRepository extends JpaRepository<Mileage,Long> {

    List<Mileage> findAllByMemberId(Long memberId);

    List<Mileage> findAllBySpaceId(Long spaceId);
}
