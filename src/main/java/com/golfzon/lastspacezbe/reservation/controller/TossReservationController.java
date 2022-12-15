package com.golfzon.lastspacezbe.reservation.controller;

import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.payment.service.TossPaymentService;
import com.golfzon.lastspacezbe.reservation.dto.ReservationRequestDto;
import com.golfzon.lastspacezbe.reservation.service.ReservationService;
import com.golfzon.lastspacezbe.security.UserDetailsImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "토스예약 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class TossReservationController {

    private final ReservationService reservationService;
    private final TossPaymentService tossReservationService;


    // 토스예약하기
    @ApiOperation(value = "토스예약", notes = "토스예약 처리입니다.")
    @PostMapping(value = "/toss-post")
    public ResponseEntity<String> tossReserve(
            @RequestBody ReservationRequestDto requestDto){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("principal:{}",principal);
        Member member = ((UserDetailsImpl)principal).getMember();
        log.info("member?:{}",member);

         tossReservationService.tossReserve(requestDto, member);
         return ResponseEntity.ok()
                .body("result : 예약완료");
    }
}

