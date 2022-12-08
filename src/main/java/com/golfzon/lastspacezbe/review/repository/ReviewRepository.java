package com.golfzon.lastspacezbe.review.repository;

import com.golfzon.lastspacezbe.review.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ReviewRepository extends JpaRepository<Review,Long> {

    //List<Review> findAllByCompanyId(Long companyId);

    Review findBySpaceId(Long spaceId);

    //List<Review> findAllByOrderByReviewTimeDesc(Pageable pageable);

    List<Review> findAllByCompanyIdOrderByReviewTimeDesc(Pageable pageable, Long companyId);

    Review findBySpaceIdAndMemberId(Long spaceId, Long memberId);

    Review findByReviewId(Long reviewId);

    List<Review> findAllByCompanyId(Long companyId);
}
