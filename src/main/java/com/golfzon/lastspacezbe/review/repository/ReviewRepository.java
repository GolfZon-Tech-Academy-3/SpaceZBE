package com.golfzon.lastspacezbe.review.repository;

import com.golfzon.lastspacezbe.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ReviewRepository extends JpaRepository<Review,Long> {

    //상세페이지 리뷰 조회
    Page<Review> findAllByCompanyIdOrderByReviewTimeDesc(Pageable pageable, Long companyId);

    //리뷰 조회
    Review findByReviewId(Long reviewId);

    //리뷰 조회
    List<Review> findAllByCompanyId(Long companyId);

    //리뷰 개수 조회
    int countByCompanyId(Long companyId);

    //예약 이력 리뷰 조회
    Review findByReservationIdAndMemberId(Long reservationId, Long memberId);
}
