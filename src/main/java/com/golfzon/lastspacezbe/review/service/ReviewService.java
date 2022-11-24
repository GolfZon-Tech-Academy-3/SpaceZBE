package com.golfzon.lastspacezbe.review.service;

import com.golfzon.lastspacezbe.review.dto.ReviewDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    // 공간에 등록된 리뷰 가져오기
    public List<ReviewDto> spaceReviews() {
        return null;
    }

    // 리뷰 등록
    public void reviewRegister(ReviewDto requestDto) {
    }

    // 리뷰 수정
    public void reviewUpdate(Long reviewId, ReviewDto requestDto) {
    }

    // 리뷰 삭제
    public void reviewDelete(Long reviewId) {
    }



}
