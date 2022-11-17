package com.golfzon.lastspacezbe.reservation.controller;

import com.golfzon.lastspacezbe.reservation.dto.ReservationRequestDto;
import com.golfzon.lastspacezbe.reservation.service.ReservationService;
import com.golfzon.lastspacezbe.security.UserDetailsImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Api(tags = "예약 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    // 예약하기
    @ApiOperation(value = "예약", notes = "예약 처리입니다.")
    @PostMapping("/post")
    public ResponseEntity<String> reserve(
            @RequestBody ReservationRequestDto requestDto) {

         reservationService.reserve(requestDto);
//         log.info("result : {}",result);
         return ResponseEntity.ok()
                .body("result : 예약완료");
    }
    // 예약 취소하기
    @ApiOperation(value = "예약취소", notes = "예약취소 처리입니다.")
    @PutMapping("/cancel")
    public ResponseEntity<String> cancel(
            @RequestParam Long reservationId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        reservationService.cancel(reservationId);

        return ResponseEntity.ok()
                .body("result : 예약취소 완료");
    }

//    //휴대폰 인증
//    @GetMapping("/test")
//    public String test(){
//
//        return "test";
//    }

}

