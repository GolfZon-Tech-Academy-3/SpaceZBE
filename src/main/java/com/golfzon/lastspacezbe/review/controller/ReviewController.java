package com.golfzon.lastspacezbe.review.controller;

import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.review.dto.ReviewDto;
import com.golfzon.lastspacezbe.review.service.ReviewService;
import com.golfzon.lastspacezbe.security.UserDetailsImpl;
import com.golfzon.lastspacezbe.space.dto.SpaceRequestDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Api(tags = "Review 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    @ApiOperation(value = "리뷰 조회", notes = "업체 상세페이지에서 리뷰 보기입니다.")
    @GetMapping(value = "/total/{companyId}")
    public ResponseEntity<List<ReviewDto>> spaceReviews(@PathVariable(name="companyId") Long companyId){
        return ResponseEntity.ok()
                .body(reviewService.spaceReviews(companyId));
    }

    @ApiOperation(value = "리뷰 등록", notes = "마이페이지에서 리뷰 등록기능입니다.")
    @PostMapping(value = "/post")
    public ResponseEntity<String> review(@RequestBody ReviewDto requestDto) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("principal:{}", principal);
        Member member = ((UserDetailsImpl) principal).getMember();
        log.info("member?:{}", member);

        // 리뷰 등록 service
        reviewService.reviewRegister(requestDto, member.getMemberId());

        return ResponseEntity.ok()
                .body("result : 리뷰 등록완료");
    }

    // 리뷰 수정
    @PutMapping("/update")
    public ResponseEntity<String> reviewUpdate(
            @RequestBody ReviewDto requestDto) {

        // 리뷰 삭제 service
        reviewService.reviewUpdate(requestDto);

        return ResponseEntity.ok()
                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body("result : 리뷰 수정완료");
    }

    // 리뷰 삭제
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<String> reviewDelete(
            @PathVariable(name="reviewId") Long reviewId) {

        reviewService.reviewDelete(reviewId);

        return ResponseEntity.ok()
                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body("result : 리뷰 삭제완료");
    }
}
