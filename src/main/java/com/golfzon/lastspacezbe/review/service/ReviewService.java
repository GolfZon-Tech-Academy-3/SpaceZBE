package com.golfzon.lastspacezbe.review.service;

import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.member.repository.MemberRepository;
import com.golfzon.lastspacezbe.review.dto.ReviewDto;
import com.golfzon.lastspacezbe.review.entity.Review;
import com.golfzon.lastspacezbe.review.repository.ReviewRepository;
import com.golfzon.lastspacezbe.space.entity.Space;
import com.golfzon.lastspacezbe.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final SpaceRepository spaceRepository;

    // 업체에 등록된 리뷰 가져오기
    public List<ReviewDto> spaceReviews(Long companyId) {
        log.info("companyId:{}",companyId);

        List<Review> reviewList = reviewRepository.findAllByCompanyId(companyId);
        List<ReviewDto> reviews = new ArrayList<>();

        if(reviewList.size()>0){
            for (Review review:reviewList) {
                ReviewDto dto = new ReviewDto();
                dto.setRating(review.getRating()); //별점
                dto.setContent(review.getContent()); //리뷰내용
                Member member = memberRepository.findById(review.getMemberId())
                        .orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"리뷰에 등록된 멤버가 존재하지 않습니다."));
                dto.setMemberName(member.getMemberName()); //닉네임
                dto.setMemberImage(member.getImgName()); //프로필 이미지
                Space space = spaceRepository.findById(review.getSpaceId())
                        .orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"리뷰에 등록된 공간이 존재하지 않습니다."));
                dto.setType(space.getType());
                dto.setSpaceName(space.getSpaceName());
                reviews.add(dto);
            }
        }

        return reviews;
    }

    // 리뷰 등록
    public void reviewRegister(ReviewDto requestDto, Long memberId) {
        log.info("requestDto:{}",requestDto);
        Review review = new Review(requestDto, memberId);
        reviewRepository.save(review);
    }

    // 리뷰 수정
    public void reviewUpdate(ReviewDto requestDto) {
        log.info("requestDto:{}",requestDto);
        Review review = reviewRepository.findById(requestDto.getReviewId())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"해당하는 리뷰가 존재하지 않습니다."));
        review.setRating(requestDto.getRating());
        review.setContent(requestDto.getContent());
        reviewRepository.save(review);
        log.info("review:{}",review);
    }

    // 리뷰 삭제
    public void reviewDelete(Long reviewId) {
        log.info("reviewId:{}",reviewId);
        reviewRepository.deleteById(reviewId);
    }



}
