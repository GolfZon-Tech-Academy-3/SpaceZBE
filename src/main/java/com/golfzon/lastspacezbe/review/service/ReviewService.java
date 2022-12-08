package com.golfzon.lastspacezbe.review.service;

import com.golfzon.lastspacezbe.company.entity.Company;
import com.golfzon.lastspacezbe.company.repository.CompanyRepository;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.member.repository.MemberRepository;
import com.golfzon.lastspacezbe.review.dto.ReviewDto;
import com.golfzon.lastspacezbe.review.entity.Review;
import com.golfzon.lastspacezbe.review.repository.ReviewRepository;
import com.golfzon.lastspacezbe.space.entity.Space;
import com.golfzon.lastspacezbe.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final SpaceRepository spaceRepository;
    private final CompanyRepository companyRepository;

    // 업체에 등록된 리뷰 가져오기
    public Map<String, Object> spaceReviews(Long companyId, int page) {
        log.info("companyId:{}",companyId);

        Map<String, Object> map = new HashMap<>();

        Pageable pageable = PageRequest.of(page -1, 5);
        List<Review> reviewList = reviewRepository.findAllByCompanyIdOrderByReviewTimeDesc(pageable, companyId);
        map.put("totalCount", reviewList.size()); // 총 리뷰 개수
        List<ReviewDto> reviews = new ArrayList<>();

        if(reviewList.size()>0){
            for (Review review:reviewList) {
                log.info("review:{}",review);
                ReviewDto dto = new ReviewDto();
                dto.setReviewId(review.getReviewId()); //리뷰번호
                dto.setCompanyId(review.getCompanyId()); //업체번호
                dto.setSpaceId(review.getSpaceId()); //공간번호
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
        map.put("reviews",reviews);
        return map;
    }

    // 리뷰 등록
    public void reviewRegister(ReviewDto requestDto, Long memberId) {
        log.info("requestDto:{}",requestDto);
        Review review = new Review(requestDto, memberId);
        reviewRepository.save(review);

        updateReviewAvg(requestDto.getCompanyId());
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

        updateReviewAvg(requestDto.getCompanyId());
    }

    // 리뷰 삭제
    public void reviewDelete(Long reviewId) {
        log.info("reviewId:{}",reviewId);
        Review review = reviewRepository.findByReviewId(reviewId);
        reviewRepository.deleteById(reviewId);

        updateReviewAvg(review.getCompanyId());
    }

    // company review avg 변경
    public void updateReviewAvg(Long companyId){
        // company review avg 변경
        double avg = 0;
        List<Review> reviews = reviewRepository.findAllByCompanyId(companyId);
        if (!reviews.isEmpty()) {
            double sum = 0;
            for (Review r : reviews) {
                sum += r.getRating();
            }
            avg = (Math.floor(sum / reviews.size() * 10) / 10);
        }
        log.info("avg:{}",avg);
        Company company = companyRepository.findByCompanyId(companyId);
        company.setReviewAvg(avg);
        companyRepository.save(company);
    }

}
