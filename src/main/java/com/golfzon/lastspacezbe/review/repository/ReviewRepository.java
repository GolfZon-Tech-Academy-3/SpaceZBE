package com.golfzon.lastspacezbe.review.repository;

import com.golfzon.lastspacezbe.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ReviewRepository extends JpaRepository<Review,Long> {

    Review findBySpaceId(Long spaceId);

    Page<Review> findAllByCompanyIdOrderByReviewTimeDesc(Pageable pageable, Long companyId);

    Review findByReviewId(Long reviewId);

    List<Review> findAllByCompanyId(Long companyId);

    int countByCompanyId(Long companyId);

    Review findByReservationIdAndMemberId(Long reservationId, Long memberId);
}
