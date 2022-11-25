package com.golfzon.lastspacezbe.review.repository;

import com.golfzon.lastspacezbe.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ReviewRepository extends JpaRepository<Review,Long> {
    List<Review> findAllBySpaceId(Long spaceId);

    List<Review> findAllByCompanyId(Long companyId);

    Review findBySpaceId(Long spaceId);
}
